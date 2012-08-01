package com.apiomat.chatomat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.MemberModel;

public class ProfileActivity extends Activity
{
	private static final int ACTIVITY_SELECT_IMAGE = 2;
	private MemberModel member;
	private String newImagePath;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_profile );

		this.member = MemberCache.getMySelf( );
	}

	@Override
	protected void onResume( )
	{
		super.onResume( );

		if ( this.member != null )
		{
			( ( EditText ) findViewById( R.id.profileUserName ) ).setText( this.member.getUserName( ) );
			( ( EditText ) findViewById( R.id.profileFirstName ) ).setText( this.member.getFirstName( ) );
			( ( EditText ) findViewById( R.id.profileLastName ) ).setText( this.member.getLastName( ) );
			( ( EditText ) findViewById( R.id.profileProfession ) ).setText( this.member.getProfession( ) );
			( ( EditText ) findViewById( R.id.profileCompany ) ).setText( this.member.getCompany( ) );
			if ( this.member.getAge( ) != null )
			{
				( ( EditText ) findViewById( R.id.profileAge ) ).setText( this.member.getAge( ).toString( ) );
			}
			( ( Spinner ) findViewById( R.id.profileSex ) ).setSelection( "female".equals( this.member.getSex( ) ) ? 0
				: 1 );

			LoadMemberImageTask task = new LoadMemberImageTask( );
			task.execute( );
			try
			{
				Bitmap bm = task.get( );
				if ( bm != null )
				{
					( ( ImageView ) findViewById( R.id.profileImage ) )
						.setImageBitmap( bm );
				}
			}
			catch ( Exception e )
			{
				Log.e( "MemberAdapter", "Error loading member image" );
			}
		}
		( ( EditText ) findViewById( R.id.profileUserName ) ).setEnabled( true );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater( ).inflate( R.menu.activity_profile, menu );
		return true;
	}

	/**
	 * Handles the image file selection
	 */
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent imageReturnedIntent )
	{
		super.onActivityResult( requestCode, resultCode, imageReturnedIntent );

		if ( resultCode == RESULT_OK && requestCode == ACTIVITY_SELECT_IMAGE )
		{
			Uri selectedImage = imageReturnedIntent.getData( );
			String[ ] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = getContentResolver( ).query( selectedImage, filePathColumn, null, null, null );
			cursor.moveToFirst( );

			int columnIndex = cursor.getColumnIndex( filePathColumn[ 0 ] );
			String filePath = cursor.getString( columnIndex );
			cursor.close( );
			this.newImagePath = filePath;
			( ( ImageView ) findViewById( R.id.profileImage ) )
				.setImageBitmap( BitmapFactory.decodeFile( this.newImagePath ) );
		}
	}

	public void clearProfile( final View view )
	{
		this.member = null;
		MemberCache.setMyself( null );
		( ( EditText ) findViewById( R.id.profileUserName ) ).setText( "" );
		( ( EditText ) findViewById( R.id.profileFirstName ) ).setText( "" );
		( ( EditText ) findViewById( R.id.profilePassword ) ).setText( "" );
		( ( EditText ) findViewById( R.id.profileLastName ) ).setText( "" );
		( ( EditText ) findViewById( R.id.profileProfession ) ).setText( "" );
		( ( EditText ) findViewById( R.id.profileCompany ) ).setText( "" );
		( ( EditText ) findViewById( R.id.profileAge ) ).setText( "" );
		( ( Spinner ) findViewById( R.id.profileSex ) ).setSelection( 0 );
		( ( EditText ) findViewById( R.id.profileUserName ) ).setEnabled( true );
	}

	public void saveProfile( final View view )
	{
		AlertDialog alert = new AlertDialog.Builder( ProfileActivity.this ).create( );
		alert.setCancelable( true );
		alert.setTitle( "Field values error" );
		String errorMessage = "";

		if ( ( ( EditText ) findViewById( R.id.profileFirstName ) ).getText( ).length( ) == 0 )
		{
			errorMessage = "Please provide a value for field 'First name'!";
		}
		else if ( ( ( EditText ) findViewById( R.id.profileLastName ) ).getText( ).length( ) == 0 )
		{
			errorMessage = "Please provide a value for field 'Last name'!";
		}
		else if ( ( ( EditText ) findViewById( R.id.profileAge ) ).getText( ).length( ) == 0 )
		{
			errorMessage = "Please provide a value for field 'Age'!";
		}
		else if ( ( ( EditText ) findViewById( R.id.profileProfession ) ).getText( ).length( ) == 0 )
		{
			errorMessage = "Please provide a value for field 'Profession'!";
		}
		else if ( ( ( EditText ) findViewById( R.id.profileCompany ) ).getText( ).length( ) == 0 )
		{
			errorMessage = "Please provide a value for field 'Company'!";
		}
		else if ( ( ( EditText ) findViewById( R.id.profilePassword ) ).getText( ).length( ) == 0 &&
			this.member == null )
		{
			errorMessage = "Please provide a value for field 'Password'!";
		}

		if ( !errorMessage.isEmpty( ) )
		{
			alert.setMessage( errorMessage );
			alert.show( );
		}
		else
		{
			if ( this.member == null )
			{
				this.member = new MemberModel( );
			}

			this.member.setUserName( ( ( EditText ) findViewById( R.id.profileUserName ) ).getText( ).toString( ) );
			this.member.setFirstName( ( ( EditText ) findViewById( R.id.profileFirstName ) ).getText( ).toString( ) );
			this.member.setLastName( ( ( EditText ) findViewById( R.id.profileLastName ) ).getText( ).toString( ) );
			this.member.setCompany( ( ( EditText ) findViewById( R.id.profileCompany ) ).getText( ).toString( ) );
			this.member.setAge( Integer.parseInt( ( ( EditText ) findViewById( R.id.profileAge ) ).getText( )
				.toString( ) ) );
			this.member.setProfession( ( ( EditText ) findViewById( R.id.profileProfession ) ).getText( ).toString( ) );
			this.member.setSex( ( ( Spinner ) findViewById( R.id.profileSex ) ).getSelectedItem( ).toString( ) );

			if ( !( ( EditText ) findViewById( R.id.profilePassword ) ).getText( ).toString( ).equals( "" ) )
			{
				this.member.setPassword( ( ( EditText ) findViewById( R.id.profilePassword ) ).getText( ).toString( ) );
			}

			LocationManager lm = ( LocationManager ) getSystemService( Context.LOCATION_SERVICE );
			Criteria c = new Criteria( );
			c.setPowerRequirement( Criteria.POWER_LOW );
			Location location = lm.getLastKnownLocation( lm.getBestProvider( c, true ) );
			this.member.setLocLongitude( location.getLongitude( ) );
			this.member.setLocLatitude( location.getLatitude( ) );

			CreateOrLoadMemberTask task = new CreateOrLoadMemberTask( );
			SaveMemberImageTask task2 = new SaveMemberImageTask( );

			task.execute( );
			try
			{
				String pw = this.member.getPassword( );
				boolean ok = task.get( );
				if ( !ok )
				{
					alert.setMessage( "Please try another username or use the correct password." );
					alert.show( );
					return;
				}
				this.member.setPassword( pw ); // is not returned from server

				if ( this.newImagePath != null )
				{
					pw = this.member.getPassword( );
					task2.execute( this.newImagePath );
					task2.get( );
					this.member.setPassword( pw ); // is not returned from server
				}
			}
			catch ( Exception e )
			{
				Log.e( "ProfileActivity", "Error creating member", e );
			}

			MemberCache.setMyself( this.member.getUserName( ) );
			MemberCache.putMember( this.member );
			goBack( view );
		}
	}

	public void goBack( View view )
	{
		if ( this.member == null )
		{
			AlertDialog alert = new AlertDialog.Builder( ProfileActivity.this ).create( );
			alert.setCancelable( true );
			alert.setTitle( "Member needed" );
			alert
				.setMessage( "You need to create a profile before going on. Please fill out all fields and hit 'save'." );
			alert.show( );
		}
		else if ( this.member.getPassword( ) != "" )
		{
			Intent intent = new Intent( );
			MemberCache.putMember( this.member );
			setResult( RESULT_OK, intent );
			finish( );
		}
		else
		{
			Intent intent = new Intent( );
			setResult( RESULT_CANCELED, intent );
			finish( );
		}
	}

	public void changeProfileImage( @SuppressWarnings( "unused" ) View view )
	{
		Intent i = new Intent( Intent.ACTION_PICK,
			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
		startActivityForResult( i, ACTIVITY_SELECT_IMAGE );
	}

	private class CreateOrLoadMemberTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground( Void... m )
		{
			try
			{
				if ( ProfileActivity.this.member.getUserName( ) == "" )
				{
					ProfileActivity.this.member.setUserName( ProfileActivity.this.member.getFirstName( ) +
						ProfileActivity.this.member.getLastName( ) );
				}

				Datastore.configure( MemberModel.baseURL, MemberModel.apiKey,
					ProfileActivity.this.member.getUserName( ),
					ProfileActivity.this.member.getPassword( ) );

				if ( ProfileActivity.this.member.getHref( ) == null )
				{
					/* try to load an existing user; then update it with the new values */
					MemberModel tmpMember = new MemberModel( );
					tmpMember.setUserName( ProfileActivity.this.member.getUserName( ) );
					tmpMember.setPassword( ProfileActivity.this.member.getPassword( ) );
					tmpMember.loadMe( );

					tmpMember.setAge( ProfileActivity.this.member.getAge( ) );
					tmpMember.setCompany( ProfileActivity.this.member.getCompany( ) );
					tmpMember.setFirstName( ProfileActivity.this.member.getFirstName( ) );
					tmpMember.setLastName( ProfileActivity.this.member.getLastName( ) );
					tmpMember.setLocLatitude( ProfileActivity.this.member.getLocLatitude( ) );
					tmpMember.setLocLongitude( ProfileActivity.this.member.getLocLongitude( ) );
					tmpMember.setProfession( ProfileActivity.this.member.getProfession( ) );
					tmpMember.setSex( ProfileActivity.this.member.getSex( ) );
					ProfileActivity.this.member = tmpMember;
				}
				ProfileActivity.this.member.save( );

				return ProfileActivity.this.member.getHref( ) != null;
			}
			catch ( Exception e )
			{
				Log.w( "ProfileActivity", "Error loading member", e );
			}
			return false;
		}
	}

	private class SaveMemberImageTask extends AsyncTask<String, Void, Void>
	{
		@Override
		protected Void doInBackground( String... m )
		{
			try
			{
				String filePath = m[ 0 ];
				byte[ ] imageBytes = readFile( new File( filePath ) );

				ProfileActivity.this.member.postImage( imageBytes );
			}
			catch ( Exception e )
			{
				Log.e( "ProfileActivity", "Error uploading profile image", e );
			}
			return null;
		}

		private byte[ ] readFile( File file ) throws IOException
		{
			// Open file
			RandomAccessFile f = new RandomAccessFile( file, "r" );

			try
			{
				// Get and check length
				long longlength = f.length( );
				int length = ( int ) longlength;
				if ( length != longlength )
				{
					throw new IOException( "File size >= 10 MB" );
				}

				// Read file and return data
				byte[ ] data = new byte[ length ];
				f.readFully( data );
				return data;
			}
			finally
			{
				f.close( );
			}
		}
	}

	private class LoadMemberImageTask extends AsyncTask<Void, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground( Void... nix )
		{
			try
			{
				URL newurl = new URL( ProfileActivity.this.member.getImageURL( ) );
				return BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );
			}
			catch ( Exception e )
			{
				Log.e( "MemberAdapter", "Error loading member image" );
				return null;
			}
		}
	}

	@Override
	protected void onPause( )
	{
		super.onPause( );

		SharedPreferences mPrefs = getSharedPreferences( MainActivity.EXTRA_MEMBER, MODE_PRIVATE );
		SharedPreferences.Editor ed = mPrefs.edit( );

		if ( this.member != null )
		{
			ed.putString( "userName", this.member.getUserName( ) );
			if ( this.member.getPassword( ) != "" )
			{
				ed.putString( "password", this.member.getPassword( ) );
			}
		}
		else
		{
			ed.remove( "userName" );
			ed.remove( "password" );
		}
		ed.commit( );
	}
}
