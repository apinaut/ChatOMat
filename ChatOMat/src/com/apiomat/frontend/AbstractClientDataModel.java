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
package com.apiomat.frontend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import rpc.json.me.JSONArray;
import rpc.json.me.JSONObject;

/**
 * This class defines the base class of all data models for frontend developers. All data is stored in a JSON data
 * object except the HREF of this model, originally containing the type of this model. 
 * 
 * @author andreasfey
 */
public abstract class AbstractClientDataModel implements Serializable
{
	/**
	 * The representation of the data of this model as JSON object
	 */
	protected JSONObject data;
	private String href;

	/**
	 * Constructor
	 */
	@SuppressWarnings( "rawtypes" )
	public AbstractClientDataModel( )
	{
		this.data = new JSONObject( );
		this.data.put( "@type", getType( ) );
		if ( this.getSimpleName( ).equals( "MemberModel" ) && getModuleName( ).equals( "Basics" ) )
		{
			this.data.put( "dynamicAttributes", new Hashtable( ) );
		}
	}

	/**
	 * Returns the unique type of this data model to get identified via REST interface
	 * 
	 * @return
	 */
	private String getType( )
	{
		return this.getModuleName( ) + "$" + this.getSimpleName( ) + "$" + this.getSystem( );
	}

	/**
	 * Returns the HREF of this data model
	 * 
	 * @return HREF of this data model, NULL if it was created but not saved yet
	 */
	public final String getHref( )
	{
		return this.href;
	}

	/**
	 * Returns the date when this object was created on server side
	 * 
	 * @return date when this object was created on server side, NULL if it was created but not saved yet
	 */
	public final Date getCreatedAt( )
	{
		return new Date( this.data.getLong( "createdAt" ) );
	}

	/**
	 * Returns the date when this object was modified last on server side
	 * 
	 * @return date when this object was modified last on server side, NULL if it was created but not saved yet
	 */
	public final Date getLastModifiedAt( )
	{
		return new Date( this.data.getLong( "lastModifiedAt" ) );
	}

	/**
	 * Returns the name of the app where this data model belongs to
	 * 
	 * @return name of the app where this data model belongs to
	 */
	public final String getAppName( )
	{
		return this.data.getString( "applicationName" );
	}

	/**
	 * Returns the simple data model name
	 * 
	 * @return simple data model name
	 */
	public abstract String getSimpleName( );

	/**
	 * Returns the module name where this data model belongs to
	 * 
	 * @return name of the module where this data model belongs to
	 */
	public abstract String getModuleName( );

	/**
	 * Returns the system to connect to
	 * 
	 * @return TEST for test system, LIVE for production
	 */
	public abstract String getSystem( );

	/**
	 * Decodes this data model from a JSON string; used to communicate with the REST interface
	 * 
	 * @param jsonData
	 */
	public final void fromJson( final String jsonData )
	{
		this.data = new JSONObject( jsonData );
		this.href = this.data.optString( "href" );
	}

	/**
	 * Decodes this data model from a JSON object; used to communicate with the REST interface
	 * 
	 * @param jsonData
	 */
	public final void fromJson( final JSONObject jsonData )
	{
		this.data = jsonData;
		this.href = this.data.optString( "href" );
	}

	/**
	 * Encodes this data model as a JSON string; used to communicate with the REST interface
	 * 
	 * @return this data model as JSON string
	 */
	public final String toJson( )
	{
		String json = "";
		if ( getHref( ) != null )
		{
			this.data.put( "id", getHref( ).substring( getHref( ).lastIndexOf( "/" ) + 1 ) );
			json = this.data.toString( );
			this.data.remove( "id" );
		}
		else
		{
			json = this.data.toString( );
		}
		return json;
	}

	/**
	 * Saves this data model. It is - based on HREF - automatically determined, if this model exists on the server,
	 * leading to an update, or not, leading to an post command.
	 * 
	 * @return TRUE if save operations was successful
	 */
	public final boolean save( ) throws Exception
	{
		boolean ret;

		if ( this.href == null )
		{
			final String location = Datastore.getInstance( ).postOnServer( this );
			this.href = location;
			ret = location != null;
		}
		else
		{
			ret = Datastore.getInstance( ).updateOnServer( this );
		}

		/* fetch server-side values */
		load( );

		return ret;
	}

	/**
	 * Loads (updates) this data model with server values
	 */
	public final void load( ) throws Exception
	{
		Datastore.getInstance( ).loadFromServer( this, this.getHref( ) );
	}

	/**
	 * Loads (updates) this data model with server values. Since you have to pass the HREF for this method, only use it
	 * when loading a model which has no HREF in it (was not sent/loaded before). Else use {@link #load()}
	 * 
	 * @param href The HREF of this model
	 */
	public final void load( final String href ) throws Exception
	{
		Datastore.getInstance( ).loadFromServer( this, href );
		this.href = this.data.optString( "href" , null);
	}

	/**
	 * Deletes this data model on server
	 */
	public final void delete( ) throws Exception
	{
		Datastore.getInstance( ).deleteOnServer( this );
	}

	/**
	 * Helper method to convert a JSON array to a list
	 * 
	 * @param array
	 * @return a list containing all elements of the JSON array
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected static List fromJSONArray( JSONArray array )
	{
		List l = new ArrayList( );
		for ( int i = 0; i < array.length( ); i++ )
		{
			l.add( array.get( i ) );
		}
		return l;
	}

	/**
	 * Helper method to convert a list to a vector
	 * 
	 * @param list
	 * @return a vector containing all elements of the list
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected static Vector toVector( List list )
	{
		Vector v = new Vector( );
		for ( Object o : list )
		{
			v.add( o );
		}
		return v;
	}

	/**
	 * Converts a number to double
	 * 
	 * @param number
	 * @return double value; 0 if number is not either Integer or Double
	 */
	protected static double convertNumberToDouble( final Object number )
	{
		double returnValue = 0.0d;

		if ( number instanceof Integer )
		{
			returnValue = ( ( Integer ) number ).doubleValue( );
		}
		else if ( number instanceof Double )
		{
			returnValue = ( ( Double ) number ).doubleValue( );
		}

		return returnValue;
	}
	
	private void readObject( ObjectInputStream ois )
		throws IOException
	{
		fromJson( ois.readUTF( ) );
		href = ois.readUTF( );
	}

	private void writeObject( ObjectOutputStream ois )
		throws IOException
	{
		ois.writeUTF( toJson( ) );
		ois.writeUTF( href); 
	}
}
