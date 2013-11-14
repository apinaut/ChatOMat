/* Copyright (c) 2011-2013, Apinauten GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
 * THIS FILE IS GENERATED AUTOMATICALLY. DON'T MODIFY IT. */
package com.apiomat.frontend;

import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;
import com.apiomat.frontend.helper.AOMTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
	protected String href;
	private ObjectState currentState;

	/**
	 * Constructor
	 */
	@SuppressWarnings( "rawtypes" )
	public AbstractClientDataModel( )
	{
		this.data = new JSONObject( );
		this.data.put( "@type", getType( ) );
		if ( ( this.getSimpleName( ).equals( "MemberModel" ) || this.getSimpleName( ).equals( "User" ) )  && getModuleName( ).equals( "Basics" ) )
		{
			this.data.put( "dynamicAttributes", new Hashtable( ) );
		}
		setCurrentState( ObjectState.PERSISTED );
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

	/**
	 * Returns the system to connect to
	 * 
	 * @return TEST for test system, LIVE for production
	 */
	public String getSystem( )
	{
		return MemberModel.system;
	}

	/**
	 * Returns the unique type of this data model to get identified via REST interface
	 * 
	 * @return
	 */
	private String getType( )
	{
		return this.getModuleName( ) + "$" + this.getSimpleName( );
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
	 * Returns the foreign id for this object.
	 * A foreign id is a NON apiomat id (like facebook/twitter id)
	 * 
	 * @return String the foreign id
	 */
	public final String getForeignId( )
	{
		return this.data.optString( "foreignId" );
	}

	/**
	 * Set the foreign id for this object.
	 * A foreign id is a NON apiomat id (like facebook/twitter id)
	 * 
	 * @param foreignId the foreign id
	 */
	public final void setForeignId( final String foreignId )
	{
		this.data.put( "foreignId", foreignId );
	}

	/**
	 * Returns a boolean value if the access to resources is restricted by the defined roles for this object
	 * 
	 * @return boolean value if the access to resources is restricted
	 */
	public final boolean getRestrictResourceAccess( )
	{
		return this.data.optBoolean( "restrictResourceAccess" );
	}

	/**
	 * Sets if the access to resources is restricted by the defined roles for this object
	 * 
	 * @param restrictResourceAccess boolean value if the access to resources is restricted
	 */
	public final void setRestrictResourceAccess( boolean restrictResourceAccess )
	{
		this.data.put( "restrictResourceAccess", restrictResourceAccess );
	}

	/**
	 * Returns a set of all role names allowed to grant privileges on this object
	 * 
	 * @return set of all roles allowed to grant privileges on this object
	 */
	public final Set<String> getAllowedRolesGrant( )
	{
		JSONArray array = this.data.optJSONArray( "allowedRolesGrant" );
		Set<String> ret = new HashSet<String>( );
		for ( int i = 0; i < array.length( ); i++ )
		{
			ret.add( array.getString( i ) );
		}
		return ret;
	}

	/**
	 * Sets the set of all role names allowed to write this object
	 * 
	 * @param allowedRolesGrant role names allowed to write this object
	 */
	public final void setAllowedRolesGrant( final Set<String> allowedRolesGrant )
	{
		JSONArray array = new JSONArray( );
		for ( String roleName : allowedRolesGrant )
		{
			array.put( roleName );
		}
		this.data.put( "allowedRolesGrant", array );
	}

	/**
	 * Returns a set of all role names allowed to write this object
	 * 
	 * @return set of all roles allowed to write this object
	 */
	public final Set<String> getAllowedRolesWrite( )
	{
		JSONArray array = this.data.optJSONArray( "allowedRolesWrite" );
		Set<String> ret = new HashSet<String>( );
		for ( int i = 0; i < array.length( ); i++ )
		{
			ret.add( array.getString( i ) );
		}
		return ret;
	}

	/**
	 * Sets the set of all role names allowed to write this object
	 * 
	 * @param allowedRolesWrite role names allowed to write this object
	 */
	public final void setAllowedRolesWrite( final Set<String> allowedRolesWrite )
	{
		JSONArray array = new JSONArray( );
		for ( String roleName : allowedRolesWrite )
		{
			array.put( roleName );
		}
		this.data.put( "allowedRolesWrite", array );
	}

	/**
	 * Returns a set of all role names allowed to read this object
	 * 
	 * @return set of all roles allowed for this object
	 */
	public final Set<String> getAllowedRolesRead( )
	{
		JSONArray array = this.data.optJSONArray( "allowedRolesRead" );
		Set<String> ret = new HashSet<String>( );
		for ( int i = 0; i < array.length( ); i++ )
		{
			ret.add( array.getString( i ) );
		}
		return ret;
	}

	/**
	 * Sets the set of all role names allowed to read this object
	 * 
	 * @param allowedRolesRead names allowed to read this object
	 */
	public final void setAllowedRolesRead( final Set<String> allowedRolesRead )
	{
		JSONArray array = new JSONArray( );
		for ( String roleName : allowedRolesRead )
		{
			array.put( roleName );
		}
		this.data.put( "allowedRolesRead", array );
	}

	/**
	 * Returns a map containing all referenced model HREFs. Use it for caching purposes.
	 * 
	 * @return a map containing all referenced model HREFs
	 */
	public final Map<String, List<String>> getRefModelHrefs( )
	{
		return this.data.optJSONObject( "referencedHrefs" ).getMyHashMap( );
	}

	/**
	 * Returns HREFs of a referenced model, given by its name
	 * 
	 * @param name
	 * @return HREFs of a referenced model
	 */
	@SuppressWarnings( "unchecked" )
	public final List<String> getRefModelHrefsForName( String name )
	{
		Map<String, List<String>> referencedHrefs = getRefModelHrefs( );
		if ( referencedHrefs != null && referencedHrefs.containsKey( name ) )
		{
			return fromJSONArray( ( JSONArray ) referencedHrefs.get( name ) );
		}
		return null;
	}

	/**
	 * Returns the date when this object was created on server side
	 * 
	 * @return date when this object was created on server side, NULL if it was created but not saved yet
	 */
	public final Date getCreatedAt( )
	{
		Date d = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) ).getTime( );
		d.setTime( this.data.optLong( "createdAt" ) );
		return d;
	}

	/**
	 * Returns the date when this object was modified last on server side
	 * 
	 * @return date when this object was modified last on server side, NULL if it was created but not saved yet
	 */
	public final Date getLastModifiedAt( )
	{
		Date d = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) ).getTime( );
		d.setTime( this.data.optLong( "lastModifiedAt" ) );
		return d;
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
	 * @return the currentState
	 */
	public ObjectState getCurrentState( )
	{
		return this.currentState;
	}

	/**
	 * @param currentState
	 *        the currentState to set
	 */
	public void setCurrentState( ObjectState currentState )
	{
		this.currentState = currentState;
	}

	/**
	 * Decodes this data model from a JSON string; used to communicate with the
	 * REST interface
	 * 
	 * @param jsonData
	 * @return this object
	 */
	public final AbstractClientDataModel fromJson( final String jsonData )
	{
		this.data = new JSONObject( jsonData );
		this.href = this.data.optString( "href" );
		return this;
	}

	/**
	 * Decodes this data model from a JSON object; used to communicate with the REST interface
	 * 
	 * @param jsonData
	 * @return this object
	 */
	public final AbstractClientDataModel fromJson( final JSONObject jsonData )
	{
		this.data = jsonData;
		this.href = this.data.optString( "href" );
		return this;
	}

	/**
	 * Encodes this data model as a JSON string; used to communicate with the REST interface
	 * 
	 * @return this data model as JSON string
	 */
	public final String toJson( )
	{
		String json = "";
		if ( getHref( ) != null && getHref( ).length( ) > 0 )
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
	 * Saves this data model. It is - based on HREF - automatically determined,
	 * if this model exists on the server, leading to an update, or not, leading
	 * to an post command.
	 * 
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public void save( ) throws ApiomatRequestException
	{
		setCurrentState( ObjectState.PERSISTING );
		boolean wasLocalSave = false;
		if ( this.href == null )
		{
			if ( Datastore.getInstance( ).sendOffline( "POST" ) )
			{
				final String sendHREF = Datastore.getInstance( ).createModelHref( getModuleName( ), getSimpleName( ) );
				String location = Datastore.getInstance( ).getOfflineHandler( ).addTask( "POST", sendHREF, this, null );
				wasLocalSave = true;
				if ( location != null )
				{
					this.href = location;
				}
			}
			else
			{
				String location = Datastore.getInstance( ).postOnServer( this );
				this.href = location;
			}

		}
		else
		{
			if ( Datastore.getInstance( ).sendOffline( "PUT" ) )
			{
				Datastore.getInstance( ).getOfflineHandler( ).addTask( "PUT", getHref( ), this, null );
				wasLocalSave = true;
			}
			else
			{
				Datastore.getInstance( ).updateOnServer( this );
			}
		}
		this.setOffline( wasLocalSave );

		/* fetch server-side values */
		if ( wasLocalSave == false )
		{
			load( );
			setCurrentState( ObjectState.PERSISTED );
		}
		else
		{
			setCurrentState( ObjectState.LOCAL_PERSISTED );
		}
	}

	/**
	 * Saves the object in background and not on the UI thread
	 * 
	 * @param callback
	 *        The method which will called when saving is finished
	 */
	public void saveAsync( AOMEmptyCallback callback )
	{
		// Check if current object is in persisting process
		if ( getCurrentState( ).equals( ObjectState.PERSISTING ) )
		{
			throw new IllegalStateException(
				"Object is in persisting process. Please try again later" );
		}
		AOMTask<Void> task = new AOMTask<Void>( )
		{
			@Override
			public Void doRequest( ) throws ApiomatRequestException
			{
				AbstractClientDataModel.this.save( );
				return null;
			}
		};
		task.execute( callback );
	}

	/**
	 * Loads (updates) this data model with server values
	 * 
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public final void load( ) throws ApiomatRequestException
	{
		load( null );
	}

	/**
	 * Loads (updates) this data model with server values. Since you have to
	 * pass the HREF for this method, only use it when loading a model which has
	 * no HREF in it (was not sent/loaded before). Else use {@link #load()}
	 * 
	 * @param href
	 *        The HREF of this model
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public final void load( final String href ) throws ApiomatRequestException
	{
		Datastore.getInstance( ).loadFromServer( this,
			href == null ? this.getHref( ) : href );
		// Set href only if was given
		if ( href != null )
		{
			this.href = this.data.optString( "href", null );
		}
	}

	/**
	 * Loads (updates) in background this data model with server values
	 * 
	 * @param callback
	 *        The callback method which will called when request is finished
	 */
	public final void loadAsync(
		final AOMEmptyCallback callback )
	{

		loadAsync( null, callback );
	}

	/**
	 * Loads (updates) this data model with server values in background. Since
	 * you have to pass the HREF for this method, only use it when loading a
	 * model which has no HREF in it (was not sent/loaded before). Else use
	 * {@link #loadAsync(com.apiomat.frontend.callbacks.AOMEmptyCallback callback)}
	 * 
	 * Throws an IllegalStateException if object is in persisting process
	 * 
	 * @param href
	 *        The HREF of this model
	 * @param callback
	 *        The callback method which will called when request is finished
	 */
	public final void loadAsync( final String href,
		final AOMEmptyCallback callback )
	{

		if ( currentState.equals( ObjectState.PERSISTING ) )
		{
			throw new IllegalStateException(
				"Object is in persisting process. Please try again later" );
		}
		AOMTask<Void> reqTask = new AOMTask<Void>( )
		{
			@Override
			public Void doRequest( )
				throws ApiomatRequestException
			{
				AbstractClientDataModel.this.load( href );
				return null;
			}
		};
		reqTask.execute( callback );
	}

	/**
	 * Deletes this data model on server
	 * 
	 * @throws Exception
	 */
	public final void delete( ) throws ApiomatRequestException
	{
		if ( Datastore.getInstance( ).sendOffline( "DELETE" ) )
		{
			Datastore.getInstance( ).getOfflineHandler( ).addTask( "DELETE", getHref( ), this, null );
			setCurrentState( ObjectState.LOCAL_DELETED );
		}
		else
		{
			Datastore.getInstance( ).deleteOnServer( this );
			setCurrentState( ObjectState.DELETED );
		}
	}

	/**
	 * Deletes this data model on server in background task
	 * 
	 * @param callback Callback method which is called after deletion was finished on server
	 */
	public void deleteAsync( AOMEmptyCallback callback )
	{
		// Check if current object is in persisting process
		if ( getCurrentState( ).equals( ObjectState.DELETING ) )
		{
			throw new IllegalStateException(
				"Object is in deleting process. Please try again later" );
		}
		AOMTask<Void> task = new AOMTask<Void>( )
		{
			@Override
			public Void doRequest( ) throws ApiomatRequestException
			{
				AbstractClientDataModel.this.delete( );
				return null;
			}
		};
		task.execute( callback );
	}

	private void readObject( ObjectInputStream ois )
		throws IOException
	{
		fromJson( ois.readUTF( ) );
	}

	private void writeObject( ObjectOutputStream ois )
		throws IOException
	{
		ois.writeUTF( toJson( ) );
	}

	public boolean isOffline( )
	{
		return this.data.optBoolean( "isOffline", false );
	}

	public void setOffline( boolean offline )
	{
		this.data.put( "isOffline", offline );
	}

	public String getID( )
	{
		String id = this.data.optString( "id", null );
		if ( id == null )
		{
			/* extract from HREF */
			id = getHref( ).substring( getHref( ).lastIndexOf( "/" ) + 1 );
		}
		return id;
	}

	public static enum ObjectState
	{
		DELETING, DELETED, PERSISTING, PERSISTED, LOCAL_PERSISTED, LOCAL_DELETED;
	}
}
