package com.apiomat.frontend.offline;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.apiomat.frontend.AbstractClientDataModel;
import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.Status;
import com.jakewharton.disklrucache.DiskLruCache;
import rpc.json.me.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * Copyright (c) 2011-2013, Apinauten GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THIS FILE IS GENERATED AUTOMATICALLY. DON'T MODIFY IT.
 */
public class AOMOfflineHandler
{
    public static final String TASKS_KEY = "tasks";
    public static final String HREFMAP_KEY = "hrefmap_keys";

    private static final long MAX_CACHE_SIZE_BYTE = 20000;
    public static String TAG = "AOMOfflineHandler";
    private static DiskLruCache fileCache;
    private final AOMNetworkHandler.AOMNetworkListener networkListener;
    Object connectLock = new Object();
    /* This queue hold all POST/PUT/DELETE tasks */
    ConcurrentLinkedQueue<AOMOfflineInfo> tasks = new ConcurrentLinkedQueue<AOMOfflineInfo>();
    /* This queue holds all GET queries URL->AOMOfflineInfo */
    Map<String, AOMOfflineInfo> savedQueries = new HashMap<String, AOMOfflineInfo>();
    /* Map which maps localID -> HREF */
    Map<String, String> mapIdToHref= new HashMap<String, String>();
    /* maps localID -> Reference to real model */
    Map<String, AbstractClientDataModel> mapIdToObj= new HashMap<String, AbstractClientDataModel>();
    private Set<AOMOfflineListener> listeners = new HashSet<AOMOfflineListener>();
    private boolean isConnected = true;
    private boolean isWorking = false;
    private SecureRandom random = new SecureRandom();

