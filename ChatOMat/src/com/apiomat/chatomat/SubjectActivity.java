package com.apiomat.chatomat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.apiomat.chatomat.adapter.AttendeeAdapter;
import com.apiomat.chatomat.adapter.MessageAdapter;
import com.apiomat.frontend.chat.ConversationModel;
import com.apiomat.frontend.chat.MessageModel;

public class SubjectActivity extends Activity
{
	private ConversationModel conv;
	private MessageAdapter messageAdapter;
	private AttendeeAdapter attendeeAdapter;
	private Timer t;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_subject );

		Intent i = getIntent( );
		this.conv =
			( ConversationModel ) i.getExtras( ).getSerializable( MainActivity.EXTRA_CONVERSATION );

		/* Draw grid of attendees */
		final GridView list = ( GridView ) findViewById( R.id.attendeesList );
		this.attendeeAdapter = new AttendeeAdapter( this, this.conv.getAttendeeUserNames( ) );
		list.setAdapter( this.attendeeAdapter );

		/* Draw subject title */
		TextView subjectText = ( TextView ) findViewById( R.id.subjectTitle );
		subjectText.setText( "Conversation subject: " + this.conv.getSubject( ) );

		/* Draw messages */
		final ListView lst = ( ( ListView ) findViewById( R.id.messageList ) );
		final ListView mlist = ( ListView ) findViewById( R.id.messageList );
		this.messageAdapter = new MessageAdapter( this, MemberCache.getMyself( ) );
		mlist.setAdapter( this.messageAdapter );

		/* new message */
		final EditText newMessage = ( EditText ) findViewById( R.id.newMessageText );
		newMessage.setOnEditorActionListener( new OnEditorActionListener( )
		{
			@Override
			public boolean onEditorAction( TextView paramTextView, int paramInt, KeyEvent paramKeyEvent )
			{
				if ( newMessage.getText( ).toString( ).length( ) > 0 )
				{
					AddMessagesTask task = new AddMessagesTask( );
					task.execute( newMessage.getText( ).toString( ) );

					try
					{
						MessageModel mm = task.get( );
						SubjectActivity.this.messageAdapter.add( mm );
					}
					catch ( Exception e )
					{
						Log.e( "SubjectActivity", "Error creating new message", e );
					}
					newMessage.setText( "" );

					lst.setSelection( lst.getCount( ) - 1 );
				}
				return false;
			}
		} );

	}

	/**
	 * Navigates back to the main activity
	 * 
	 * @param view
	 */
	public void goBack( @SuppressWarnings( "unused" ) View view )
	{
		/* called from Main screen */
		Intent intent = new Intent( );

		if ( this.messageAdapter.getCount( ) > 0 )
		{
			setResult( RESULT_OK, intent );
			MessageModel msg = this.messageAdapter.getItem( this.messageAdapter.getCount( ) - 1 );
			intent.putExtra( MainActivity.EXTRA_LAST_MESSAGE, msg );
			intent.putExtra( MainActivity.EXTRA_MEMBER, this.messageAdapter.getMemberFromLastMessage( ) );
		}
		else
		{
			setResult( RESULT_CANCELED, intent );
		}
		finish( );
	}

	/**
	 * Opens the list of members
	 * 
	 * @param view
	 */
	public void addAttendee( @SuppressWarnings( "unused" ) View view )
	{
		Intent intent = new Intent( this, MemberSelectionActivity.class );
		intent.putExtra( MainActivity.EXTRA_CONVERSATION, this.conv );
		startActivityForResult( intent, 0 );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		super.onActivityResult( requestCode, resultCode, intent );
		if ( requestCode == 0 && resultCode == RESULT_OK )
		{
			String newAttendee = intent.getExtras( ).getString( MainActivity.EXTRA_USERNAME );
			this.attendeeAdapter.add( newAttendee );
		}
	}

	@Override
	protected void onResume( )
	{
		super.onRestart( );

		/* Start timer to fetch messages periodically */
		this.t = new Timer( );
		this.t.scheduleAtFixedRate( new RefreshMessagesTimer( ), 0, 20000 );
	}

	@Override
	protected void onPause( )
	{
		super.onPause( );
		this.t.cancel( );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater( ).inflate( R.menu.activity_subject, menu );
		return true;
	}

	private class LoadConversationMessagesTask extends
		AsyncTask<Void, Void, List<MessageModel>>
	{
		@Override
		protected List<MessageModel> doInBackground( Void... nix )
		{
			try
			{
				return SubjectActivity.this.conv.loadMessages( "" );
			}
			catch ( Exception e )
			{
				Log.e( "LoadConversationsTask", "Error loading messages", e );
				return new ArrayList<MessageModel>( );
			}
		}
	}

	private class AddMessagesTask extends AsyncTask<String, Void, MessageModel>
	{
		@Override
		protected MessageModel doInBackground( String... msg )
		{
			try
			{
				MessageModel mm = new MessageModel( );
				mm.setText( msg[ 0 ] );
				mm.save( );
				mm.postSender( MemberCache.getMySelf( ) );

				SubjectActivity.this.conv.postMessages( mm );

				return mm;
			}
			catch ( Exception e )
			{
				Log.e( "AddMessagesTask", "Error creating message", e );
			}
			return null;
		}
	}

	private class RefreshMessagesTimer extends TimerTask
	{
		@Override
		public void run( )
		{
			LoadConversationMessagesTask t = new LoadConversationMessagesTask( );
			t.execute( );
			try
			{
				for ( final MessageModel mm : t.get( ) )
				{
					boolean alreadyExists = false;
					for ( int i = 0; i < SubjectActivity.this.messageAdapter.getCount( ); i++ )
					{
						if ( SubjectActivity.this.messageAdapter.getItem( i ).getHref( ).equals( mm.getHref( ) ) )
						{
							alreadyExists = true;
							break;
						}
					}
					if ( !alreadyExists )
					{
						SubjectActivity.this.runOnUiThread( new Runnable( )
						{
							@Override
							public void run( )
							{
								SubjectActivity.this.messageAdapter.add( mm );
							}
						} );

					}
				}
			}
			catch ( Exception e )
			{
				Log.e( "RefreshMessagesTimer", "Error refreshing messages", e );
			}
		}
	}
}
