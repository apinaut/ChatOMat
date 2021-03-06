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
package com.apiomat.frontend.chat;

import java.util.*;
import com.apiomat.frontend.*;
import com.apiomat.frontend.basics.*;
import com.apiomat.frontend.callbacks.*;
import com.apiomat.frontend.helper.*;

import rpc.json.me.*;


/**
* Generated class for your ChatMessageModel data model 
*/
public class ChatMessageModel extends AbstractClientDataModel
{
    private User sender = null;
    /**
    * Default constructor. Needed for internal processing.
    */
    public ChatMessageModel ( )
    {
        super( );
    }

    /**
    * Returns the simple name of this class 
    */
    public String getSimpleName( )
    {
        return "ChatMessageModel";
    }

    /**
    * Returns the name of the module where this class belongs to
    */
    public String getModuleName( )
    {
        return "Chat";
    }
    
    /**
    * Returns the system to connect to
    */
    public String getSystem( )
    {

        return User.system;
    }

    /**
    * Returns a list of objects of this class filtered by the given query from server
    * @query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
    */
    public static final List<ChatMessageModel> getChatMessageModels( String query ) throws ApiomatRequestException
    {
        ChatMessageModel o = new ChatMessageModel();
        return Datastore.getInstance( ).loadFromServer( ChatMessageModel.class, o.getModuleName( ),
            o.getSimpleName( ), query );
    }
    
    /**
     * Get a list of objects of this class filtered by the given query from server
     * This method works in the background and call the callback function when finished
     *
     * @param query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
     * @param listAOMCallback The callback method which will called when request is finished
     */
    public static void getChatMessageModelsAsync(final String query, final AOMCallback<List<ChatMessageModel>> listAOMCallback) 
    {
       getChatMessageModelsAsync(query, false, listAOMCallback);
    }
    
    /**
    * Returns a list of objects of this class filtered by the given query from server
    *
    * @query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
    * @param withReferencedHrefs set to true to get also all HREFs of referenced models
    */
    public static final List<ChatMessageModel> getChatMessageModels( String query, boolean withReferencedHrefs ) throws Exception
    {
        ChatMessageModel o = new ChatMessageModel();
        return Datastore.getInstance( ).loadFromServer( ChatMessageModel.class, o.getModuleName( ),
            o.getSimpleName( ), withReferencedHrefs, query);
    }
    
    /**
     * Get a list of objects of this class filtered by the given query from server
     * This method works in the background and call the callback function when finished
     *
     * @param query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
     * @param withReferencedHrefs set true to get also all HREFs of referenced models
     * @param listAOMCallback The callback method which will called when request is finished
     */
    public static void getChatMessageModelsAsync(final String query, final boolean withReferencedHrefs, final AOMCallback<List<ChatMessageModel>> listAOMCallback) 
    {
         ChatMessageModel o = new  ChatMessageModel();
        Datastore.getInstance().loadFromServerAsync(ChatMessageModel.class,o.getModuleName(), o.getSimpleName(), withReferencedHrefs, query, listAOMCallback);
    }


    
    public User loadSender( ) throws Exception
    {
        final String refUrl = this.data.optString("senderHref" );
        sender = Datastore.getInstance( ).loadFromServer( User.class, refUrl);
        return sender;
    }
    
    /**
    * Getter for local linked variable
    */
    public User getSender() 
    {
        return sender;
    }
    /** 
    * Load referenced object(s) on a background thread and
    * add result from server to member variable of this class.
    * 
    *     * @param callback callback method which will called after request is finished
    *
    */
    public void loadSenderAsync( final AOMEmptyCallback callback ) {
        final String refUrl = this.data.optString("senderHref");
        if (refUrl == null || refUrl.length() == 0) {
            if(callback != null) 
            {
                callback.isDone(new ApiomatRequestException(Status.HREF_NOT_FOUND));
            }
            else
            {
                System.err.println("Error occured: " + Status.HREF_NOT_FOUND.getReasonPhrase());  
            }
            return;
        }
        
        AOMCallback<User> cb = new AOMCallback<User>() {
            @Override
            public void isDone(User result,
                               ApiomatRequestException ex) {
                if (ex == null) {
                    sender = result;
                }
                if(callback != null) 
                {
                    callback.isDone(ex);
                }
                else
                {
                    if(ex != null)
                    {
                        System.err.println("Error occured: " + ex.getMessage());
                    }
                }
            }
        };
        Datastore.getInstance().loadFromServerAsync(User.class, refUrl, cb);
    }

