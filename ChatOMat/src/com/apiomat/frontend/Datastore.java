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

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.Status;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.basics.User;
import com.apiomat.frontend.callbacks.AOMCallback;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;
import com.apiomat.frontend.helper.AOMTask;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import rpc.json.me.JSONArray;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.apiomat.frontend.offline.AOMOfflineHandler;

/**
 * This class is your interface to the apiOmat service. Each method lets your
 * post, put, get or delete your data models. Basic handling is already
 * implemented in your generated module classes, so it won't be necessary in
 * most cases to call the {@link Datastore} methods directly.
 * 
 * @author andreasfey, phimi
 */
public class Datastore
{
	private static Datastore myInstance;

	private static AOMCacheStrategy cacheStrategy = AOMCacheStrategy.CHECK_SERVER_ELSE_CACHE;
	private static AOMOfflineStrategy offlineStrategy = AOMOfflineStrategy.NO_OFFLINE_HANDLING;
	private static Context appCtx = null;
	private final AOMModelStore modelCache = new AOMModelStore( );
	private final String baseUrl;
	private final String apiKey;
	private final String userName;
	private final String password;
	private final String system;
	private DefaultHttpClient httpClient;
	private AOMOfflineHandler offlineHandler = null;

	private Datastore( final String baseUrl, final String apiKey,
		final String userName, final String password, final String system )
	{
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
		this.userName = userName;
		this.password = password;
		this.system = system;
	}

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

	/**
	 * Configures and returns a {@link Datastore} instance
	 * 
	 * @deprecated Please us {@link #configure(com.apiomat.frontend.basics.User user)} instead
	 * 
	 * @param baseUrl
	 *        The base URL of the APIOMAT service; usually <a
	 *        href="http://apiomat.org/yambas/rest/apps/">http://apiomat.org/yambas/rest/apps/</a> (see the member model
	 *        class)
	 * @param apiKey
	 *        The api key of your application (see the member model class)
	 * @param userName
	 *        Your username
	 * @param password
	 *        Your password
	 * @return A configured Datastore instance
	 */
	@Deprecated
	public static Datastore configure( final String baseUrl,
		final String apiKey, final String userName, final String password )
	{
		myInstance = new Datastore( baseUrl, apiKey, userName, password, MemberModel.system );
		return myInstance;
	}

	/**
	 * Configures and returns a {@link Datastore} instance
	 * 
	 * @deprecated Use Datastore.configure(user) instead
	 * @param member
	 *        The member where userName and password are the login
	 *        credentials
	 * @return A configured Datastore instance
	 */
	@Deprecated
	public static Datastore configure( final MemberModel member )
	{
		myInstance = new Datastore( MemberModel.baseURL, MemberModel.apiKey,
			member.getUserName( ), member.getPassword( ), member.getSystem( ) );
		return myInstance;
	}

	/**
	 * Configures and returns a {@link Datastore} instance
	 * 
	 * @param user
	 *        The user where userName and password are the login
	 *        credentials
	 * @return A configured Datastore instance
	 */
	public static Datastore configure( final User user )
	{
		myInstance = new Datastore( User.baseURL, User.apiKey,
			user.getUserName( ), user.getPassword( ), user.getSystem( ) );
		return myInstance;
	}

	/**
	 * Configures and returns a {@link com.apiomat.frontend.Datastore} instance
	 * 
	 * @param baseUrl The base URL of the APIOMAT service; usually <a
	 *        href="http://apiomat.org/yambas/rest/apps/">http://apiomat.org/yambas/rest/apps/</a> (see the member model
	 *        class)
	 * @param apiKey The api key of your application (see the member model class)
	 * @param userName Your username
	 * @param password Your password
	 * @param sdkVersion The SDK version (see the member model class)
	 * @param system The system which will be used (see the member model class) (should be LIVE,TEST,STAGING)
	 * @return A configured Datastore instance
	 */
	public static Datastore configure( final String baseUrl, final String apiKey, final String userName,
		final String password, final String sdkVersion, final String system )
	{
		myInstance = new Datastore( baseUrl, apiKey, userName, password, system );
		return myInstance;
	}

	/**
	 * Configures and returns a {@link com.apiomat.frontend.Datastore} instance
	 * 
	 * @param baseUrl The base URL of the APIOMAT service; usually <a
	 *        href="http://apiomat.org/yambas/rest/apps/">http://apiomat.org/yambas/rest/apps/</a> (see the member model
	 *        class)
	 * @param apiKey The api key of your application (see the member model class)
	 * @param system The system which will be used (see the member model class) (should be LIVE,TEST,STAGING)
	 * @return A configured Datastore instance
	 */
	public static Datastore configure( final String baseUrl, final String apiKey, final String system )
	{
		return configure( baseUrl, apiKey, null, null, null, system );
	}

	/**
	 * Sets the caching strategy for this datastore.
	 * 
	 * @param cacheStrategy the caching strategy to use; see {@link com.apiomat.frontend.Datastore.AOMCacheStrategy}
	 * @return the datastore
	 */
	public static Datastore setCachingStrategy( AOMCacheStrategy cacheStrategy )
	{
		if ( myInstance == null )
		{
			throw new IllegalStateException( "Please configure Datastore first" );
		}
		Datastore.cacheStrategy = cacheStrategy;
		return myInstance;
	}

