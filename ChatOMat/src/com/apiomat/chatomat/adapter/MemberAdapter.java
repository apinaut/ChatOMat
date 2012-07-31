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

import com.apiomat.chatomat.MemberCache;
import com.apiomat.chatomat.R;
import com.apiomat.chatomat.views.MemberView;
import com.apiomat.frontend.basics.MemberModel;

/**
 * @author andreasfey
 */
public class MemberAdapter extends ArrayAdapter<MemberModel>
{
	/**
	 * @param context
	 */
	public MemberAdapter( Context context )
	{
		super( context, android.R.layout.simple_list_item_1 );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		LayoutInflater inflater = ( ( Activity ) this.getContext( ) ).getLayoutInflater( );
		View row = inflater.inflate( R.layout.member_listview, parent, false );

		MemberView memberView = ( MemberView ) row.findViewById( R.id.memberView );
		memberView.setMember( getItem( position ) );
		memberView.setMemberImage( MemberCache.getImage( getItem( position ).getUserName( ) ) );

		return row;
	}
}
