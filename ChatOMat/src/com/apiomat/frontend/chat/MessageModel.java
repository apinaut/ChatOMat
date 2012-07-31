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
* Generated class for your MessageModel data model 
*/
public class MessageModel extends AbstractClientDataModel
{

	/**
	* Default constructor. Needed for internal processing.
	*/
	public MessageModel ( )
	{
		super( );
	}

	/**
	* Returns the simple name of this class 
	*/
	public String getSimpleName( )
	{
		return "MessageModel";
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
	public static final List<MessageModel> getMessageModels( String query ) throws Exception
	{
		MessageModel o = new MessageModel();
		return Datastore.getInstance( ).loadFromServer( MessageModel.class, o.getModuleName( ),
			o.getSimpleName( ), query );
	}

	public MemberModel loadSender( ) throws Exception
	{
		final String refUrl = this.data.optString("senderHref" );
		return Datastore.getInstance( ).loadFromServer( MemberModel.class, refUrl);
	}
	
	public String postSender( MemberModel refData ) throws Exception
	{
		refData.save();
		Datastore.getInstance( ).postOnServer(refData, this.data.optString("senderHref"));
		return refData.getHref();
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

}
