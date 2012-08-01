package com.apiomat.chatomat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.apiomat.chatomat.adapter.ConversationAdapter;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.ConversationModel;
import com.apiomat.frontend.chat.MessageModel;

/**
 * First screen showing a list of conversations
 * 
 * @author andreasfey
 */
public class MainActivity extends Activity
{
	public static final String EXTRA_POSITION = "position";
	public static final String EXTRA_CONVERSATION = "conv";
	public static final String EXTRA_USERNAME = "username";
	public static final String EXTRA_MEMBER = "member";
	public static final String EXTRA_LAST_MESSAGE = "lastMessageText";

	static final int EXPECTED_SUBJECT_CODE = 1;
	static final int EXPECTED_PROFILE_CODE = 0;

	private ConversationAdapter adapter;
	private Timer t;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		this.adapter = new ConversationAdapter( this );

		final ListView list = ( ListView ) findViewById( R.id.conversationsList );
		list.setAdapter( this.adapter );
		list.setOnItemClickListener( new OnItemClickListener( )
		{
			@Override
			public void onItemClick( AdapterView<?> parent, View view,
				int position, long id )
			{
				Intent intent = new Intent( parent.getContext( ), SubjectActivity.class );
				intent.putExtra( EXTRA_CONVERSATION, ( Serializable ) list.getAdapter( ).getItem( position ) );
				startActivityForResult( intent, EXPECTED_SUBJECT_CODE );
			}
		} );

		/* get member back from store or create a new one */
		SharedPreferences mPrefs = getSharedPreferences( MainActivity.EXTRA_MEMBER, MODE_PRIVATE );
		if ( !mPrefs.contains( "userName" ) || !mPrefs.contains( "password" ) ||
			mPrefs.getString( "password", "" ) == "" )
		{
			Intent intent = new Intent( this, ProfileActivity.class );
			startActivityForResult( intent, EXPECTED_PROFILE_CODE );
		}
		else
		{
			MemberCache.loadMyselfToCache( mPrefs.getString( "userName", "" ), mPrefs.getString( "password", "" ) );
		}
	}

	@Override
	protected void onPause( )
	{
		super.onPause( );
		this.t.cancel( );
	}

	@Override
	protected void onResume( )
	{
		super.onResume( );

		this.t = new Timer( );
		if ( MemberCache.getMySelf( ) != null )
		{
			/* Start timer to fetch messages periodically */
			this.t.scheduleAtFixedRate( new RefreshConversationsTimer( ), 0, 10000 );
		}
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		super.onActivityResult( requestCode, resultCode, intent );
		if ( requestCode == EXPECTED_PROFILE_CODE )
		{
			if ( resultCode == RESULT_OK )
			{
				Datastore.configure( MemberModel.baseURL, MemberModel.apiKey, MemberCache.getMySelf( ).getUserName( ),
					MemberCache.getMySelf( ).getPassword( ) );
				onResume( );
			}
			else
			{
				Datastore.configure( MemberModel.baseURL, MemberModel.apiKey, "", "" );
				this.adapter.clear( );
				AlertDialog dialog = new AlertDialog.Builder( this ).create( );
				dialog.setMessage( "You have to create a profile using the apinaut button before going on!" );
				dialog.show( );
			}
		}
		else if ( requestCode == EXPECTED_SUBJECT_CODE && resultCode == RESULT_OK )
		{
			MemberModel sender = ( MemberModel ) intent.getExtras( ).getSerializable( EXTRA_MEMBER );
			MessageModel msg = ( MessageModel ) intent.getExtras( ).getSerializable( EXTRA_LAST_MESSAGE );
			this.adapter.setLastMessage( msg, sender );
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater( ).inflate( R.menu.activity_main, menu );
		return true;
	}

	/**
	 * Opens the profile activity
	 * 
	 * @param view
	 */
	public void openProfile( @SuppressWarnings( "unused" ) View view )
	{
		Intent intent = new Intent( this, ProfileActivity.class );
		startActivityForResult( intent, EXPECTED_PROFILE_CODE );
	}

	/**
	 * Opens the list of members
	 * 
	 * @param view
	 */
	public void addAttendee( @SuppressWarnings( "unused" ) View view )
	{
		Intent intent = new Intent( this, MemberSelectionActivity.class );
		startActivity( intent );
	}

	private class LoadConversationsTask extends AsyncTask<Void, Void, List<ConversationModel>>
	{
		@Override
		protected List<ConversationModel> doInBackground( Void... nothing )
		{
			try
			{
				return ConversationModel.getConversationModels( "" );
			}
			catch ( Exception e )
			{
				Log.e( "LoadConversationsTask", "Error loading conversations", e );
				return new ArrayList<ConversationModel>( );
			}
		}
	}

	private class RefreshConversationsTimer extends TimerTask
	{
		@Override
		public void run( )
		{
			LoadConversationsTask t = new LoadConversationsTask( );
			t.execute( );
			try
			{
				for ( final ConversationModel mm : t.get( ) )
				{
					boolean alreadyExists = false;
					for ( int i = 0; i < MainActivity.this.adapter.getCount( ); i++ )
					{
						if ( MainActivity.this.adapter.getItem( i ).getHref( ).equals( mm.getHref( ) ) )
						{
							alreadyExists = true;
							break;
						}
					}
					if ( !alreadyExists )
					{
						MainActivity.this.runOnUiThread( new Runnable( )
						{
							@Override
							public void run( )
							{
								MainActivity.this.adapter.add( mm );
							}
						} );
					}
				}
			}
			catch ( Exception e )
			{
				Log.e( "RefreshConversationsTimer", "Error refreshing conversations", e );
			}
		}
	}
}
