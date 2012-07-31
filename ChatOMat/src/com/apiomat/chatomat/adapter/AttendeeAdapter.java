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