    public String postSender( User refData ) throws ApiomatRequestException
    {
        String href = refData.getHref();
        if(href == null || href.length() < 1) 
        {
            throw new ApiomatRequestException(Status.SAVE_REFERENECE_BEFORE_REFERENCING);
        }
        
        String refHref = null;
        /* Let's check if we use offline storage or send req to server */
        if(Datastore.getInstance().sendOffline("POST"))
        {
            refHref = Datastore.getInstance().getOfflineHandler().addTask("POST", getHref(), refData, "sender" );
        } 
        else
        {
            refHref = Datastore.getInstance( ).postOnServer(refData, this.data.optString("senderHref"));
        }
        
        if(refHref!=null && refHref.length()>0)
        {
            sender = refData;
        }
        return href;
    }
    
    public void postSenderAsync(final User refData, final AOMEmptyCallback callback ) {
        String href = refData.getHref();
        if(href == null || href.length() < 1)
        {
            if(callback != null)
            {
                callback.isDone(new ApiomatRequestException(Status.SAVE_REFERENECE_BEFORE_REFERENCING));
            }
            else
            {
                System.err.println("Error occured: " + Status.SAVE_REFERENECE_BEFORE_REFERENCING.getReasonPhrase());
            }
            return;
        }
         /* check if we've use offline storage */
        if(Datastore.getInstance().sendOffline("POST"))
        {
            final String refHref = Datastore.getInstance().getOfflineHandler().addTask("POST", getHref(), refData, "sender" );
            /* check if local list contains refData with same href */
            sender = refData;
            if(callback != null)
            {
                callback.isDone(null);
            }
        }
        else
        {
            AOMCallback<String> cb = new AOMCallback<String>() {
                @Override
                public void isDone(String refHref, ApiomatRequestException ex) {
                    if(ex == null && refHref!=null && refHref.length()>0)
                    {
                        sender = refData;
                    }
                    if(callback != null)
                    {
                        callback.isDone(ex);
                    }
                    else
                    {
                        System.err.println("Exception was thrown: " + ex.getMessage());
                    }
                }
            };
            Datastore.getInstance( ).postOnServerAsync(refData, this.data.optString("senderHref"), cb);
        }
    }
    
    public void removeSender( User refData ) throws Exception
    {
        final String id = refData.getHref( ).substring( refData.getHref( ).lastIndexOf( "/" ) + 1 );
        if(Datastore.getInstance().sendOffline("DELETE"))
        {
            Datastore.getInstance().getOfflineHandler().addTask("DELETE", getHref(), refData, "sender" );
        }
        else
        {
            Datastore.getInstance( ).deleteOnServer( this.data.optString("senderHref") + "/" + id);
        }
            sender = null;
    }
    
    public void removeSenderAsync( final User refData, final AOMEmptyCallback callback )
    {
        final String id = refData.getHref( ).substring( refData.getHref( ).lastIndexOf( "/" ) + 1 );
        if(Datastore.getInstance().sendOffline("DELETE"))
        {
            Datastore.getInstance().getOfflineHandler().addTask("DELETE", getHref(), refData, "sender");
            sender = null;
            if(callback != null)
            {
                callback.isDone(null);
            }
        }
        else
        {
            AOMEmptyCallback cb = new AOMEmptyCallback() {
                @Override
                public void isDone(ApiomatRequestException ex) {
                    if(ex == null) {
                        sender = null;
                    }
                    callback.isDone(ex);
                }
            };
            Datastore.getInstance( ).deleteOnServerAsync( this.data.optString("senderHref") + "/" + id, cb);
        }
    }


        public String getText()
    {
         return this.data.optString( "text" );
    }

    public void setText( String arg )
    {
        String text = arg;
        this.data.put( "text", text );
    }

        public String getSenderUserName()
    {
         return this.data.optString( "senderUserName" );
    }

    public void setSenderUserName( String arg )
    {
        String senderUserName = arg;
        this.data.put( "senderUserName", senderUserName );
    }

}
