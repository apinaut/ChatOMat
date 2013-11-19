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
package com.apiomat.chatomat.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apiomat.chatomat.UserCache;
import com.apiomat.chatomat.R;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.basics.User;
import com.apiomat.frontend.chat.ChatMessageModel;


/**
 * Adapter for the balloon messages show in the Conversation subject activity
 * 
 * @author apiomat
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public class ChatMessageAdapter extends ArrayAdapter<ChatMessageModel>
{
	private final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
	private final SimpleDateFormat stf = new SimpleDateFormat( "HH:mm" );
	private final String userName;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param userName user name of the sender of this message
	 */
	public ChatMessageAdapter( Context context, String userName )
	{
		super( context, android.R.layout.simple_list_item_1 );
		this.userName = userName;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		final LayoutInflater inflater = ( ( Activity ) this.getContext( ) ).getLayoutInflater( );
		final View row = inflater.inflate( R.layout.message_listview, parent, false );
		final ChatMessageModel currentMsg = getItem( position );

		if ( currentMsg != null )
		{
			Date createdAt = currentMsg.getHref( ) != null ? currentMsg.getCreatedAt( ) : new Date( );
			final TextView messageDate = ( TextView ) row.findViewById( R.id.messageDate );
			messageDate.setText( getDisplayableDate( createdAt ) );

			final TextView messageSender = ( TextView ) row.findViewById( R.id.messageSender );
			messageSender.setText( currentMsg.getSenderUserName( ) );

			final TextView messageText = ( TextView ) row.findViewById( R.id.messageText );
			messageText.setText( currentMsg.getText( ) + "\n" + this.stf.format( createdAt ) );

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

			if ( currentMsg.getSenderUserName( ).equals( this.userName ) )
			{
				message.setBackgroundResource( R.drawable.baloon_left9p );
				message.setPadding( 40, 10, 0, 0 );
			}
			else
			{
				message.setBackgroundResource( R.drawable.baloon_right9p );
				message.setPadding( 20, 10, 30, 0 );
				params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );

			}
			message.setLayoutParams( params ); // causes layout update
		}
		return row;
	}

	/**
	 * Returns the sender of the last message, used for updating the list of conversations on the main screen
	 * 
	 * @return {@link MemberModel} of the last sender
	 */
	public User getUserFromLastMessage( )
	{
		final ChatMessageModel currentMsg = getItem( getCount( ) - 1 );
		return UserCache.getUser( currentMsg.getSenderUserName( ) );
	}

	private String getDisplayableDate( Date d )
	{
		if ( this.sdf.format( d ).equals( this.sdf.format( new Date( ) ) ) )
		{
			return "Today";
		}
		return this.sdf.format( d );
	}
}
