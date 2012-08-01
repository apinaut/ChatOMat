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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.apiomat.chatomat.R;
import com.apiomat.chatomat.views.ConversationSubjectView;
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.ConversationModel;
import com.apiomat.frontend.chat.MessageModel;

/**
 * @author andreasfey
 */
public class ConversationAdapter extends ArrayAdapter<ConversationModel>
{
	ViewGroup lastParent;

	/**
	 * @param context
	 */
	public ConversationAdapter( Context context )
	{
		super( context, android.R.layout.simple_list_item_1 );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		this.lastParent = parent;

		LayoutInflater inflater = ( ( Activity ) this.getContext( ) ).getLayoutInflater( );
		View row = inflater.inflate( R.layout.conversation_listview, parent, false );

		ConversationSubjectView l = ( ConversationSubjectView ) row.findViewById( R.id.csView );
		l.setConversation( getItem( position ) );
		return row;
	}

	public void setLastMessage( MessageModel lastMessage, MemberModel sender )
	{
		LayoutInflater inflater = ( ( Activity ) this.getContext( ) ).getLayoutInflater( );
		View row = inflater.inflate( R.layout.conversation_listview, this.lastParent, false );

		ConversationSubjectView l = ( ConversationSubjectView ) row.findViewById( R.id.csView );
		l.setLastMessage( lastMessage );
		l.setLastSender( sender );
	}

}
