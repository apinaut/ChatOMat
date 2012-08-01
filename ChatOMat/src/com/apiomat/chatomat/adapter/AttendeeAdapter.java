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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apiomat.chatomat.MemberCache;
import com.apiomat.chatomat.R;

/**
 * Adapter for the grid of attendee user names and images shown in the conversation subject activity
 * 
 * @author andreasfey
 */
public class AttendeeAdapter extends ArrayAdapter<String>
{
	/**
	 * @param context
	 * @param attendeeNames
	 */
	public AttendeeAdapter( Context context, List<String> attendeeNames )
	{
		super( context, android.R.layout.simple_list_item_1 );
		for ( String att : attendeeNames )
		{
			add( att );
		}
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		LayoutInflater inflater = ( ( Activity ) this.getContext( ) ).getLayoutInflater( );
		View row = inflater.inflate( R.layout.attendee_listview, parent, false );

		ImageView image = ( ImageView ) row.findViewById( R.id.attendeeImage );
		TextView text = ( TextView ) row.findViewById( R.id.attendeeName );
		text.setText( getItem( position ) );

		Bitmap bm = MemberCache.getImage( getItem( position ) );
		if ( bm != null )
		{
			image.setImageBitmap( bm );
		}
		else
		{
			image.setImageResource( R.drawable.profilimg_default );
		}
		return row;
	}
}
