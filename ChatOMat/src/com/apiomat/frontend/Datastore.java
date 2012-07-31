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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.apiomat.frontend.AbstractClientDataModel;
import com.apiomat.frontend.basics.MemberModel;

import rpc.json.me.JSONArray;
import android.util.Base64;
import android.util.Log;

/**
 * This class is your interface to the APIOMAT service. Each method lets your post, put, get or delete your data models.
 * Basic handling is already implemented in your generated module classes, so it won't be necessary in most cases to
 * call the {@link Datastore} methods directly.
 * 
 * @author andreasfey
 */
public class Datastore
{
	private static Datastore myInstance;

	private final String baseUrl;

	private final String apiKey;

	private final String userName;

	private final String password;

	/**
	 * Returns a singleton instance of the {@link Datastore}
	 * 
	 * @return singleton instance
	 */
	public static Datastore getInstance( )
	{
		if ( myInstance != null )
		{
			return myInstance;
		}

		throw new IllegalStateException( );
	}

	private Datastore( final String baseUrl, final String apiKey, final String userName, final String password )
	{
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Configures and returns a {@link Datastore} instance
	 * 
	 * @param baseUrl The base URL of the APIOMAT service; usually <a
	 *        href="http://apiomat.org/yambas/rest/apps/">http://apiomat.org/yambas/rest/apps/</a>
	 * @param apiKey The api key of your application (see the member model class)
	 * @param userName Your username
	 * @param password Your password
	 * @return A configured Datastore instance
	 */
	public static Datastore configure( final String baseUrl, final String apiKey, final String userName,
		final String password )
	{
		myInstance = new Datastore( baseUrl, apiKey, userName, password );
		return myInstance;
	}
	
	/**
	 * Configures and returns a {@link Datastore} instance
	 * 
	 * @param member Your member model (user)
	 * @return A configured Datastore instance
	 */
	public static Datastore configure( final MemberModel member )
	{
		myInstance = new Datastore( MemberModel.baseURL, MemberModel.apiKey, member.getUserName( ), member.getPassword( ) );
		return myInstance;
	}

	/**
	 * Method for posting the model to the server <u>initially</u>. That is, at the point using this method the model
	 * has not HREF yet.
	 * 
	 * @param dataModel
	 * @return the HREF of the posted model
	 */
	public String postOnServer( final AbstractClientDataModel dataModel ) throws Exception
	{
		return postOnServer( dataModel, createModelHref( dataModel.getModuleName( ), dataModel.getSimpleName( ) ) );
	}

	/**
	 * Method for posting the model to the server in an update manner. That is, at the point using this method the model
	 * has a HREF and exists on server.
	 * 
	 * @param dataModel
	 * @param href HREF of the model to post (or the address to post the model to)
	 * @return the HREF of the posted model
	 */
	public String postOnServer( final AbstractClientDataModel dataModel, final String href ) throws Exception
	{
		final String data = dataModel.toJson( );
		return postOnServer( new StringEntity( data ), href );
	}

	/**
	 * Method to post static data to the server. Do not forget to store the returned HREF to the owner model, since this
	 * method only stores the byte array in the server.
	 * 
	 * @param rawData raw data as byte array
	 * @param isImage TRUE to store the raw data as image, FALSE to store as video
	 * @return HREF of the posted data
	 */
	public String postStaticDataOnServer( final byte[ ] rawData, final boolean isImage ) throws Exception
	{
		return postOnServer( new ByteArrayEntity( rawData ), createStaticDataHref( isImage ) );
	}

	/**
	 * Loads an existing model from the server
	 * 
	 * @param clazz class of the model
	 * @param dataModelHref HREF address of the model
	 * @return the model object
	 */
	public <T extends AbstractClientDataModel> T loadFromServer( final Class<T> clazz, final String dataModelHref )
		throws Exception
	{
		T element = clazz.newInstance( );
		loadFromServer( element, dataModelHref );
		return element;
	}

	/**
	 * Loads an existing model from the server. The new values from server are written directly to the dataModel
	 * parameter.
	 * 
	 * @param dataModel the model object
	 * @param dataModelHref HREF address of the model
	 * @return the model object
	 */
	public <T extends AbstractClientDataModel> T loadFromServer( final T dataModel, final String dataModelHref )
		throws Exception
	{
		if ( dataModelHref == null || "".equals( dataModelHref ))
		{
			Log.e( "yambas datastore", "Model has no HREF; please save it first!" );
		}

		final DefaultHttpClient httpClient = new DefaultHttpClient( );
		final HttpGet request = new HttpGet( new URI( createHref( dataModelHref ) ) );

		request.setHeader( "Accept", "application/json" );
		request.setHeader( "Authorization", getAuthenticationHeader( ) );
		request.setHeader( "X-apiomat-apikey", this.apiKey );

		final HttpResponse httpResponse = httpClient.execute( request );

		if ( httpResponse.getStatusLine( ).getStatusCode( ) == 200 )
		{
			final InputStream is = httpResponse.getEntity( ).getContent( );
			dataModel.fromJson( copyStreamToString( is ) );
			return dataModel;
		}
		return null;
	}

	/**
	 * Loads existing models from the server
	 * 
	 * @param clazz class of the models
	 * @param moduleName module name where the models are used in
	 * @param simpleModelName the simple class name of the models
	 * @param query a query string to filter the results
	 * @return all models fitting the search parameters
	 */
	public <T extends AbstractClientDataModel> List<T> loadFromServer( final Class<T> clazz, final String moduleName,
		final String simpleModelName, final String query ) throws Exception
	{
		return loadFromServer( clazz, createModelHref( moduleName, simpleModelName ), query );
	}

	/**
	 * Loads existing models from the server
	 * 
	 * @param clazz class of the models
	 * @param dataModelHref HREF of the models
	 * @param query a query string to filter the results
	 * @return all models fitting the search parameters
	 */
	public <T extends AbstractClientDataModel> List<T> loadFromServer( final Class<T> clazz,
		final String dataModelHref, final String query ) throws Exception
	{
		if ( dataModelHref == null || "".equals( dataModelHref ) )
		{
			Log.e( "yambas datastore", "Model has no HREF; please save it first!" );
		}

		final URI uri = new URI( concatHrefWithQuery( dataModelHref, query ) );
		final DefaultHttpClient httpClient = new DefaultHttpClient( );
		final HttpGet request = new HttpGet( uri );

		request.setHeader( "Accept", "application/json" );
		request.setHeader( "Authorization", getAuthenticationHeader( ) );
		request.setHeader( "X-apiomat-apikey", this.apiKey );

		final HttpResponse httpResponse = httpClient.execute( request );

		if ( httpResponse.getStatusLine( ).getStatusCode( ) == 200 )
		{
			final InputStream is = httpResponse.getEntity( ).getContent( );
			final JSONArray listOfModels = new JSONArray( copyStreamToString( is ) );
			final List<T> returnValue = new LinkedList<T>( );

			for ( int i = 0; i < listOfModels.length( ); i++ )
			{
				T element = clazz.newInstance( );
				element.fromJson( listOfModels.getString( i ) );
				returnValue.add( element );
			}
			return returnValue;
		}
		return null;
	}

	/**
	 * Deletes the data model from the server
	 * 
	 * @param dataModel the data model object
	 * @return TRUE on success
	 */
	public boolean deleteOnServer( AbstractClientDataModel dataModel ) throws Exception
	{
		return deleteOnServer( dataModel.getHref( ) );
	}

	/**
	 * Deletes the data model from the server based on its href
	 * 
	 * @param href the data model href
	 * @return TRUE on success
	 */
	public boolean deleteOnServer( String href ) throws Exception
	{
		if ( href == null || "".equals( href ) )
		{
			Log.e( "yambas datastore", "Model has no HREF; please save it first!" );
		}

		final URI uri = new URI( href );
		final DefaultHttpClient httpClient = new DefaultHttpClient( );
		final HttpDelete request = new HttpDelete( uri );

		request.setHeader( "Accept", "application/json" );
		request.setHeader( "Authorization", getAuthenticationHeader( ) );
		request.setHeader( "X-apiomat-apikey", this.apiKey );

		final HttpResponse httpResponse = httpClient.execute( request );

		return httpResponse.getStatusLine( ).getStatusCode( ) == 204;
	}

	/**
	 * Updates the data model from the server
	 * 
	 * @param dataModel the data model object
	 * @return TRUE on success
	 */
	public boolean updateOnServer( AbstractClientDataModel dataModel ) throws Exception
	{
		if ( dataModel.getHref( ) == null || "".equals( dataModel.getHref( ) ) )
		{
			Log.e( "yambas datastore", "Model has no HREF; please save it first!" );
		}

		final URI uri = new URI( dataModel.getHref( ) );
		final DefaultHttpClient httpClient = new DefaultHttpClient( );
		final HttpPut request = new HttpPut( uri );
		final String data = dataModel.toJson( );

		request.setEntity( new StringEntity( data ) );
		request.setHeader( "Content-Type", "application/json" );
		request.setHeader( "Authorization", getAuthenticationHeader( ) );
		request.setHeader( "X-apiomat-apikey", this.apiKey );

		final HttpResponse httpResponse = httpClient.execute( request );

		return httpResponse.getStatusLine( ).getStatusCode( ) == 200;
	}

	private String postOnServer( final HttpEntity entity, final String href ) throws Exception
	{
		final DefaultHttpClient httpClient = new DefaultHttpClient( );
		final URI uri = new URI( href );
		final HttpPost request = new HttpPost( uri );

		request.setEntity( entity );
		request.setHeader( "Content-Type", "application/json" );
		request.setHeader( "Authorization", getAuthenticationHeader( ) );
		request.setHeader( "X-apiomat-apikey", this.apiKey );

		final HttpResponse httpResponse = httpClient.execute( request );

		if ( httpResponse.getStatusLine( ).getStatusCode( ) == 201 )
		{
			return httpResponse.getFirstHeader( "Location" ).getValue( );
		}

		return null;
	}

	private String getAuthenticationHeader( )
	{
		final String credentials = this.userName + ":" + this.password;
		final byte[ ] encoded = Base64.encode( credentials.getBytes( ), Base64.NO_PADDING | Base64.NO_WRAP );
		final String encodedAuth = "Basic " + new String( encoded );

		return encodedAuth;
	}

	private String createHref( String href )
	{
		if ( href.startsWith( "http" ) )
		{
			return href;
		}

		if ( href.startsWith( "/apps" ) )
		{
			return this.baseUrl.substring( 0, this.baseUrl.indexOf( "/apps" ) ) + href;
		}

		return this.baseUrl + "/" + href;
	}

	private String createStaticDataHref( final boolean image )
	{
		final StringBuffer sb = new StringBuffer( );
		sb.append( this.baseUrl );
		sb.append( "/data/" );
		sb.append( image ? "images" : "videos" );
		sb.append( '/' );
		return sb.toString( );
	}

	private String createModelHref( final String moduleName, final String simpleModelName )
	{
		final StringBuffer sb = new StringBuffer( );
		sb.append( this.baseUrl );
		sb.append( "/models/" );
		sb.append( moduleName );
		sb.append( '/' );
		sb.append( simpleModelName );
		return sb.toString( );
	}

	private static String concatHrefWithQuery( final String dataModelHref, final String query )
	{
		final StringBuffer sb = new StringBuffer( );
		sb.append( dataModelHref );
		if ( query != null )
		{
			sb.append( "?q=" );
			sb.append( URLEncoder.encode( query ) );
		}
		return sb.toString( );
	}

	private String copyStreamToString( final InputStream is ) throws Exception
	{
		final BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
		final StringBuilder content = new StringBuilder( );
		String line = null;

		while ( ( line = reader.readLine( ) ) != null )
		{
			content.append( line );
		}

		return content.toString( );
	}

}