    public AOMOfflineHandler(Context _context) throws RuntimeException
    {
       String errorMsg = "Can't get application context. Please set it on Datastore.getInstance().setOfflineStrategy(...)"; 
       if(_context == null)
       {
           Log.e(TAG, errorMsg);
           throw new RuntimeException(errorMsg);
       }

       if(_context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != PackageManager.PERMISSION_GRANTED)
       {
           errorMsg = "Can't get access to network state! Please set permission android.permission.ACCESS_NETWORK_STATE in your AndroidManifest.xml";
           Log.e(TAG, errorMsg);
           networkListener = null;
           throw new RuntimeException(errorMsg);
       }

        /* Initialize disk cache */
        try
        {
            fileCache = DiskLruCache.open(_context.getDir("apiomat", Context.MODE_PRIVATE),1, 1, MAX_CACHE_SIZE_BYTE );
        }
        catch (IOException e) 
        {
           errorMsg = "Can't access cache directory. Please make sure you have correct rights to write on internal disk";
           Log.e(TAG, errorMsg);
           throw new RuntimeException(errorMsg);
        }
        
        /* Check if we've persisted tasks and get them */
        try 
        {
            DiskLruCache.Snapshot cacheEntry = fileCache.get(TASKS_KEY);
            if(cacheEntry != null)
            {
                ObjectInputStream objIS = new ObjectInputStream(cacheEntry.getInputStream(0));
                tasks = (ConcurrentLinkedQueue<AOMOfflineInfo>) objIS.readObject();
                objIS.close();
                cacheEntry.close();
            }
        } 
        catch (Exception e) 
        {
            Log.e(TAG, "Can't get tasks list back from disc cache: " + e.getMessage());
            tasks = new ConcurrentLinkedQueue<AOMOfflineInfo>();
        }

        /* Check for persisted href map */
        try
        {
            DiskLruCache.Snapshot cacheEntry = fileCache.get(HREFMAP_KEY);
            if(cacheEntry != null)
            {
                ObjectInputStream objIS = new ObjectInputStream(cacheEntry.getInputStream(0));
                mapIdToHref = (Map<String, String>) objIS.readObject();
                objIS.close();
                cacheEntry.close();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Can't get hrefmap  back from disc cache: " + e.getMessage());
            mapIdToHref = new HashMap<String, String>();
        }

        networkListener = new AOMNetworkHandler.AOMNetworkListener() {
            @Override
            public void networkStateChanged(boolean _isConnected) {
                System.out.println("Is connected: " + _isConnected);
                setConnected(_isConnected);
                if(isConnected())
                {
                    sendTasks();
                }
            }
        };

        AOMNetworkHandler.getInstance().addListener(networkListener, _context);
        setConnected(AOMNetworkHandler.getInstance().isConnected(_context));
        /* check for open tasks */
        sendTasks();
    }

    public void addListener(AOMOfflineListener _listener)
    {
        this.listeners.add(_listener);
    }

    public void removeListener(AOMOfflineListener _listener)
    {
        this.listeners.remove(_listener);
    }

    /**
     * This method will go through the queue and sends saved data to backend
     */
    private void sendTasks()
    {
        /* how we prevent running twice time */
        AsyncTask<Void, Void, Void> worker = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                isWorking = true;
                while(isConnected() && tasks.size() > 0)
                {
                    AOMOfflineInfo task = tasks.poll();
                    System.out.println("Size of tasks " + tasks.size());
                  
                    if(task != null)
                    {
                        try 
                        {
                            /* also write to persisted list of tasks */
                            writeInfosToCache();

                            final boolean isStaticData = task.getClazz() == null;
                            AbstractClientDataModel tmpModel = null;
                            byte[] staticData = null;

                            DiskLruCache.Snapshot cacheEntry = fileCache.get(task.getFileKey());

                            if(cacheEntry != null)
                            {
                                if(isStaticData)
                                {
                                    InputStream in = null;
                                    ByteArrayOutputStream out = null;
                                    try
                                    {
                                        /* Copy cached inputstream to bytes */
                                        in = cacheEntry.getInputStream(0);
                                        out = new ByteArrayOutputStream();
                                        byte[] buffer = new byte[1024];
                                        int length;

                                        while ((length = in.read(buffer)) != -1)
                                        {
                                            out.write(buffer, 0, length);
                                        }
                                        staticData = out.toByteArray();
                                    }
                                    finally 
                                    {
                                        if(in != null)
                                        {
                                            in.close();
                                        }
                                        if(out != null)
                                        {
                                            out.close();
                                        }
                                    }
                                }
                                else
                                {
                                    /* generate new ACDM */
                                    tmpModel = (AbstractClientDataModel) task.getClazz().newInstance();
                                    /* Get JSON from disc cache */
                                    tmpModel.fromJson(cacheEntry.getString(0));
                                }
                            
                                /* remove file from cache */
                                cacheEntry.close();
                                fileCache.remove(task.getFileKey());

                                if(tmpModel != null)
                                {
                                    sendModeltoServer(task, tmpModel);
                                }
                                else if(staticData != null)
                                {
                                    sendStaticDataToServer(task, staticData);
                                }
                            }
                            else
                            {
                                Log.e(TAG, "Can't find persisted model. Maybe cache size was exceeded and model was deleted");
                                throw new ApiomatRequestException(com.apiomat.frontend.Status.CANT_WRITE_IN_CACHE);
                            }
                        } 
                        catch (Exception e) 
                        {
                            Log.e(TAG, "Can't update model: " + e.getMessage());
                            informListeners(task, null, new ApiomatRequestException(com.apiomat.frontend.Status.HREF_NOT_FOUND));
                        }
                    }
                }
                isWorking = false;
                return null;
            }

            private void sendStaticDataToServer(AOMOfflineInfo task, byte[] data)
            {
                try
                {
                    if(task.getHttpMethod().equals("POST"))
                    {
                        String href = Datastore.getInstance().postStaticDataOnServer(data, true);
                        if(href != null && href.length() > 0)
                        {
                            mapIdToHref.put(task.getLocalId(), href);
                            writeHrefMapToCache();
                            informListeners(task, task.getLocalId(), null);
                        }
                        else
                        {
                            throw new ApiomatRequestException(com.apiomat.frontend.Status.HREF_NOT_FOUND);
                        }
                    }
                    else if(task.getHttpMethod().equals("DELETE"))
                    {
                        Datastore.getInstance().deleteOnServer(task.getUrl());
                    }
                }
                catch (ApiomatRequestException e)
                {
                    Log.e(TAG, "Can't delete or save static data: " + e.getMessage());
                    informListeners(task, task.getLocalId(), e);
                }
            }

            private void sendModeltoServer(AOMOfflineInfo task, AbstractClientDataModel tmpModel) 
            {
                /* check httpMethod and decide if we have to call post/put/delete on datastore */
                if(task.getHttpMethod().equals("POST"))
                {
                    try {
                        String url = task.getUrl();
                        /* seems to be a reference */
                        if(task.getRefName() != null && task.getRefName().length() > 0)
                        {
                            String parentID = url.substring(url.lastIndexOf("/") + 1);
                            /* add correct href to referenced model */
                            getHref(tmpModel);
                            url = mapIdToHref.get(parentID) + "/" + task.getRefName();
                        }
                        if(url != null)
                        {
                            final String href =  Datastore.getInstance().postOnServer(tmpModel, url);
                            /* inform listeners */
                            if(href != null && href.length() > 0)
                            {
                                mapIdToHref.put(task.getLocalId(), href);
                                writeHrefMapToCache();
                                /* update reference object if there */
                                updateRealModel(task.getLocalId(), href);
                                informListeners(task, task.getLocalId(), null);
                            }
                            else
                            {
                                ApiomatRequestException e =  new ApiomatRequestException(com.apiomat.frontend.Status.HREF_NOT_FOUND);
                                Log.e(TAG, "Can't save model: " + e.getMessage());
                                informListeners(task, task.getLocalId(), e);
                            }
                        }
                        else
                        {
                            ApiomatRequestException e =  new ApiomatRequestException(com.apiomat.frontend.Status.HREF_NOT_FOUND);
                            Log.e(TAG, "Can't save model: " + e.getMessage());
                            informListeners(task, task.getLocalId(), e);
                        }
                    }
                    catch (ApiomatRequestException e)
                    {
                        Log.e(TAG, "Can't save model: " + e.getMessage());
                        informListeners(task, task.getLocalId(), e);
                    }
                    finally
                    {
                        tmpModel = null;
                    }
                }
                else if (task.getHttpMethod().equals("PUT"))
                {
                    String href = getHref(tmpModel);
                    if(href != null && href.length() > 0)
                    {
                        try {
                            Datastore.getInstance().updateOnServer(tmpModel);
                            informListeners(task,href, null);
                        } catch (ApiomatRequestException e) {
                            Log.e(TAG, "Can't update model: " + e.getMessage());
                            informListeners(task, href, e);
                        }
                    }
                    else
                    {
                        ApiomatRequestException e =  new ApiomatRequestException(com.apiomat.frontend.Status.HREF_NOT_FOUND);
                        Log.e(TAG, "Can't save model: " + e.getMessage());
                        informListeners(task, task.getLocalId(), e);
                    }
                }
                else if(task.getHttpMethod().equals("DELETE"))
                {
                    String href = getHref(tmpModel);
                    final boolean isRef =task.getRefName() != null && task.getRefName().length() > 0;
                        /* seems to be a reference */
                    if(isRef)
                    {
                        String parentID = task.getUrl().substring(task.getUrl().lastIndexOf("/") + 1);
                        /* add correct href to referenced model */
                        href = mapIdToHref.get(parentID) + "/" + task.getRefName() + "/" + tmpModel.getHref().substring(tmpModel.getHref().lastIndexOf("/") + 1);
                    }
                    if(href != null  && href.length() > 0)
                    {
                            try {
                                if(isRef)
                                {
                                    Datastore.getInstance().deleteOnServer(href);
                                }
                                else
                                {
                                    Datastore.getInstance().deleteOnServer(tmpModel);
                                }
                                informListeners(task,href, null);
                            } catch (ApiomatRequestException e) {
                                Log.e(TAG, "Can't delete model: " + e.getMessage());
                                informListeners(task, href, e);
                            }
                    }
                    else
                    {
                        informListeners(task, task.getLocalId(), new ApiomatRequestException(com.apiomat.frontend.Status.HREF_NOT_FOUND));
                    }
                }
            }
        };

