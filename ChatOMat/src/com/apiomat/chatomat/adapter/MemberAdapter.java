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
 * Adapter for the list of possible members which may be attached to a conversation. The list is displayed each time the
 * "plus" icon is pressed.
 * 
 * @author andreasfey
 */
public class MemberAdapter extends ArrayAdapter<MemberModel>
{
	/**
	 * Constructor
	 * 
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
