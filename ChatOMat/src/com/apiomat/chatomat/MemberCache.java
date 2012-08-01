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
 * OF THE POSSIBILITY OF SUCH DAMAGE. */
package com.apiomat.chatomat;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.MemberModel;

/**
 * Small cache which stores all member objects and their images
 * 
 * @author andreasfey
 */
public class MemberCache
{
	private final Map<String, MemberModel> mapUserNameToMember = new HashMap<String, MemberModel>( );
	private final Map<String, Bitmap> mapUsernameToImage = new HashMap<String, Bitmap>( );
	private String myself;

	private static MemberCache instance;

	private MemberCache( )
	{}

	private static MemberCache getInstance( )
	{
		if ( instance == null )
		{
			instance = new MemberCache( );
		}
		return instance;
	}

	public static boolean containsMember( String userName )
	{
		return getInstance( ).mapUserNameToMember.containsKey( userName );
	}

	public static boolean containsImage( String userName )
	{
		return getInstance( ).mapUsernameToImage.containsKey( userName ) &&
			getInstance( ).mapUsernameToImage.get( userName ) != null;
	}

	public static MemberModel getMember( String userName )
	{
		return getInstance( ).mapUserNameToMember.get( userName );
	}

	public static Bitmap getImage( String userName )
	{
		try
		{
			if ( !getInstance( ).mapUsernameToImage.containsKey( userName ) )
			{
				LoadAttendeeImageTask task = getInstance( ).new LoadAttendeeImageTask( );
				task.execute( userName );
				getInstance( ).mapUsernameToImage.put( userName, task.get( ) );
			}

			return getInstance( ).mapUsernameToImage.get( userName );
		}
		catch ( Exception e )
		{
			Log.e( "MemberCache", "Could not load member", e );
			return null;
		}
	}

	public static MemberModel getMySelf( )
	{
		return getInstance( ).mapUserNameToMember.get( getInstance( ).myself );
	}

	public static Bitmap getMySelfImage( )
	{
		return getInstance( ).mapUsernameToImage.get( getInstance( ).myself );
	}

	public static void putMember( MemberModel member )
	{
		if ( member != null )
		{
			getInstance( ).mapUserNameToMember.put( member.getUserName( ), member );
		}
	}

	public static void putImage( String userName, Bitmap image )
	{
		if ( image != null )
		{
			getInstance( ).mapUsernameToImage.put( userName, image );
		}
	}

	public static final String getMyself( )
	{
		return getInstance( ).myself;
	}

	public static final void setMyself( String myself )
	{
		getInstance( ).myself = myself;
	}

	public static MemberModel loadMemberToCache( String userName, String password )
	{
		try
		{
			if ( !containsMember( userName ) )
			{
				MemberModel m = new MemberModel( );
				m.setUserName( userName );
				m.setPassword( password );

				CreateOrLoadMemberTask task = getInstance( ).new CreateOrLoadMemberTask( );
				task.execute( m );
				getInstance( ).mapUserNameToMember.put( userName, task.get( ) );
			}

			return getMember( userName );
		}
		catch ( Exception e )
		{
			Log.e( "MemberCache", "Could not load member", e );
			return null;
		}
	}

	private class LoadAttendeeImageTask extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground( String... userName )
		{
			try
			{
				MemberModel mm = getMember( userName[ 0 ] );
				URL newurl = new URL( mm.getImageURL( ) );
				Bitmap bm = BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );
				return bm;
			}
			catch ( Exception e )
			{
				Log.i( "LoadAttendeeImageTask", "Could not load member image" );
			}
			return null;
		}
	}

	private class CreateOrLoadMemberTask extends AsyncTask<MemberModel, Void, MemberModel>
	{
		@Override
		protected MemberModel doInBackground( MemberModel... m )
		{
			try
			{
				MemberModel member = m[ 0 ];
				if ( member.getUserName( ) == "" )
				{
					member.setUserName( member.getFirstName( ) +
						member.getLastName( ) );
				}

				Datastore.configure( MemberModel.baseURL, MemberModel.apiKey,
					member.getUserName( ),
					member.getPassword( ) );

				member.loadMe( );
				if ( member.getHref( ) == null )
				{
					member.save( );
				}
				return member;
			}
			catch ( Exception e )
			{
				Log.w( "MemberCache", "Error loading member", e );
			}
			return null;
		}
	}
}