        synchronized (connectLock)
        {
            worker.execute();
        }
    }

    /**
     * Add a new task tu request queue and return temp HREf aka local href
     *
     * @param _httpMethod
     * @param _url
     * @param _content
     * @return
     */
    public String addTask(final String _httpMethod, final String _url, final byte[] _content)
    {
        String returnedUri = _url;
        String localId = null;
        /* decide if we need new localId */
        if(_httpMethod.equals("POST"))
        {
            localId = createNewLocalId();
            returnedUri += "/"  + localId;
        }
        try
        {
            final String fileKey = _httpMethod.toLowerCase() + "_" + System.currentTimeMillis();
            DiskLruCache.Editor creator = fileCache.edit(fileKey);
            OutputStream out =  creator.newOutputStream(0);
            out.write(_content);
            creator.commit();
            out.close();
            
            final AOMOfflineInfo info = new AOMOfflineInfo(_httpMethod, _url, fileKey, null, localId);
            tasks.add(info);
            /* also commit task to disc cache */
            writeInfosToCache();

        }
        catch (IOException e)
        {
            Log.e(TAG, "Can't add task to disc cache: " + e.getMessage());
        }

        return returnedUri;
    }

    public String addTask(final String _httpMethod, final String _url, final AbstractClientDataModel _dataModel, final String _parentHref)
    {
        String returnedUri = _url;
        String localId = null;
        /* decide if we need new localId */
        if(_httpMethod.equals("POST"))
        {
            localId = createNewLocalId();
            returnedUri += "/"  + localId;
            mapIdToObj.put(localId, _dataModel);
        }
        try 
        {
            final String fileKey = _httpMethod.toLowerCase() + "_" + System.currentTimeMillis() + "_" + ( localId != null?localId:createNewLocalId() );
            DiskLruCache.Editor creator = fileCache.edit(fileKey);
            creator.set(0, _dataModel.toJson());
            creator.commit();
            final AOMOfflineInfo info = new AOMOfflineInfo(_httpMethod, _url, fileKey, _dataModel.getClass(), localId, _parentHref);
            tasks.add(info);
            /* also commit tasks to disc cache */
            writeInfosToCache();
            
        } 
        catch (IOException e) 
        {
            Log.e(TAG, "Can't add task to disc cache: " + e.getMessage());
        } 
        return returnedUri;
    }

    public void addTask(final String _httpMethod, final String _url)
    {
        addTask(_httpMethod, _url, null, null);
    }

    public synchronized String createNewLocalId()
    {
        return new BigInteger(130, random).toString(32);
    }

    /**
     * Writes waiting tasks to disc cache
     *
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    private void writeInfosToCache()
    {
        writeObjToCache(TASKS_KEY, this.tasks);
    }

    /**
     * Writes mapped hrefs to disc cache
     *
     * @throws java.io.IOException
     */
    private void writeHrefMapToCache()
    {
        writeObjToCache(HREFMAP_KEY, this.mapIdToHref);
    }

    /**
     * Writes given object to disc cache
     *
     * @throws java.io.IOException
     */
    private void writeObjToCache(final String key, final Object object)
    {
        try
        {
            DiskLruCache.Editor creator = fileCache.edit(key);
            ObjectOutputStream objectOS = new ObjectOutputStream(creator.newOutputStream(0));
            objectOS.writeObject(object);
            creator.commit();
            objectOS.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Can't serialize '" + object.getClass() + "': " + e.getMessage());
        }
    }

    private void informListeners(final AOMOfflineInfo _info, final String _href, final Throwable  _exception)
    {
        for (AOMOfflineListener listener : this.listeners)
        {
            /* it seems that all want ok */
            if(_exception == null)
            {
                listener.onTaskExecuted(_info, _href);
            }
            else
            {
                listener.onTaskExecutionError(_info, _href, _exception);
            }
        }
    }

    private String getHref(AbstractClientDataModel tmpModel) {
        String href = tmpModel.getHref();
        if(tmpModel.isOffline())
        {

            /* check if there also localHREFs for files/images in model */
            String jsonStr = tmpModel.toJson();
            JSONObject jsonRep = new JSONObject(jsonStr);
            Enumeration<String> jsonKeys = jsonRep.keys();
            boolean wasUpdateFound = false;
            while (jsonKeys.hasMoreElements())
            {
                String jsonKey = jsonKeys.nextElement();
                /* we only check properties with URL at the end */
                if(jsonKey.endsWith("URL"))
                {
                    String jsonObj = jsonRep.getString(jsonKey);
                    if(jsonObj != null)
                    {
                       String realHref = getHrefForLocalHref(jsonObj);
                       if(realHref != null)
                       {
                           jsonRep.put(jsonKey, realHref);
                           wasUpdateFound = true;
                       }
                    }
                }
            }
            /* if we updated a field than update model */
            if(wasUpdateFound)
            {
                tmpModel.fromJson(jsonRep);
            }

            final String id = tmpModel.getID();
            if(id != null && id.length() > 0)
            {
                href = mapIdToHref.get(id);
                href = injectHref(tmpModel, href);
            }
            else
            {
                Log.e(TAG, "No local ID found");
            }

        }
        return href;
    }

    /**
     * Returns "real" href for given local href, if exists otherwise false
     *
     * @return "real" href or null if not found in list
     */
    private String getHrefForLocalHref(String _localHref)
    {
        String id = _localHref.substring( _localHref.lastIndexOf("/") + 1 );
        return mapIdToHref.get(id);
    }

    private String injectHref(AbstractClientDataModel tmpModel, String href) {
    /* inject server href */
        Field f = null;
        try
        {
            f = AbstractClientDataModel.class.getDeclaredField("href");
            f.setAccessible(true);
            f.set(tmpModel, href);
            f.setAccessible(false);
        }
        catch (Exception e)
        {
           Log.e(TAG, "Can't inject href");
           href = null;
        }
        return href;
    }

    /**
     * This method updates the reference model with a the new href from server
     *
     * @param _localId
     * @param _href
     */
    private void updateRealModel(final String _localId, final String _href)
    {
        System.out.println("Size: " + mapIdToObj.size());
        AbstractClientDataModel model = mapIdToObj.remove(_localId);
        if(model != null && model.isOffline())
        {
            System.out.println( "LocalID is " + _localId);
            /* inject new HREF to model */
            injectHref(model, _href);
            model.setOffline(false);
        }
    }

    public boolean isConnected()
    {
        return isConnected;
    }
    
    protected void setConnected(boolean connected)
    {
        synchronized (connectLock)
        {
            isConnected = connected;
        }
    }

    /**
     * This method removes all waiting tasks from queue and also the cached data from disc
     */
    public void clearCache()  {
        tasks.clear();
        mapIdToHref.clear();
        /* remove also persisted elements */
        try
        {
            DiskLruCache.Snapshot cacheEntry = fileCache.get(TASKS_KEY);
            if(cacheEntry != null)
            {
                ObjectInputStream in = new ObjectInputStream(cacheEntry.getInputStream(0));
                ConcurrentLinkedQueue<AOMOfflineInfo> cachedTasks = (ConcurrentLinkedQueue<AOMOfflineInfo>) in.readObject();
                if(cachedTasks != null)
                {
                    for (AOMOfflineInfo task: cachedTasks)
                    {
                        /* remove serialized JSON from cache */
                        try
                        {
                            fileCache.remove(task.getFileKey());
                        }
                        catch(IOException e)
                        {
                            Log.e(TAG, "Can't remove cache entry for task  '" +task.toString() + "'" + e.getMessage());
                        }
                    }
                }
                cacheEntry.close();
                fileCache.remove(TASKS_KEY);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Can't clear cache: " + e.getMessage());
        }

        try
        {
            fileCache.remove(HREFMAP_KEY);
        } catch (IOException e)
        {
            Log.e(TAG, "Can't remove href map: " + e.getMessage());
        }
    }

    public interface AOMOfflineListener
    {
        public void onTaskExecuted(final AOMOfflineInfo _offlineObj, final String _href);

        public void onTaskExecutionError(final AOMOfflineInfo _offlineObj, final String _href, final Throwable _exception);
    }
}