	/**
	 * Set the offline strategy for you application.
	 * 
	 * @param _offlineStrategy The strategy
	 * @param _context The context (for offline initalization) Normally it will be teh application context
	 * @param _listener (optional) This listener will be informed if offline task are executed successfully or with
	 *        errors
	 * 
	 * @return Configured {@link Datastore} instance
	 */
	public static Datastore setOfflineStrategy( AOMOfflineStrategy _offlineStrategy, Context _context,
		AOMOfflineHandler.AOMOfflineListener _listener ) throws IllegalStateException, RuntimeException
	{
		if ( myInstance == null )
		{
			throw new IllegalStateException( "Please configure Datastore first" );
		}
		Datastore.offlineStrategy = _offlineStrategy;
		Datastore.appCtx = _context;
		/* initialize handler */
		if ( myInstance.offlineHandler == null )
		{
			myInstance.offlineHandler = new AOMOfflineHandler( _context );
			if ( _listener != null )
			{
				myInstance.offlineHandler.addListener( _listener );
			}
		}
		return myInstance;
	}

	private static String copyStreamToString( final InputStream is )
		throws IOException
	{
		final BufferedReader reader = new BufferedReader( new InputStreamReader(
			is ) );
		final StringBuilder content = new StringBuilder( );
		String line;

		while ( ( line = reader.readLine( ) ) != null )
		{
			content.append( line );
		}

		return content.toString( );
	}

	/**
	 * Method for posting the model to the server <u>initially</u>. That is, at
	 * the point using this method the model has not HREF yet.
	 * 
	 * @param dataModel the model to save on server
	 * @return the HREF of the posted model
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public String postOnServer( final AbstractClientDataModel dataModel )
		throws ApiomatRequestException
	{
		return postOnServer(
			dataModel,
			createModelHref( dataModel.getModuleName( ),
				dataModel.getSimpleName( ) ) );
	}

	/**
	 * Method for posting the model to the server in an update manner. That is,
	 * at the point using this method the model has a HREF and exists on server.
	 * 
	 * @param dataModel the model which will be saved on server
	 * @param href
	 *        HREF of the model to post (or the address to post the model
	 *        to)
	 * @return the HREF of the posted model
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public String postOnServer( final AbstractClientDataModel dataModel,
		final String href ) throws ApiomatRequestException
	{
		final String data = dataModel.toJson( );
		try
		{
			return postOnServer( new StringEntity( data, "utf-8" ), href );
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new ApiomatRequestException(
				Status.UNSUPPORTED_ENCODING.getStatusCode( ), 0,
				e.getMessage( ) );
		}
	}

	/**
	 * Method for posting the model to the server in an update manner. That is,
	 * at the point using this method the model has a HREF.
	 * Request works background thread and not on the UI thread
	 * 
	 * @param dataModel the model which will be saved on server
	 * @param href
	 *        HREF of the model to post (or the address to post the model
	 *        to)
	 * @param callback The method which will called if response come back from server.
	 */
	public void postOnServerAsync( final AbstractClientDataModel dataModel,
		final String href, final AOMCallback<String> callback )
	{
		try
		{
			final String data = dataModel.toJson( );
			final HttpEntity entity = new StringEntity( data, "utf-8" );
			new AOMTask<String>( )
			{
				@Override
				public String doRequest( ) throws ApiomatRequestException
				{
					return postOnServer( entity, href );
				}
			}.execute( callback );

		}
		catch ( UnsupportedEncodingException e )
		{
			callback.isDone( null, new ApiomatRequestException( Status.UNSUPPORTED_ENCODING ) );
		}
	}

	/**
	 * Method to post static data to the server. Do not forget to store the
	 * returned HREF to the owner model, since this method only stores the byte
	 * array on the server.
	 * 
	 * @param rawData
	 *        raw data as byte array
	 * @param isImage
	 *        TRUE to store the raw data as image, FALSE to store as video
	 * @return HREF of the posted data
	 * @throws Exception
	 */
	public String postStaticDataOnServer( final byte[ ] rawData,
		final boolean isImage ) throws ApiomatRequestException
	{
		return postOnServer( new ByteArrayEntity( rawData ),
			createStaticDataHref( isImage ) );
	}

	/**
	 * Method to post static data to the server. Do not forget to store the
	 * returned HREF to the owner model, since this method only stores the byte
	 * array on the server.
	 * Request works in background thread and not on the UI thread
	 * 
	 * @param rawData
	 *        raw data as byte array
	 * @param isImage
	 *        TRUE to store the raw data as image, FALSE to store as video
	 * @param _callback The object which will called if response come back from server.
	 */
	public void postStaticDataOnServerAsync( final byte[ ] rawData,
		final boolean isImage, final AOMCallback<String> _callback )
	{
		new AOMTask<String>( )
		{
			@Override
			public String doRequest( ) throws ApiomatRequestException
			{
				return postOnServer( new ByteArrayEntity( rawData ), createStaticDataHref( isImage ) );
			}
		}.execute( _callback );
	}

