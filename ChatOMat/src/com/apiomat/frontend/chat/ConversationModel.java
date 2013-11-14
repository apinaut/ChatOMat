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
* Generated class for your ConversationModel data model 
*/
public class ConversationModel extends AbstractClientDataModel
{
    private List<ChatMessageModel> messages = new ArrayList<ChatMessageModel>();
    /**
    * Default constructor. Needed for internal processing.
    */
    public ConversationModel ( )
    {
        super( );
    }

    /**
    * Returns the simple name of this class 
    */
    public String getSimpleName( )
    {
        return "ConversationModel";
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
        return MemberModel.system;
    }

    /**
    * Returns a list of objects of this class filtered by the given query from server
    * @query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
    */
    public static final List<ConversationModel> getConversationModels( String query ) throws Exception
    {
        ConversationModel o = new ConversationModel();
        return Datastore.getInstance( ).loadFromServer( ConversationModel.class, o.getModuleName( ),
            o.getSimpleName( ), query );
    }
    
    /**
     * Get a list of objects of this class filtered by the given query from server
     * This method works in the background and call the callback function when finished
     *
     * @param query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
     * @param listAOMCallback The callback method which will called when request is finished
     */
    public static void getConversationModelsAsync(final String query, final AOMCallback<List<ConversationModel>> listAOMCallback) 
    {
       getConversationModelsAsync(query, false, listAOMCallback);
    }
    
    /**
    * Returns a list of objects of this class filtered by the given query from server
    *
    * @query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
    * @param withReferencedHrefs set to true to get also all HREFs of referenced models
    */
    public static final List<ConversationModel> getConversationModels( String query, boolean withReferencedHrefs ) throws Exception
    {
        ConversationModel o = new ConversationModel();
        return Datastore.getInstance( ).loadFromServer( ConversationModel.class, o.getModuleName( ),
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
    public static void getConversationModelsAsync(final String query, final boolean withReferencedHrefs, final AOMCallback<List<ConversationModel>> listAOMCallback) 
    {
         ConversationModel o = new  ConversationModel();
        Datastore.getInstance().loadFromServerAsync(ConversationModel.class,o.getModuleName(), o.getSimpleName(), withReferencedHrefs, query, listAOMCallback);
    }

    public List<ChatMessageModel> loadMessages( String query ) throws Exception
    {
        final String refUrl = this.data.optString( "messagesHref" );
        if( refUrl==null || refUrl.length()==0 )
        {
            return messages;
        } 
        messages = Datastore.getInstance( ).loadFromServer( ChatMessageModel.class, refUrl, query );
        return messages;
    }
    
    /**
    * Getter for local linked variable
    */
    public List<ChatMessageModel> getMessages() 
    {
        return messages;
    }
    /** 
    * Load referenced object(s) on a background thread and
    * add result from server to member variable of this class.
    * 
    * @param query filter returned references by query    * @param callback callback method which will called after request is finished
    *
    */
    public void loadMessagesAsync(final String query, final AOMEmptyCallback callback ) {
        final String refUrl = this.data.optString("messagesHref");
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
        
        AOMCallback<List<ChatMessageModel>> cb = new AOMCallback<List<ChatMessageModel>>() {
            @Override
            public void isDone(List<ChatMessageModel> result,
                               ApiomatRequestException ex) {
                if (ex == null) {
                    messages.clear();
                    messages.addAll(result);
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
        Datastore.getInstance().loadFromServerAsync(ChatMessageModel.class, refUrl, query, cb);
    }

    public String postMessages( ChatMessageModel refData ) throws ApiomatRequestException
    {
        String href = refData.getHref();
        if(href == null || href.length() < 1) 
        {
            throw new ApiomatRequestException(Status.SAVE_REFERENECE_BEFORE_REFERENCING);
        }
        String refHref = Datastore.getInstance( ).postOnServer(refData, this.data.optString("messagesHref"));
        
        if(refHref!=null && refHref.length()>0)
        {
            //check if local list contains refData with same href
            if(ModelHelper.containsHref(messages, refHref)==false)
            {
                messages.add(refData);
            }
        }
        return href;
    }
    
    public void postMessagesAsync(final ChatMessageModel refData, final AOMEmptyCallback callback ) {
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
        AOMCallback<String> cb = new AOMCallback<String>() {
            @Override
            public void isDone(String refHref, ApiomatRequestException ex) {
                if(ex == null && refHref!=null && refHref.length()>0)
                {
                    //check if local list contains refData with same href
                    if(ModelHelper.containsHref(messages, refHref)==false)
                    {
                        messages.add(refData);
                    }
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
        Datastore.getInstance( ).postOnServerAsync(refData, this.data.optString("messagesHref"), cb);
    }
    
    public void removeMessages( ChatMessageModel refData ) throws Exception
    {
        final String id = refData.getHref( ).substring( refData.getHref( ).lastIndexOf( "/" ) + 1 );
        Datastore.getInstance( ).deleteOnServer( this.data.optString("messagesHref") + "/" + id);
        messages.remove(refData);
    }
    
    public void removeMessagesAsync( final ChatMessageModel refData, final AOMEmptyCallback callback )
    {
        final String id = refData.getHref( ).substring( refData.getHref( ).lastIndexOf( "/" ) + 1 );
        AOMEmptyCallback cb = new AOMEmptyCallback() {
            @Override
            public void isDone(ApiomatRequestException ex) {
                if(ex == null) {
                    messages.remove(refData);
                }
                callback.isDone(ex);
            }
        };
        Datastore.getInstance( ).deleteOnServerAsync( this.data.optString("messagesHref") + "/" + id, cb);
    }

    public String getSubject()
    {
         return this.data.optString( "subject" );
    }

    public void setSubject( String arg )
    {
        String subject = arg;
        this.data.put( "subject", subject );
    }
    public List getAttendeeUserNames()
    {
         JSONArray array = (JSONArray)this.data.opt( "attendeeUserNames" );
        return fromJSONArray(array);
    }

    public void setAttendeeUserNames( List arg )
    {
        Vector attendeeUserNames = toVector( arg);
        this.data.put( "attendeeUserNames", attendeeUserNames );
    }
}
