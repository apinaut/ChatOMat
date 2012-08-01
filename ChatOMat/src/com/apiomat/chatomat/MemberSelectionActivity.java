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
 * OF THE POSSIBILITY OF SUCH DAMAGE. */package com.apiomat.chatomat;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.apiomat.chatomat.adapter.MemberAdapter;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.ConversationModel;
import com.apiomat.frontend.chat.MessageModel;

/**
 * Acitivity which shows a list of members; the user can select one to add him to a new or existing conversation. <br/>
 * This activity may be called by pressing the plus button either from the {@link MainActivity}, which means member for
 * a <b>new</b> conversation is selected, or from the {@link SubjectActivity}, which means a member will be added to an
 * existing conversation.
 * 
 * @author andreasfey
 */
public class MemberSelectionActivity extends Activity
{
	private ConversationModel conv;
	private MemberAdapter adapter;
	boolean startNewConversation = true;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_member_selection );

		final Intent i = getIntent( );
		if ( i.getExtras( ) != null && i.getExtras( ).containsKey( MainActivity.EXTRA_CONVERSATION ) )
		{
			this.startNewConversation = false;
			this.conv =
				( ConversationModel ) i.getExtras( ).getSerializable( MainActivity.EXTRA_CONVERSATION );
		}

		final ListView list = ( ListView ) findViewById( R.id.listViewAttendees );
		this.adapter = new MemberAdapter( this );
		list.setAdapter( this.adapter );
		list.setOnItemClickListener( new OnItemClickListener( )
		{
			@Override
			public void onItemClick( AdapterView<?> parent, View view,
				int position, long id )
			{
				if ( MemberSelectionActivity.this.startNewConversation )
				{
					addConversation( view, ( MemberModel ) list.getItemAtPosition( position ) );
				}
				else
				{
					addAttendee( ( MemberModel ) list.getItemAtPosition( position ) );

					Intent intent = new Intent( );
					MemberModel m = ( MemberModel ) list.getItemAtPosition( position );
					intent.putExtra( MainActivity.EXTRA_USERNAME, m.getUserName( ) );
					setResult( RESULT_OK, intent );
					finish( );
				}
			}

		} );

		LoadMembersTask task = new LoadMembersTask( );
		task.execute( );
		try
		{
			this.adapter.clear( );
			for ( MemberModel m : task.get( ) )
			{
				this.adapter.add( m );
			}
		}
		catch ( Exception e )
		{
			Log.e( "AttendeeAdapter", "Error loading members of app", e );
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater( ).inflate( R.menu.activity_attendee_selection, menu );
		return true;
	}

	/** pass back results to main screen */
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		super.onActivityResult( requestCode, resultCode, intent );
		if ( resultCode == RESULT_OK )
		{
			MemberModel sender = ( MemberModel ) intent.getExtras( ).getSerializable( MainActivity.EXTRA_MEMBER );
			MessageModel msg = ( MessageModel ) intent.getExtras( ).getSerializable( MainActivity.EXTRA_LAST_MESSAGE );

			setResult( RESULT_OK, intent );
			intent.putExtra( MainActivity.EXTRA_LAST_MESSAGE, msg );
			intent.putExtra( MainActivity.EXTRA_MEMBER, sender );
		}
		finish( );
	}

	/**
	 * Navigates back to the main activity
	 * 
	 * @param view
	 */
	public void goBack( @SuppressWarnings( "unused" ) View view )
	{
		finish( );
	}

	private void addAttendee( final MemberModel m )
	{
		AddAttendeeTask task = new AddAttendeeTask( );
		task.execute( m );
		try
		{
			task.get( );
		}
		catch ( Exception e )
		{
			Log.e( "MemberAdapter", "Error saving attendees", e );
		}
	}

	private void addConversation( View view, final MemberModel m )
	{
		LayoutInflater inflater = ( LayoutInflater ) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View popupView = inflater.inflate( R.layout.subject_popup, null, false );
		final PopupWindow pw = new PopupWindow( popupView, 300, 260, true );
		final EditText subject = ( EditText ) popupView.findViewById( R.id.subjectText );
		final Button okButton = ( Button ) popupView.findViewById( R.id.subjectTextOK );
		final Button cancelButton = ( Button ) popupView.findViewById( R.id.subjectTextCancel );

		cancelButton.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View paramView )
			{
				pw.dismiss( );
			}
		} );
		okButton.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				if ( subject.getText( ).length( ) == 0 )
				{
					AlertDialog alert = new AlertDialog.Builder( MemberSelectionActivity.this ).create( );
					alert.setCancelable( true );
					alert.setTitle( "Subject must not be empty" );
					alert.setMessage( "We need a subject to create a new conversation." );
					alert.show( );
					return;
				}

				pw.dismiss( );

				try
				{
					AddConversationsTask task = new AddConversationsTask( );
					task.execute( subject.getText( ).toString( ) );
					task.get( );

					addAttendee( m );

					Intent intent = new Intent( MemberSelectionActivity.this, SubjectActivity.class );
					intent.putExtra( MainActivity.EXTRA_CONVERSATION, MemberSelectionActivity.this.conv );
					startActivityForResult( intent, MainActivity.EXPECTED_SUBJECT_CODE );
				}
				catch ( Exception e )
				{
					Log.e( "MainActivity", "Error creating new conversation", e );
				}
			}
		} );
		pw.showAtLocation( view, Gravity.CENTER, 0, 0 );
	}

	private class AddAttendeeTask extends AsyncTask<MemberModel, Void, Void>
	{
		@Override
		protected Void doInBackground( MemberModel... member )
		{
			try
			{
				List<String> userNames = MemberSelectionActivity.this.conv.getAttendeeUserNames( );
				userNames.add( member[ 0 ].getUserName( ) );
				MemberSelectionActivity.this.conv.setAttendeeUserNames( userNames );
				MemberSelectionActivity.this.conv.save( );
			}
			catch ( Exception e )
			{
				Log.e( "MemberAdapter", "Error saving attendees", e );
			}
			return null;
		}
	}

	/**
	 * Load all existing members from server for this app, excluding all current attendees
	 * 
	 * @author andreasfey
	 */
	private class LoadMembersTask extends AsyncTask<Void, Void, List<MemberModel>>
	{
		@Override
		protected List<MemberModel> doInBackground( Void... nox )
		{
			try
			{
				StringBuffer filter = new StringBuffer( );
				if ( MemberSelectionActivity.this.conv != null )
				{
					for ( String attendee : ( List<String> ) MemberSelectionActivity.this.conv.getAttendeeUserNames( ) )
					{
						if ( filter.length( ) > 0 )
						{
							filter.append( " AND " );
						}
						filter.append( "userName != \"" + attendee + "\"" );
					}
				}
				else
				{
					filter.append( "userName != \"" + MemberCache.getMyself( ) + "\"" );
				}
				MemberModel mm = new MemberModel( );
				List<MemberModel> models =
					Datastore.getInstance( ).loadFromServer( MemberModel.class, mm.getModuleName( ),
						mm.getSimpleName( ), filter.toString( ) );
				return models;
			}
			catch ( Exception e )
			{
				Log.e( "MemberAdapter", "Error loading members of app", e );
				return null;
			}
		}
	}

	private class AddConversationsTask extends AsyncTask<String, Void, Void>
	{
		@Override
		protected Void doInBackground( String... subject )
		{
			try
			{
				MemberSelectionActivity.this.conv = new ConversationModel( );
				MemberSelectionActivity.this.conv.setAttendeeUserNames( Arrays.asList( new String[ ] { MemberCache
					.getMyself( ) } ) );
				MemberSelectionActivity.this.conv.setSubject( subject[ 0 ] );
				MemberSelectionActivity.this.conv.save( );
			}
			catch ( Exception e )
			{
				Log.e( "AddConversationsTask", "Error creating conversation", e );
			}
			return null;
		}
	}

}