	/**
	 * Loads an existing model from the server
	 * 
	 * @param clazz
	 *        class of the model
	 * @param dataModelHref
	 *        HREF address of the model
	 * @return the model object
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> T loadFromServer(
		final Class<T> clazz, final String dataModelHref )
		throws ApiomatRequestException
	{
		return loadFromServer( clazz, dataModelHref, false );
	}

	/**
	 * Loads an existing model from the server but in the background and not on
	 * the UI thread
	 * 
	 * @param clazz
	 *        class of the model
	 * @param dataModelHref
	 *        HREF address of the model
	 * @param getCallback
	 *        The method which will called if response come back from server.
	 */
	public <T extends AbstractClientDataModel> void loadFromServerAsync(
		final Class<T> clazz, final String dataModelHref,
		final AOMCallback<T> getCallback )
	{
		loadFromServerAsync( clazz, dataModelHref, false, getCallback );
	}

	/**
	 * Loads an existing model from the server
	 * 
	 * @param clazz
	 *        class of the model
	 * @param dataModelHref
	 *        HREF address of the model
	 * @param withReferencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @return the model object
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> T loadFromServer(
		final Class<T> clazz, final String dataModelHref,
		final boolean withReferencedHrefs ) throws ApiomatRequestException
	{
		T element;
		try
		{
			element = clazz.newInstance( );
		}
		catch ( InstantiationException e )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND );
		}
		catch ( Exception e )
		{
			throw new ApiomatRequestException( Status.INSTANTIATE_EXCEPTION );
		}
		return loadFromServer( element, dataModelHref, withReferencedHrefs );
	}

	/**
	 * Loads an existing model from the server but in the background and not on
	 * the UI thread
	 * 
	 * @param clazz
	 *        class of the model
	 * @param dataModelHref
	 *        HREF address of the model
	 * @param withReferencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @param getCallback
	 *        The method which will called if response come back from server
	 */
	public <T extends AbstractClientDataModel> void loadFromServerAsync(
		final Class<T> clazz, final String dataModelHref,
		final boolean withReferencedHrefs, final AOMCallback<T> getCallback )
	{
		T element;
		try
		{
			element = clazz.newInstance( );
		}
		catch ( Exception e )
		{
			getCallback.isDone( null, new ApiomatRequestException( Status.INSTANTIATE_EXCEPTION ) );
			return;
		}
		loadFromServerAsync( element, dataModelHref, withReferencedHrefs, getCallback );
	}

