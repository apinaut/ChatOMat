/* Copyright (c) 2007 - 2011 All Rights Reserved, http://www.match2blue.com/
 * 
 * This source is property of match2blue.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 * 
 * match2blue software development GmbH, Leutragraben 1, 07743 Jena, Germany
 * 
 * 25.07.2012
 * andreasfey */
package com.apiomat.chatomat.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apiomat.chatomat.R;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.MessageModel;

/**
 * @author andreasfey
 */
public class MessageAdapter extends ArrayAdapter<MessageModel>
{
	private final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
	private final SimpleDateFormat stf = new SimpleDateFormat( "HH:mm" );
	private final Map<MessageModel, MemberModel> memberOfMessageMap = new HashMap<MessageModel, MemberModel>( );
	private final String userName;

	/**
	 * @param context
	 * @param messages
	 * @param userName
	 */
	public MessageAdapter( Context context, String userName )
	{
		super( context, android.R.layout.simple_list_item_1 );
		this.userName = userName;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		final LayoutInflater inflater = ( ( Activity ) this.getContext( ) ).getLayoutInflater( );
		final View row = inflater.inflate( R.layout.message_listview, parent, false );
		final MessageModel currentMsg = getItem( position );

		if ( currentMsg != null )
		{
			final MemberModel sender = loadSender( currentMsg );

			final TextView messageDate = ( TextView ) row.findViewById( R.id.messageDate );
			messageDate.setText( getDisplayableDate( currentMsg.getCreatedAt( ) ) );

			final TextView messageSender = ( TextView ) row.findViewById( R.id.messageSender );
			messageSender.setText( sender.getUserName( ) );

			final TextView messageText = ( TextView ) row.findViewById( R.id.messageText );
			messageText.setText( currentMsg.getText( ) + "\n" + this.stf.format( currentMsg.getCreatedAt( ) ) );

			final RelativeLayout message = ( RelativeLayout ) row.findViewById( R.id.message );
			final RelativeLayout.LayoutParams params = ( RelativeLayout.LayoutParams ) message.getLayoutParams( );

			if ( position > 0 && getItem( position - 1 ) != null &&
				getDisplayableDate( getItem( position - 1 ).getCreatedAt( ) ).equals( messageDate.getText( ) ) )
			{
				messageDate.setVisibility( View.INVISIBLE );
				params.setMargins( 0, 2, 0, 0 );
			}
			else
			{
				params.setMargins( 0, 8, 0, 0 );
			}

			if ( sender.getUserName( ).equals( this.userName ) )
			{
				message.setBackgroundResource( R.drawable.baloon_left9p );
				message.setPadding( 30, 10, 0, 0 );
			}
			else
			{
				message.setBackgroundResource( R.drawable.baloon_right9p );
				message.setPadding( 10, 10, 30, 0 );
				params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );

			}
			message.setLayoutParams( params ); // causes layout update
		}
		return row;
	}

	public MemberModel getMemberForMessage( int position )
	{
		final MessageModel currentMsg = getItem( position );
		return this.memberOfMessageMap.get( currentMsg );
	}

	private String getDisplayableDate( Date d )
	{
		if ( this.sdf.format( d ).equals( this.sdf.format( new Date( ) ) ) )
		{
			return "Today";
		}
		return this.sdf.format( d );
	}

	private MemberModel loadSender( MessageModel message )
	{
		if ( !this.memberOfMessageMap.containsKey( message ) )
		{
			try
			{
				LoadMemberTask task = new LoadMemberTask( );
				task.execute( message );
				this.memberOfMessageMap.put( message, task.get( ) );
			}
			catch ( Exception e )
			{
				Log.e( "MessageAdapter", "Error loading member", e );
			}
		}
		return this.memberOfMessageMap.get( message );
	}

	// TODO vermeiden
	private class LoadMemberTask extends AsyncTask<MessageModel, Void, MemberModel>
	{
		@Override
		protected MemberModel doInBackground( MessageModel... message )
		{
			try
			{
				return message[ 0 ].loadSender( );
			}
			catch ( Exception e )
			{
				Log.e( "MessageAdapter", "Error loading member", e );
				return null;
			}
		}
	}
}
