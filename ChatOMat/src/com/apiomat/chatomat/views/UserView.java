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
import com.apiomat.frontend.basics.User;

/**
 * View for displaying member (username + image) in the Member selection activity
 * 
 * @author apiomat
 */

public class UserView extends View
{
	private User user;
	private Bitmap memberImage;
	private String city = "";

	private static final int HEIGHT = 80;
	private static final int BORDER = 5;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public UserView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		setMinimumHeight( HEIGHT );
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param attrs
	 */
	public UserView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		setMinimumHeight( HEIGHT );
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public UserView( Context context )
	{
		super( context );
		setMinimumHeight( HEIGHT );
	}

	/**
	 * Sets the member to display, computes the locality if location data is present
	 * 
	 * @param member
	 */
	public final void setUser( User user )
	{
		this.user = user;

		if ( user.getLocLatitude( ) != 0 || user.getLocLongitude( ) != 0 )
		{
			Geocoder gcd = new Geocoder( getContext( ), Locale.getDefault( ) );
			List<Address> addresses;
			try
			{
				addresses = gcd.getFromLocation( this.user.getLocLatitude( ), this.user.getLocLongitude( ), 1 );
				if ( addresses.size( ) > 0 )
				{
					this.city = addresses.get( 0 ).getLocality( );
				}
			}
			catch ( Exception e )
			{
				Log.e( "MemberView", "Could not determine position", e );
			}
		}
	}

	/**
	 * Sets the image to display left of the user name
	 * 
	 * @param memberImage
	 */
	public final void setMemberImage( Bitmap memberImage )
	{
		this.memberImage = memberImage;
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );

		if ( this.user != null )
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
			canvas.drawText( this.user.getUserName( ), HEIGHT + 20, 20f, paint );

			/* city */
			paint = new Paint( );
			paint.setColor( Color.BLACK );
			paint.setTextSize( 18 );
			paint.setAntiAlias( true );
			paint.setTypeface( Typeface.DEFAULT );
			paint.setTextAlign( Align.RIGHT );
			canvas.drawText( this.city, canvas.getWidth( ) - BORDER, 40f, paint );

			/* profession */
			if ( this.user.getProfession( ) != null )
			{
				paint = new Paint( );
				paint.setColor( Color.BLACK );
				paint.setAntiAlias( true );
				paint.setTypeface( Typeface.DEFAULT );
				paint.setTextSize( 24 );
				canvas.drawText( this.user.getProfession( ), HEIGHT + 20, 40f, paint );
			}
		}
	}
}
