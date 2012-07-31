package com.apiomat.chatomat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.apiomat.chatomat.adapter.MemberAdapter;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.ConversationModel;

public class MemberSelectionActivity extends Activity
{
	private ConversationModel conv;
	private MemberAdapter adapter;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_member_selection );

		final Intent i = getIntent( );
		this.conv =
			( ConversationModel ) i.getExtras( ).getSerializable( MainActivity.EXTRA_CONVERSATION );

		final ListView list = ( ListView ) findViewById( R.id.listViewAttendees );
		this.adapter = new MemberAdapter( this );
		list.setAdapter( this.adapter );
		list.setOnItemClickListener( new OnItemClickListener( )
		{
			@Override
			public void onItemClick( AdapterView<?> parent, View view,
				int position, long id )
			{
				AddAttendeeTask task = new AddAttendeeTask( );
				task.execute( ( MemberModel ) list.getItemAtPosition( position ) );
				try
				{
					task.get( );
				}
				catch ( Exception e )
				{
					Log.e( "MemberAdapter", "Error saving attendees", e );
				}
				Intent intent = new Intent( );
				MemberModel m = ( MemberModel ) list.getItemAtPosition( position );
				intent.putExtra( MainActivity.EXTRA_USERNAME, m.getUserName( ) );
				setResult( RESULT_OK, intent );
				finish( );
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

	/**
	 * Navigates back to the main activity
	 * 
	 * @param view
	 */
	public void goBack( View view )
	{
		finish( );
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

	private class LoadMembersTask extends AsyncTask<Void, Void, List<MemberModel>>
	{
		@Override
		protected List<MemberModel> doInBackground( Void... nox )
		{
			try
			{
				StringBuffer filter = new StringBuffer( );
				for ( String attendee : ( List<String> ) MemberSelectionActivity.this.conv.getAttendeeUserNames( ) )
				{
					if ( filter.length( ) > 0 )
					{
						filter.append( " AND " );
					}
					filter.append( "userName != \"" + attendee + "\"" );
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
}
