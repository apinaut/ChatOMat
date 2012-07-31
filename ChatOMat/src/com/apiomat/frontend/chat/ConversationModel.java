/*
 * Copyright (c) 2012, Apinauten UG (haftungsbeschraenkt)
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

import rpc.json.me.*;

/**
* Generated class for your ConversationModel data model 
*/
public class ConversationModel extends AbstractClientDataModel
{

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
		return "";
	}

	/**
	* Returns a list of objects of this class filtered by the given query from server
	* @query a query filtering the results in SQL style (@see <a href="https://apiomat.com/apidocs/index.html">API documentation</a> of ModelRestResource in Dashboard)
	*/
	public static final List<ConversationModel> getConversationModels( String query ) throws Exception
	{
		ConversationModel o = new ConversationModel();
		return Datastore.getInstance( ).loadFromServer( ConversationModel.class, o.getModuleName( ),
			o.getSimpleName( ), query );
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
	public List<MessageModel> loadMessages( String query ) throws Exception
	{
		final String refUrl = this.data.optString( "messagesHref" );
		if( refUrl==null || refUrl.length()==0 )
		{
			return new ArrayList<MessageModel>();
		}
		return Datastore.getInstance( ).loadFromServer( MessageModel.class, refUrl, query );
	}
	
	public String postMessages( MessageModel refData ) throws Exception
	{
		refData.save();
		Datastore.getInstance( ).postOnServer(refData, this.data.optString("messagesHref"));
		return refData.getHref();
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

}
