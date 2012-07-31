/* Copyright (c) 2007 - 2011 All Rights Reserved, http://www.match2blue.com/
 * 
 * This source is property of match2blue.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 * 
 * match2blue software development GmbH, Leutragraben 1, 07743 Jena, Germany
 * 
 * 24.07.2012
 * andreasfey */
package com.apiomat.chatomat.views;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.apiomat.chatomat.R;
import com.apiomat.frontend.basics.MemberModel;

/**
 * @author andreasfey
 */
public class MemberView extends View
{
	private MemberModel member;
	private Bitmap memberImage;

	private static final int HEIGHT = 80;
	private static final int BORDER = 5;

	public MemberView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		setMinimumHeight( HEIGHT );
	}

	public MemberView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		setMinimumHeight( HEIGHT );
	}

	public MemberView( Context context )
	{
		super( context );
		setMinimumHeight( HEIGHT );
	}

	public final MemberModel getMember( )
	{
		return this.member;
	}

	public final void setMember( MemberModel member )
	{
		this.member = member;
	}

	public final void setMemberImage( Bitmap memberImage )
	{
		this.memberImage = memberImage;
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );

		if ( this.member != null )
		{
			/* sender image */
			Resources res = getResources( );
			if ( this.memberImage != null )
			{
				ImageView image = new ImageView( getContext( ) );
				image.setMaxWidth( BORDER );
				image.setMaxHeight( BORDER );
				image.setImageBitmap( this.memberImage );
				image.draw( canvas );
			}
			else
			{
				BitmapDrawable drawable = ( BitmapDrawable ) res.getDrawable( R.drawable.profilimg_default );
				drawable.setBounds( BORDER, BORDER, HEIGHT - BORDER, HEIGHT - BORDER );
				drawable.draw( canvas );
			}

			/* username */
			Paint paint = new Paint( );
			paint.setColor( Color.BLACK );
			paint.setAntiAlias( true );
			paint.setTypeface( Typeface.DEFAULT_BOLD );
			paint.setTextSize( 24 );
			canvas.drawText( this.member.getUserName( ), HEIGHT + 20, 20f, paint );

			/* city */
			paint = new Paint( );
			paint.setColor( Color.BLACK );
			paint.setTextSize( 18 );
			paint.setAntiAlias( true );
			paint.setTypeface( Typeface.DEFAULT );
			paint.setTextAlign( Align.RIGHT );
			Geocoder gcd = new Geocoder( getContext( ), Locale.getDefault( ) );
			List<Address> addresses;
			try
			{
				addresses = gcd.getFromLocation( this.member.getLocLatitude( ), this.member.getLocLongitude( ), 1 );
				if ( addresses.size( ) > 0 )
				{
					canvas.drawText( addresses.get( 0 ).getAddressLine( 0 ), canvas.getWidth( ) - BORDER, 40f, paint );
				}
			}
			catch ( Exception e )
			{
				Log.e( "MemberView", "Could not determine position", e );
			}

			/* profession */
			if ( this.member.getProfession( ) != null )
			{
				paint = new Paint( );
				paint.setColor( Color.BLACK );
				paint.setAntiAlias( true );
				paint.setTypeface( Typeface.DEFAULT );
				paint.setTextSize( 24 );
				canvas.drawText( this.member.getProfession( ), HEIGHT + 20, 40f, paint );
			}
		}
	}
}
