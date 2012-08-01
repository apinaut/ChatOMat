/* Copyright (c) 2012, Apinauten UG (haftungsbeschraenkt)
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
package com.apiomat.frontend.basics;

import java.util.Date;

import rpc.json.me.JSONArray;

import com.apiomat.frontend.AbstractClientDataModel;
import com.apiomat.frontend.Datastore;

/**
 * Generated default class representing a user in your app
 */
public class MemberModel extends AbstractClientDataModel
{
	public static String apiKey = "1911826875896020520";
	// public static String apiKey = "848158150886472345";
	public static String baseURL = "http://ec2-54-247-10-207.eu-west-1.compute.amazonaws.com/yambas/rest/apps/Chat";

	// public static String baseURL = "http://192.168.6.8:8080/yambas/rest/apps/Chat";

	/**
	 * Default constructor. Needed for internal processing.
	 */
	public MemberModel( )
	{
		super( );
	}

	/**
	 * Returns the simple name of this class
	 */
	@Override
	public String getSimpleName( )
	{
		return "MemberModel";
	}

	/**
	 * Returns the name of the module where this class belongs to
	 */
	@Override
	public String getModuleName( )
	{
		return "Basics";
	}

	/**
	 * Returns the system to connect to
	 */
	@Override
	public String getSystem( )
	{
		return "";
	}

	/**
	 * Updates this class from server
	 */
	public void loadMe( ) throws Exception
	{
		load( "models/me" );
	}

	public double getLocLatitude( )
	{
		final JSONArray loc = this.data.optJSONArray( "loc" );
		final Object raw = loc.get( 0 );

		return convertNumberToDouble( raw );
	}

	public double getLocLongitude( )
	{
		final JSONArray loc = this.data.optJSONArray( "loc" );
		final Object raw = loc.get( 1 );

		return convertNumberToDouble( raw );
	}

	public void setLocLatitude( double latitude )
	{
		if ( this.data.has( "loc" ) == false )
		{
			this.data.put( "loc", new JSONArray( ) );
		}

		this.data.getJSONArray( "loc" ).put( 0, latitude );
	}

	public void setLocLongitude( double longitude )
	{
		if ( this.data.has( "loc" ) == false )
		{
			this.data.put( "loc", new JSONArray( ) );
		}

		this.data.getJSONArray( "loc" ).put( 1, longitude );
	}

	public Integer getAge( )
	{
		if ( this.data.optJSONObject( "dynamicAttributes" ).isNull( "age" ) )
		{
			return null;
		}
		return ( Integer ) this.data.optJSONObject( "dynamicAttributes" ).get( "age" );
	}

	public void setAge( Integer arg )
	{
		Integer age = arg;
		this.data.optJSONObject( "dynamicAttributes" ).put( "age", age );
	}

	public String getCompany( )
	{
		if ( this.data.optJSONObject( "dynamicAttributes" ).isNull( "company" ) )
		{
			return null;
		}
		return ( String ) this.data.optJSONObject( "dynamicAttributes" ).get( "company" );
	}

	public void setCompany( String arg )
	{
		String company = arg;
		this.data.optJSONObject( "dynamicAttributes" ).put( "company", company );
	}

	public String getUserName( )
	{
		return this.data.optString( "userName" );
	}

	public void setUserName( String arg )
	{
		String userName = arg;
		this.data.put( "userName", userName );
	}

	public String getPassword( )
	{
		return this.data.optString( "password" );
	}

	public void setPassword( String arg )
	{
		String password = arg;
		this.data.put( "password", password );
	}

	public String getLastName( )
	{
		return this.data.optString( "lastName" );
	}

	public void setLastName( String arg )
	{
		String lastName = arg;
		this.data.put( "lastName", lastName );
	}

	public String getImageURL( )
	{
		if ( this.data.optJSONObject( "dynamicAttributes" ).isNull( "imageURL" ) )
		{
			return null;
		}
		return ( String ) this.data.optJSONObject( "dynamicAttributes" ).get( "imageURL" ) + ".img?apiKey=" + apiKey;
	}

	public String postImage( byte[ ] data ) throws Exception
	{
		String href = Datastore.getInstance( ).postStaticDataOnServer( data, true );
		this.data.optJSONObject( "dynamicAttributes" ).put( "imageURL", href );
		this.save( );
		return href;
	}

	public boolean deleteImage( ) throws Exception
	{
		return Datastore.getInstance( ).deleteOnServer( getImageURL( ) );
	}

	public String getProfession( )
	{
		if ( this.data.optJSONObject( "dynamicAttributes" ).isNull( "profession" ) )
		{
			return null;
		}
		return ( String ) this.data.optJSONObject( "dynamicAttributes" ).get( "profession" );
	}

	public void setProfession( String arg )
	{
		String profession = arg;
		this.data.optJSONObject( "dynamicAttributes" ).put( "profession", profession );
	}

	public String getSex( )
	{
		if ( this.data.optJSONObject( "dynamicAttributes" ).isNull( "sex" ) )
		{
			return null;
		}
		return ( String ) this.data.optJSONObject( "dynamicAttributes" ).get( "sex" );
	}

	public void setSex( String arg )
	{
		String sex = arg;
		this.data.optJSONObject( "dynamicAttributes" ).put( "sex", sex );
	}

	public Date getDateOfBirth( )
	{
		return new Date( this.data.getLong( "dateOfBirth" ) );
	}

	public void setDateOfBirth( Date dateOfBirth )
	{
		this.data.putOpt( "dateOfBirth", dateOfBirth.getTime( ) );
	}

	public String getFirstName( )
	{
		return this.data.optString( "firstName" );
	}

	public void setFirstName( String arg )
	{
		String firstName = arg;
		this.data.put( "firstName", firstName );
	}

}