	/**
	 * Loads an existing model from the server. The new values from server are
	 * written directly to the dataModel parameter.
	 * 
	 * @param dataModel
	 *        the model object
	 * @param dataModelHref
	 *        HREF address of the model
	 * @return the model object
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> T loadFromServer(
		final T dataModel, final String dataModelHref )
		throws ApiomatRequestException
	{
		return loadFromServer( dataModel, dataModelHref, false );
	}

	/**
	 * Loads an existing model from the server. The new values from server are
	 * written directly to the dataModel parameter.
	 * 
	 * @param dataModel
	 *        the model object
	 * @param dataModelHref
	 *        HREF address of the model
	 * @param withReferencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @return the model object
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> T loadFromServer(
		final T dataModel, final String dataModelHref,
		final boolean withReferencedHrefs ) throws ApiomatRequestException
	{
		if ( dataModelHref == null || dataModelHref.equals( "" ) )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND );
		}

		// check if we have lastModified for dataModel if not check if Modelstore contains object with tag
		String lastModified = null;
		if ( cacheStrategy.equals( AOMCacheStrategy.CHECK_SERVER_ELSE_CACHE ) )
		{
			lastModified = this.modelCache.getEtagForModel( dataModelHref, dataModel.getClass( ) );
		}

		AtomicReference<Integer> returnedStatusCode = new AtomicReference<Integer>( );
		List<Integer> expectedCodes = new ArrayList<Integer>( );
		expectedCodes.add( HttpStatus.SC_OK );
		expectedCodes.add( HttpStatus.SC_NOT_MODIFIED );
		expectedCodes.add( HttpStatus.SC_NO_CONTENT );
		String returnStr =
			sendRequest( createHref( dataModelHref ), "", withReferencedHrefs, new HttpGet( ), null, expectedCodes,
				returnedStatusCode, lastModified );

		if ( returnedStatusCode.get( ) == HttpStatus.SC_NOT_MODIFIED )
		{
			/* Check if in cache and cache strategy is using cache */
			if ( cacheStrategy.equals( AOMCacheStrategy.CHECK_SERVER_ELSE_CACHE ) )
			{
				final String jsonStr = this.modelCache.getModel( dataModelHref );
				if ( jsonStr == null || jsonStr.equals( "" ) )
				{
					throw new ApiomatRequestException( Status.INSTANTIATE_EXCEPTION );
				}
				dataModel.fromJson( jsonStr );
			}
			return dataModel;
		}
		else if ( returnedStatusCode.get( ) == HttpStatus.SC_OK )
		{
			dataModel.fromJson( returnStr );
			// Add datamodel to store if we use other cache strategy than NO_CACHE
			if ( cacheStrategy.equals( AOMCacheStrategy.NO_CACHE ) == false )
			{
				this.modelCache.addModel( dataModelHref, dataModel );
			}
			return dataModel;
		}
		return null;
	}

	/**
	 * Loads an existing model from the server but in the background and not on
	 * the UI thread. The new values from server are written directly to the
	 * dataModel parameter.
	 * 
	 * @param dataModel
	 *        the model object
	 * @param dataModelHref
	 *        HREF address of the model
	 * @param withReferencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @param getCallback the callback method
	 */
	public <T extends AbstractClientDataModel> void loadFromServerAsync(
		final T dataModel, final String dataModelHref,
		final boolean withReferencedHrefs, AOMCallback<T> getCallback )
	{

		new AOMTask<T>( )
		{
			@Override
			public T doRequest( ) throws ApiomatRequestException
			{
				loadFromServer( dataModel, dataModelHref, withReferencedHrefs );
				return dataModel;
			}
		}.execute( getCallback );
	}

	/**
	 * Loads existing models from the server
	 * 
	 * @param clazz
	 *        class of the models
	 * @param moduleName
	 *        module name where the models are used in
	 * @param simpleModelName
	 *        the simple class name of the models
	 * @param query
	 *        a query string to filter the results
	 * @return all models fitting the search parameters
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> List<T> loadFromServer(
		final Class<T> clazz, final String moduleName,
		final String simpleModelName, final String query )
		throws ApiomatRequestException
	{
		return loadFromServer( clazz, moduleName, simpleModelName, false, query );
	}

	/**
	 * Loads existing models from server in background
	 * 
	 * @param <T> defines the returned data type (Must by subtype of
	 *        {@link com.apiomat.frontend.AbstractClientDataModel})
	 * 
	 * @param clazz
	 *        class of the models
	 * @param moduleName
	 *        module name where the models are used in
	 * @param simpleModelName
	 *        the simple class name of the models
	 * @param query
	 *        a query string to filter the results
	 * @param getCallback method which will called if request is finished
	 */
	public <T extends AbstractClientDataModel> void loadFromServerAsync(
		final Class<T> clazz, final String moduleName,
		final String simpleModelName, final String query,
		AOMCallback<List<T>> getCallback )
	{

		new AOMTask<List<T>>( )
		{
			@Override
			public List<T> doRequest( ) throws ApiomatRequestException
			{
				return loadFromServer( clazz, moduleName, simpleModelName,
					false, query );
			}
		}.execute( getCallback );
	}

	/**
	 * Loads existing models from the server
	 * 
	 * @param clazz
	 *        class of the models
	 * @param moduleName
	 *        module name where the models are used in
	 * @param simpleModelName
	 *        the simple class name of the models
	 * @param referencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @param query
	 *        a query string to filter the results
	 * @return all models fitting the search parameters
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> List<T> loadFromServer(
		final Class<T> clazz, final String moduleName,
		final String simpleModelName, final boolean referencedHrefs,
		final String query ) throws ApiomatRequestException
	{
		return loadFromServer( clazz,
			createModelHref( moduleName, simpleModelName ), referencedHrefs,
			query );
	}

	/**
	 * Loads existing models from the server in the background
	 * 
	 * @param clazz
	 *        class of the models
	 * @param moduleName
	 *        module name where the models are used in
	 * @param simpleModelName
	 *        the simple class name of the models
	 * @param referencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @param query
	 *        a query string to filter the results
	 * @param getCallback method which will called when request is finished
	 */
	public <T extends AbstractClientDataModel> void loadFromServerAsync(
		final Class<T> clazz, final String moduleName,
		final String simpleModelName, final boolean referencedHrefs,
		final String query, final AOMCallback<List<T>> getCallback )
	{
		loadFromServerAsync( clazz,
			createModelHref( moduleName, simpleModelName ), referencedHrefs,
			query, getCallback );
	}

	/**
	 * Loads existing models from the server
	 * 
	 * @param clazz
	 *        class of the models
	 * @param dataModelHref
	 *        HREF of the models
	 * @param query
	 *        a query string to filter the results
	 * @return all models fitting the search parameters
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public <T extends AbstractClientDataModel> List<T> loadFromServer(
		final Class<T> clazz, final String dataModelHref, final String query )
		throws ApiomatRequestException
	{
		return loadFromServer( clazz, dataModelHref, false, query );
	}

	/**
	 * Loads existing models from the server in the background thread
	 * 
	 * @param clazz
	 *        class of the models
	 * @param dataModelHref
	 *        HREF of the models
	 * @param query
	 *        a query string to filter the results
	 * @param getCallback method which will called when request is finished
	 */
	public <T extends AbstractClientDataModel> void loadFromServerAsync(
		final Class<T> clazz, final String dataModelHref,
		final String query, final AOMCallback<List<T>> getCallback )
	{
		loadFromServerAsync( clazz, dataModelHref, false, query, getCallback );
	}

	/**
	 * Loads existing models from the server
	 * 
	 * @param clazz
	 *        class of the models
	 * @param dataModelHref
	 *        HREF of the models
	 * @param withReferencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @param query
	 *        a query string to filter the results
	 * @return all models fitting the search parameters
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	protected <T extends AbstractClientDataModel> List<T> loadFromServer(
		final Class<T> clazz, final String dataModelHref,
		final boolean withReferencedHrefs, final String query )
		throws ApiomatRequestException
	{
		if ( dataModelHref == null || dataModelHref.equals( "" ) )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND );
		}

		List<T> returnValue = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>( );
		params.add( new BasicNameValuePair( "withReferencedHrefs", String
			.valueOf( withReferencedHrefs ) ) );
		if ( query != null )
		{
			params.add( new BasicNameValuePair( "q", query ) );
		}

		// check if we have etag for this list in modelstore but only if we don't use a query
		AtomicReference<String> eTag = new AtomicReference<String>( );
		if ( cacheStrategy.equals( AOMCacheStrategy.CHECK_SERVER_ELSE_CACHE ) && ( query == null || query.equals( "" ) ) )
		{
			eTag.set( this.modelCache.getEtagForModels( dataModelHref ) );
		}
		AtomicReference<Integer> returnedStatusCode = new AtomicReference<Integer>( );
		List<Integer> expectedCodes = new ArrayList<Integer>( );
		expectedCodes.add( HttpStatus.SC_OK );
		expectedCodes.add( HttpStatus.SC_NOT_MODIFIED );
		expectedCodes.add( HttpStatus.SC_NO_CONTENT );

		String resultStr =
			sendRequest( dataModelHref, query, withReferencedHrefs, new HttpGet( ), null, expectedCodes,
				returnedStatusCode, null, eTag );
		if ( returnedStatusCode.get( ) == HttpStatus.SC_NOT_MODIFIED )
		{
			// Check if in cache and cache strategy is using cache
			if ( cacheStrategy.equals( AOMCacheStrategy.CHECK_SERVER_ELSE_CACHE ) )
			{
				try
				{
					final List<T> cachedDataModels = this.modelCache.getModels( dataModelHref, clazz );
					return cachedDataModels;
				}
				catch ( Exception e )
				{
					throw new ApiomatRequestException( Status.INSTANTIATE_EXCEPTION );
				}
			}
		}
		else if ( returnedStatusCode.get( ) == HttpStatus.SC_OK )
		{
			if ( resultStr != null )
			{
				final JSONArray listOfModels = new JSONArray( resultStr );
				returnValue = new LinkedList<T>( );
				for ( int i = 0; i < listOfModels.length( ); i++ )
				{
					T element;
					try
					{
						final String jsonStr = listOfModels.getString( i );
						element = clazz.newInstance( );
						element.fromJson( jsonStr );
						returnValue.add( element );
					}
					catch ( Exception e )
					{
						throw new ApiomatRequestException( Status.INSTANTIATE_EXCEPTION.getStatusCode( ),
							HttpStatus.SC_OK, e.getMessage( ) );
					}
				}
				// Check if in cache and cache strategy is using cache
				if ( cacheStrategy.equals( AOMCacheStrategy.CHECK_SERVER_ELSE_CACHE ) )
				{
					// if there was an eTag it is set in sendRequest
					this.modelCache.addModels( dataModelHref, eTag.get( ), returnValue );
				}
			}
		}

		return returnValue;
	}

	/**
	 * Loads existing models from the server in the background
	 * 
	 * @param clazz
	 *        class of the models
	 * @param dataModelHref
	 *        HREF of the models
	 * @param withReferencedHrefs
	 *        set to true to get also all HREFs of referenced models
	 * @param query
	 *        a query string to filter the results
	 * @param getCallback method which will called when request is finished
	 */
	protected <T extends AbstractClientDataModel> void loadFromServerAsync(
		final Class<T> clazz, final String dataModelHref,
		final boolean withReferencedHrefs, final String query,
		final AOMCallback<List<T>> getCallback )
	{

		new AOMTask<List<T>>( )
		{
			@Override
			public List<T> doRequest( ) throws ApiomatRequestException
			{
				return loadFromServer( clazz, dataModelHref,
					withReferencedHrefs, query );
			}
		}.execute( getCallback );
	}

	/**
	 * Loads a resource, e.g. an image with the user credentials as byte array.
	 * 
	 * @param href the URl of the image
	 * @return the resource as byte array
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public byte[ ] loadResource( String href ) throws ApiomatRequestException
	{
		byte[ ] result = null;
		try
		{
			HttpGet request = new HttpGet( href );
			if ( this.userName != null && this.password != null )
			{
				request.setHeader( "Authorization", getAuthenticationHeader( ) );
			}
			request.setHeader( "X-apiomat-apikey", this.apiKey );
			request.setHeader( "X-apiomat-sdkVersion", MemberModel.sdkVersion );
			request.setHeader( "X-apiomat-system", this.system );

			HttpResponse httpResponse = getHttpClient( ).execute( request );
			final int statusCode = httpResponse.getStatusLine( ).getStatusCode( );
			final HttpEntity entity = httpResponse.getEntity( );

			if ( HttpStatus.SC_OK == statusCode )
			{
				// Get etag from header if there
				if ( entity != null )
				{
					try
					{
						result = EntityUtils.toByteArray( entity );
					}
					catch ( EOFException e1 )
					{
						// all is ok we will ignore this cause it could be that returned entity is empty
						// e.g. Not-Modified, Update request, etc
					}
				}
			}
			else
			{
				final String errorStr = entity != null ? EntityUtils.toString( entity, "UTF-8" ) : "";
				throw new ApiomatRequestException( statusCode, 200, errorStr, errorStr );
			}
		}
		catch ( ConnectionPoolTimeoutException e )
		{
			throw new ApiomatRequestException(
				Status.IO_EXCEPTION.getStatusCode( ), HttpStatus.SC_OK, e.getMessage( ) );
		}
		catch ( ClientProtocolException e )
		{
			throw new ApiomatRequestException(
				Status.WRONG_CLIENT_PROTOCOL.getStatusCode( ), HttpStatus.SC_OK,
				e.getMessage( ) );
		}
		catch ( IOException e )
		{
			throw new ApiomatRequestException(
				Status.IO_EXCEPTION.getStatusCode( ), HttpStatus.SC_OK, e.getMessage( ) );
		}

		return result;
	}

	/**
	 * Loads a resource, e.g. an image with the user credentials as byte array in the background.
	 * 
	 * @param href the URl of the image
	 * @param callback Method which will called after response from from server returned
	 */
	public void loadResourceAsync( final String href, final AOMCallback<byte[ ]> callback )
	{
		new AOMTask<byte[ ]>( )
		{
			@Override
			public byte[ ] doRequest( ) throws ApiomatRequestException
			{
				return loadResource( href );
			}
		}.execute( callback );
	}

	/**
	 * Deletes the data model from the server
	 * 
	 * @param dataModel
	 *        the data model object
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public void deleteOnServer( AbstractClientDataModel dataModel )
		throws ApiomatRequestException
	{
		deleteOnServer( dataModel.getHref( ) );
	}

	/**
	 * Deletes the data model from the server on a background thread and not on UI
	 * 
	 * @param dataModel
	 *        the data model object
	 * @param callback method which will called after delete request is finished
	 */
	public void deleteOnServerAsync( AbstractClientDataModel dataModel, final AOMEmptyCallback callback )
	{
		final String href = dataModel.getHref( );
		deleteOnServerAsync( href, callback );
	}

	/**
	 * Deletes the data model from the server based on its href
	 * 
	 * @param href
	 *        the data model href
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public void deleteOnServer( String href ) throws ApiomatRequestException
	{
		if ( href == null || href.equals( "" ) )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND );
		}
		sendRequest( href, null, false, new HttpDelete( ), null, 204 );
	}

	/**
	 * Deletes the data model with given href from the server on a background thread and not on UI
	 * 
	 * @param href
	 *        the data model href
	 * @param callback method which will called after delete request is finished
	 */
	public void deleteOnServerAsync( final String href, final AOMEmptyCallback callback )
	{
		new AOMTask<Void>( )
		{
			@Override
			public Void doRequest( ) throws ApiomatRequestException
			{
				deleteOnServer( href );
				return null;
			}
		}.execute( callback );
	}

	/**
	 * Updates the data model from the server
	 * 
	 * @param dataModel
	 *        the data model object
	 * @throws com.apiomat.frontend.ApiomatRequestException
	 */
	public void updateOnServer( AbstractClientDataModel dataModel )
		throws ApiomatRequestException
	{
		if ( dataModel.getHref( ) == null || dataModel.getHref( ).equals( "" ) )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND );
		}
		final String data = dataModel.toJson( );
		try
		{
			sendRequest( dataModel.getHref( ), "", false, new HttpPut( ), new StringEntity( data, "utf-8" ), 200 );
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new ApiomatRequestException(
				Status.UNSUPPORTED_ENCODING.getStatusCode( ), 200,
				e.getMessage( ) );
		}
	}

	/**
	 * Send a PUT request with given json data to the server
	 * 
	 * @param href
	 * @param json
	 * @throws ApiomatRequestException
	 */
	public void updateOnServer( final String href, final String json ) throws ApiomatRequestException
	{
		if ( href == null || href.isEmpty( ) )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND.getStatusCode( ), 1,
				Status.HREF_NOT_FOUND.getReasonPhrase( ) );
		}

		try
		{
			sendRequest( href, "", false, new HttpPut( ), new StringEntity( json, "utf-8" ), 200 );
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new ApiomatRequestException(
				Status.UNSUPPORTED_ENCODING.getStatusCode( ), 200,
				e.getMessage( ) );
		}
	}
	
	/**
	 * Send a PUT request with given json data to the server.
	 * Request works on background thread and not on the UI thread
	 * 
	 * @param href HREF of the model to put (or the address to put the data to)
	 * @param json The JSON string which will be send to server
	 * @param callback The method which will called if response come back from server.
	 * 
	 */
	public void updateOnServerAsync( final String href, final String json, final AOMEmptyCallback callback )
	{
		new AOMTask<Void>( )
		{
			@Override
			public Void doRequest( ) throws ApiomatRequestException
			{
				updateOnServer( href, json );
				return null;
			}
		}.execute( callback );
	}

	public AOMOfflineHandler getOfflineHandler( )
	{
		return offlineHandler;
	}

	private String postOnServer( final HttpEntity entity,
		final String dataModelHref ) throws ApiomatRequestException
	{
		if ( dataModelHref == null || dataModelHref.equals( "" ) )
		{
			throw new ApiomatRequestException( Status.HREF_NOT_FOUND );
		}
		return sendRequest( dataModelHref, "", false, new HttpPost( ), entity, 201 );
	}

	private String getAuthenticationHeader( )
	{
		final String credentials = this.userName + ":" + this.password;
		final String encoded = Base64.encodeToString( credentials.getBytes( ), Base64.NO_WRAP | Base64.NO_PADDING );
		return "Basic " + encoded;
	}

	private String createHref( String href )
	{
		if ( href.startsWith( "http" ) )
		{
			return href;
		}

		if ( href.startsWith( "/apps" ) )
		{
			return this.baseUrl.substring( 0, this.baseUrl.indexOf( "/apps" ) )
				+ href;
		}

		return this.baseUrl + "/" + href;
	}

	public String createStaticDataHref( final boolean image )
	{
		final StringBuilder sb = new StringBuilder( );
		sb.append( this.baseUrl );
		sb.append( "/data/" );
		sb.append( image ? "images" : "files" );
		sb.append( '/' );
		return sb.toString( );
	}

	public String createModelHref( final String moduleName,
		final String simpleModelName )
	{
		final StringBuilder sb = new StringBuilder( );
		sb.append( this.baseUrl );
		sb.append( "/models/" );
		sb.append( moduleName );
		sb.append( '/' );
		sb.append( simpleModelName );
		return sb.toString( );
	}

	private void setHeader( HttpRequest request )
	{
		setHeader( request, false );
	}

	private void setHeader( HttpRequest request, boolean useEncoding )
	{
		request.setHeader( "Content-Type", "application/json" );
		request.setHeader( "Accept", "application/json" );
		request.setHeader( "Authorization", getAuthenticationHeader( ) );
		request.setHeader( "X-apiomat-apikey", this.apiKey );
		request.setHeader( "X-apiomat-sdkVersion", MemberModel.sdkVersion );
		if ( useEncoding )
		{
			request.setHeader( "Accept-Encoding", "gzip" );
		}
	}

	private String sendRequest( String href, String query, boolean withReferencedHrefs, HttpRequestBase request,
		HttpEntity postEntity, final int expectedCode )
		throws ApiomatRequestException
	{
		return sendRequest( href, query, withReferencedHrefs, request, postEntity, new ArrayList<Integer>( )
		{
			{
				add( expectedCode );
			}
		}, null, null );
	}

	private String sendRequest( String href, String query, boolean withReferencedHrefs, HttpRequestBase request,
		HttpEntity postEntity, List<Integer> expectedCodes, AtomicReference<Integer> returnedStatusCode,
		final String lastModified ) throws ApiomatRequestException
	{
		return sendRequest( href, query, withReferencedHrefs, request, postEntity, expectedCodes, returnedStatusCode,
			lastModified, null );
	}

	private String sendRequest( String href, String query, boolean withReferencedHrefs, HttpRequestBase request,
		HttpEntity postEntity, List<Integer> expectedCodes, AtomicReference<Integer> returnedStatusCode,
		final String lastModified, AtomicReference<String> eTag )
		throws ApiomatRequestException
	{
		String resultStr = null;
		try
		{
			String reqUri = href;
			if ( request.getMethod( ).equals( "GET" ) )
			{
				reqUri = Uri.parse( href ).buildUpon( )
					.appendQueryParameter( "withReferencedHrefs", String.valueOf( withReferencedHrefs ) )
					.appendQueryParameter( "q", query == null ? "" : query ).toString( );
			}
			/* check if we've to use the offline store */
			request.setURI( new URI( reqUri ) );
			request.setHeader( "Accept", "application/json" );
			if ( this.userName != null && this.password != null )
			{
				request.setHeader( "Authorization", getAuthenticationHeader( ) );
			}
			request.setHeader( "X-apiomat-apikey", this.apiKey );
			request.setHeader( "X-apiomat-sdkVersion", MemberModel.sdkVersion );
			request.setHeader( "Accept-Encoding", "gzip" );
			request.setHeader( "Content-Type", "application/json" );
			request.setHeader( "X-apiomat-system", this.system );
			request.setHeader( "X-apiomat-fullupdate", "true" );
			if ( lastModified != null && lastModified.equals( "0" ) == false )
			{
				request.setHeader( "If-Modified-Since", lastModified );
			}
			if ( eTag != null )
			{
				request.setHeader( "If-None-Match", eTag.get( ) );
			}
			// set http body if there one
			if ( postEntity != null && request instanceof HttpEntityEnclosingRequestBase )
			{
				( ( HttpEntityEnclosingRequestBase ) request ).setEntity( postEntity );
			}

			HttpResponse httpResponse = getHttpClient( ).execute( request );
			final int statusCode = httpResponse.getStatusLine( ).getStatusCode( );
			if ( returnedStatusCode != null )
			{
				returnedStatusCode.set( statusCode );
			}
			final HttpEntity entity = httpResponse.getEntity( );
			if ( expectedCodes.contains( statusCode ) )
			{
				// Get etag from header if there
				final Header eTagHeader = httpResponse.getFirstHeader( "ETag" );
				if ( eTagHeader != null && eTag != null )
				{
					eTag.set( eTagHeader.getValue( ) );
				}
				// Result string for post methods is the location in header (HREF)
				if ( request.getMethod( ).equals( "POST" ) )
				{
					resultStr = httpResponse.getFirstHeader( "Location" ).getValue( );
				}
				else
				{
					if ( entity != null )
					{
						HttpEntity e = new GzipEntityWrapper( entity );
						try
						{
							resultStr = EntityUtils.toString( e, "UTF-8" );
						}
						catch ( EOFException e1 )
						{
							// all is ok we will ignore this cause it could be that returned entity is empty
							// e.g. Not-Modified, Update request, etc
						}
					}
				}
			}
			else
			{
				HttpEntity e = null;
				Header contentEncoding = httpResponse.getFirstHeader( "Content-Encoding" );
				if ( contentEncoding != null && contentEncoding.getValue( ).equalsIgnoreCase( "gzip" ) )
				{
					e = new GzipEntityWrapper( entity );
				}
				else
				{
					e = entity;
				}
				final String errorStr = e != null ? EntityUtils.toString( e, "UTF-8" ) : "";
				throw new ApiomatRequestException( statusCode, expectedCodes.get( 0 ), errorStr, errorStr );
			}
		}
		catch ( ConnectionPoolTimeoutException e )
		{
			throw new ApiomatRequestException(
				Status.IO_EXCEPTION.getStatusCode( ), expectedCodes.get( 0 ), e.getMessage( ) );
		}
		catch ( URISyntaxException e )
		{
			throw new ApiomatRequestException(
				Status.WRONG_URI_SYNTAX.getStatusCode( ), expectedCodes.get( 0 ), e.getMessage( ) );
		}
		catch ( ClientProtocolException e )
		{
			throw new ApiomatRequestException(
				Status.WRONG_CLIENT_PROTOCOL.getStatusCode( ), expectedCodes.get( 0 ),
				e.getMessage( ) );
		}
		catch ( IOException e )
		{
			throw new ApiomatRequestException(
				Status.IO_EXCEPTION.getStatusCode( ), expectedCodes.get( 0 ), e.getMessage( ) );
		}

		return resultStr;
	}

	/**
	 * Return true if we have to use offline handler
	 * The decision depends on selected {@link com.apiomat.frontend.Datastore.AOMOfflineStrategy} and given _method
	 * 
	 * @param _method The HTTP method
	 * 
	 * @return true if we will use offline store else false
	 */
	public boolean sendOffline( String _method )
	{
		boolean useOffline =
			Datastore.offlineStrategy != null &&
				Datastore.offlineStrategy.equals( AOMOfflineStrategy.NO_OFFLINE_HANDLING ) == false &&
				this.offlineHandler != null && this.offlineHandler.isConnected( ) == false;
		switch ( Datastore.offlineStrategy )
		{
		case USE_OFFLINE_CACHE:
			useOffline &= true;
			break;
		/* case USE_OFFLINE_READONLY:
		 * useOffline &= _method.equals("GET");
		 * break;
		 * case USE_OFFLINE_WRITEONLY:
		 * useOffline &= _method.equals("POST") || _method.equals("PUT") || _method.equals("DELETE");
		 * break; */
		default:
			useOffline = false;
		}
		return useOffline;
	}

	/**
	 * use singleton httpClient
	 * 
	 * @return
	 */
	private DefaultHttpClient getHttpClient( )
	{
		return new DefaultHttpClient( );
	}

	public static enum AOMCacheStrategy
	{
		/**
		 * Use no caching
		 */
		NO_CACHE,
		/**
		 * Only use cache, do not use requests
		 */
		CACHE_ONLY,
		/**
		 * Check server for newest version and use cache if possible
		 */
		CHECK_SERVER_ELSE_CACHE;
	}

	public static enum AOMOfflineStrategy
	{
		NO_OFFLINE_HANDLING,
		USE_OFFLINE_CACHE
	}

	private class GzipEntityWrapper extends HttpEntityWrapper
	{
		public GzipEntityWrapper( HttpEntity wrapped )
		{
			super( wrapped );
		}

		@Override
		public InputStream getContent( ) throws IOException,
			IllegalStateException
		{
			return new GZIPInputStream( this.wrappedEntity.getContent( ) );
		}

		@Override
		public long getContentLength( )
		{
			return -1;
		}
	}
}
