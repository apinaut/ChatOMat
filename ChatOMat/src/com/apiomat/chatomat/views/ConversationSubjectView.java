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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import com.apiomat.frontend.basics.MemberModel;
import com.apiomat.frontend.chat.ConversationModel;
import com.apiomat.frontend.chat.MessageModel;

/**
 * @author andreasfey
 */
public class ConversationSubjectView extends View
{
	private ConversationModel conversation;
	private final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
	private static final int HEIGHT = 80;
	private static final int BORDER = 10;

	private MemberModel lastSender;
	private MessageModel lastMessage;

	public ConversationSubjectView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		setMinimumHeight( HEIGHT );
	}

	public ConversationSubjectView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		setMinimumHeight( HEIGHT );
	}

	public ConversationSubjectView( Context context )
	{
		super( context );
		setMinimumHeight( HEIGHT );
	}

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
				LoadSenderTask task2 = new LoadSenderTask( );
				task2.execute( this.lastMessage );
				this.lastSender = task2.get( );
			}
		}
		catch ( Exception e )
		{
			Log.e( "ConversationSubject",
				"Messages could not be loaded from conversation " + this.conversation.getSubject( ), e );
		}
	}

	public final void setLastSender( MemberModel lastSender )
	{
		this.lastSender = lastSender;
	}

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
				String text =
					( this.lastSender != null ? this.lastSender.getUserName( ) + ": " : "" ) +
						this.lastMessage.getText( );
				if ( text.length( ) > 50 )// TODO berechnen, nicht schaetzen
				{
					text = text.substring( 0, 50 );
				}
				canvas.drawText( text, HEIGHT + 20, HEIGHT - BORDER, paint );
			}

			/* sender image */
			Resources res = getResources( );
			BitmapDrawable drawable = ( BitmapDrawable ) res.getDrawable( R.drawable.profilimg_default );
			if ( this.lastSender != null && MemberCache.containsImage( this.lastSender.getUserName( ) ) )
			{
				Bitmap bm = MemberCache.getImage( this.lastSender.getUserName( ) );
				drawable = new BitmapDrawable( res, bm );
			}

			drawable.setBounds( BORDER, BORDER, HEIGHT - BORDER, HEIGHT - BORDER );
			drawable.draw( canvas );
		}
	}

	private class LoadSenderTask extends AsyncTask<MessageModel, Void, MemberModel>
	{
		@Override
		protected MemberModel doInBackground( MessageModel... msg )
		{
			try
			{
				return msg[ 0 ].loadSender( );
			}
			catch ( Exception e )
			{
				Log.e( "LoadConversationsTask", "Error loading member", e );
				return null;
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
