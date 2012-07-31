package com.apiomat.chatomat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.apiomat.chatomat.adapter.ConversationAdapter;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.ConversationModel;

/**
 * First screen showing a list of conversations
 * 
 * @author andreasfey
 */
public class MainActivity extends Activity
{
	private ConversationAdapter adapter;

	static String CONVERSATION = "conv";
	static String USERNAME = "username";
	static String MEMBER = "member";

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
				intent.putExtra( CONVERSATION, ( Serializable ) list.getAdapter( ).getItem( position ) );
				startActivity( intent );
			}
		} );

		/* get member back from store or create a new one */
		SharedPreferences mPrefs = getSharedPreferences( MainActivity.MEMBER, MODE_PRIVATE );
		if ( !mPrefs.contains( "userName" ) || !mPrefs.contains( "password" ) ||
			mPrefs.getString( "password", "" ) == "" )
		{
			Intent intent = new Intent( this, ProfileActivity.class );
			startActivityForResult( intent, 0 );
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

		if ( MemberCache.getMySelf( ) != null )
		{
			@SuppressWarnings( "synthetic-access" )
			LoadConversationsTask task = new LoadConversationsTask( );
			try
			{
				task.execute( );
				this.adapter.clear( );
				for ( ConversationModel cm : task.get( ) )
				{
					this.adapter.add( cm );
				}

			}
			catch ( Exception e )
			{
				Log.e( "fillWithConversations", "Error loading conversations", e );
			}
		}

		/* Start timer to fetch messages periodically */
		this.t = new Timer( );
		this.t.scheduleAtFixedRate( new RefreshConversationsTimer( ), 20000, 10000 );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		super.onActivityResult( requestCode, resultCode, intent );
		if ( requestCode == 0 && resultCode == RESULT_OK )
		{
			Datastore.configure( MemberModel.baseURL, MemberModel.apiKey, MemberCache.getMySelf( ).getUserName( ),
				MemberCache.getMySelf( ).getPassword( ) );
			onResume( );
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
		startActivityForResult( intent, 0 );
	}

	/**
	 * Adds a new conversation, asks for a subject and and opens it
	 * 
	 * @param view
	 */
	public void addConversation( View view )
	{
		LayoutInflater inflater = ( LayoutInflater ) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View popupView = inflater.inflate( R.layout.subject_popup, null, false );
		final PopupWindow pw = new PopupWindow( popupView, 300, 260, true );
		final EditText subject = ( EditText ) popupView.findViewById( R.id.subjectText );
		final Button okButton = ( Button ) popupView.findViewById( R.id.subjectTextOK );

		okButton.setOnClickListener( new OnClickListener( )
		{
			@SuppressWarnings( "synthetic-access" )
			@Override
			public void onClick( View v )
			{
				if ( subject.getText( ).length( ) == 0 )
				{
					AlertDialog alert = new AlertDialog.Builder( MainActivity.this ).create( );
					alert.setCancelable( true );
					alert.setTitle( "Subject must not be empty" );
					alert.setMessage( "We need a subject to create a new conversation." );
					alert.show( );
					return;
				}

				pw.dismiss( );

				AddConversationsTask task = new AddConversationsTask( );
				task.execute( subject.getText( ).toString( ) );

				try
				{
					ConversationModel conversation = task.get( );
					if ( conversation != null )
					{
						MainActivity.this.adapter.add( conversation );
						Intent intent = new Intent( MainActivity.this, SubjectActivity.class );
						intent.putExtra( CONVERSATION, conversation );
						startActivity( intent );
					}
					else
					{
						AlertDialog alert = new AlertDialog.Builder( MainActivity.this ).create( );
						alert.setCancelable( true );
						alert.setTitle( "Error creating conversation" );
						alert.setMessage( "Sorry, new conversation could not be created. Just try it again." );
						alert.show( );
						return;
					}
				}
				catch ( Exception e )
				{
					Log.e( "MainActivity", "Error creating new conversation", e );
				}
			}
		} );
		pw.showAtLocation( view, Gravity.CENTER, 0, 0 );
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

	private class AddConversationsTask extends AsyncTask<String, Void, ConversationModel>
	{
		@Override
		protected ConversationModel doInBackground( String... subject )
		{
			try
			{
				ConversationModel cm = new ConversationModel( );
				cm.setAttendeeUserNames( Arrays.asList( new String[ ] { MemberCache.getMyself( ) } ) );
				cm.setSubject( subject[ 0 ] );
				cm.save( );

				return cm;
			}
			catch ( Exception e )
			{
				Log.e( "AddConversationsTask", "Error creating conversation", e );
				return null;
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
				for ( ConversationModel mm : t.get( ) )
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
						MainActivity.this.adapter.add( mm );
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
