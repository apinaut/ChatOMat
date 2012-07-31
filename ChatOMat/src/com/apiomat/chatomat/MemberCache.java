/* Copyright (c) 2007 - 2011 All Rights Reserved, http://www.match2blue.com/
 * 
 * This source is property of match2blue.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 * 
 * match2blue software development GmbH, Leutragraben 1, 07743 Jena, Germany
 * 
 * 30.07.2012
 * andreasfey */
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
		return getInstance( ).mapUsernameToImage.containsKey( userName );
	}

	public static MemberModel getMember( String userName )
	{
		return getInstance( ).mapUserNameToMember.get( userName );
	}

	public static Bitmap getImage( String userName )
	{
		try
		{
			if ( !containsImage( userName ) )
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
		getInstance( ).mapUserNameToMember.put( member.getUserName( ), member );
	}

	public static void putImage( String userName, Bitmap image )
	{
		getInstance( ).mapUsernameToImage.put( userName, image );
	}

	public static final String getMyself( )
	{
		return getInstance( ).myself;
	}

	public static final void setMyself( String myself )
	{
		getInstance( ).myself = myself;
	}

	public static MemberModel loadMyselfToCache( String userName, String password )
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

			setMyself( userName );
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
