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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.apiomat.chatomat.MemberCache;
import com.apiomat.chatomat.R;
import com.apiomat.frontend.chat.ConversationModel;
import com.apiomat.frontend.chat.MessageModel;

/**
 * View for displaying a Conversation in the main activity.
 * 
 * @author andreasfey
 */
@SuppressLint( "DrawAllocation" )
public class ConversationSubjectView extends View
{
	private ConversationModel conversation;
	private final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
	private static final int HEIGHT = 80;
	private static final int BORDER = 10;

	private MessageModel lastMessage;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ConversationSubjectView( Context context, AttributeSet attrs, int defStyle )
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
	public ConversationSubjectView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		setMinimumHeight( HEIGHT );
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public ConversationSubjectView( Context context )
	{
		super( context );
		setMinimumHeight( HEIGHT );
	}

	/**
	 * Set the conversation object to show
	 * 
	 * @param conversation
	 */
	public final void setConversation( ConversationModel conversation )
	{
		this.conversation = conversation;

		try
		{
			LoadConversationMessagessTask task = new LoadConversationMessagessTask( );
			task.execute( );
			List<MessageModel> mms = task.get( );
			if ( mms != null && mms.size( ) > 0 )
			{
				this.lastMessage = mms.get( mms.size( ) - 1 );
			}
		}
		catch ( Exception e )
		{
			Log.e( "ConversationSubject",
				"Messages could not be loaded from conversation " + this.conversation.getSubject( ), e );
		}
	}

	/**
	 * Helper method to update the last message if something on the conversation changed; this helps avoiding to load
	 * all messages again to determine these value
	 * 
	 * @param lastMessage
	 */
	public final void setLastMessage( MessageModel lastMessage )
	{
		this.lastMessage = lastMessage;
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );

		if ( this.conversation != null )
		{
			/* subject */
			Paint paint = new Paint( );
			paint.setColor( Color.BLACK );
			paint.setAntiAlias( true );
			paint.setTypeface( Typeface.DEFAULT_BOLD );
			paint.setTextSize( 24 );
			canvas.drawText( this.conversation.getSubject( ), HEIGHT + 20f, 20f, paint );

			/* created at */
			paint = new Paint( );
			paint.setColor( Color.BLACK );
			paint.setTextSize( 18 );
			paint.setAntiAlias( true );
			paint.setTypeface( Typeface.DEFAULT );
			String date = this.sdf.format( this.conversation.getCreatedAt( ) );
			paint.setTextAlign( Align.RIGHT );
			canvas.drawText( date, canvas.getWidth( ) - BORDER, 23f, paint );

			/* last message */
			if ( this.lastMessage != null )
			{
				paint = new Paint( );
				paint.setColor( Color.BLACK );
				paint.setAntiAlias( true );
				paint.setTypeface( Typeface.DEFAULT );
				paint.setTextSize( 24 );
				String text = this.lastMessage.getSenderUserName( ) + ":" + this.lastMessage.getText( );
				if ( text.length( ) > 50 )// TODO berechnen, nicht schaetzen
				{
					text = text.substring( 0, 50 );
				}
				canvas.drawText( text, HEIGHT + 20, HEIGHT - BORDER, paint );

				/* sender image */
				Resources res = getResources( );
				BitmapDrawable drawable = ( BitmapDrawable ) res.getDrawable( R.drawable.profilimg_default );
				if ( MemberCache.containsImage( this.lastMessage.getSenderUserName( ) ) )
				{
					Bitmap bm = MemberCache.getImage( this.lastMessage.getSenderUserName( ) );
					drawable = new BitmapDrawable( res, bm );
				}

				drawable.setBounds( BORDER, BORDER, HEIGHT - BORDER, HEIGHT - BORDER );
				drawable.draw( canvas );
			}
		}
	}

	private class LoadConversationMessagessTask extends AsyncTask<Void, Void, List<MessageModel>>
	{
		@Override
		protected List<MessageModel> doInBackground( Void... nix )
		{
			try
			{
				return ConversationSubjectView.this.conversation.loadMessages( "" );
			}
			catch ( Exception e )
			{
				Log.e( "LoadConversationsTask", "Error loading messages", e );
				return new ArrayList<MessageModel>( );
			}
		}
	}

}
