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

/**
 * Activity which shows the conversation details, starting with the attendees, the subject and all messages als baloons
 * 
 * @author andreasfey
 */
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
					MessageModel mm = new MessageModel( );
					mm.setText( newMessage.getText( ).toString( ) );
					mm.setSenderUserName( MemberCache.getMyself( ) );

					SubjectActivity.this.messageAdapter.add( mm );
					newMessage.setText( "" );
					lst.setSelection( lst.getCount( ) - 1 );

					try
					{
						AddMessagesTask task = new AddMessagesTask( );
						task.execute( mm );
					}
					catch ( Exception e )
					{
						Log.e( "SubjectActivity", "Error creating new message", e );
					}
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

	private class AddMessagesTask extends AsyncTask<MessageModel, Void, Void>
	{
		@Override
		protected Void doInBackground( MessageModel... msg )
		{
			try
			{
				msg[ 0 ].save( );
				SubjectActivity.this.conv.postMessages( msg[ 0 ] );
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
						if ( SubjectActivity.this.messageAdapter.getItem( i ).getHref( ) == null )
						{
							SubjectActivity.this.messageAdapter
								.remove( SubjectActivity.this.messageAdapter.getItem( i ) );
							break;
						}
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
