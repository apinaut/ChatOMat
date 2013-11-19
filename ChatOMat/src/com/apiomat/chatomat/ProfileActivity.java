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
package com.apiomat.chatomat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.basics.User;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;

/**
 * Activity where the user can modify his profile. This activity is
 * automatically started at the beginning if no member was logged in/saved yet.
 * 
 * The username and password of this member is then saved in the
 * {@link #onPause()} method and queried each app start later on.
 * 
 * @author apiomat
 */

public class ProfileActivity extends Activity {
	private static final int ACTIVITY_SELECT_IMAGE = 2;
	private User user;
	private String newImagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		Resources res = getResources();
		Bitmap bMap = BitmapFactory.decodeResource(res,
				R.drawable.apinauts_header_clean);
		BitmapDrawable actionBarBackground = new BitmapDrawable(res, bMap);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(actionBarBackground);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(R.string.action_profile);

		this.user = UserCache.getMySelf();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (this.user != null) {
			((EditText) findViewById(R.id.profileUserName)).setText(this.user
					.getUserName());
			((EditText) findViewById(R.id.profileFirstName)).setText(this.user
					.getFirstName());
			((EditText) findViewById(R.id.profileLastName)).setText(this.user
					.getLastName());
			((EditText) findViewById(R.id.profileProfession)).setText(this.user
					.getProfession());
			((EditText) findViewById(R.id.profileCompany)).setText(this.user
					.getCompany());
			if (this.user.getAge() != null) {
				((EditText) findViewById(R.id.profileAge)).setText(this.user
						.getAge().toString());
			}
			((Spinner) findViewById(R.id.profileSex)).setSelection("female"
					.equals(this.user.getSex()) ? 0 : 1);

			@SuppressWarnings("synthetic-access")
			LoadMemberImageTask task = new LoadMemberImageTask();
			task.execute();
			try {
				Bitmap bm = task.get();
				if (bm != null) {
					((ImageView) findViewById(R.id.profileImage))
							.setImageBitmap(bm);
				}
			} catch (Exception e) {
				Log.e("MemberAdapter", "Error loading member image");
			}
		}
		((EditText) findViewById(R.id.profileUserName)).setEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			goBack(null);

			return true;
		case R.id.menu_save:
			saveProfile(null);
			return true;
		case R.id.menu_clear:
			clearProfile(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handles the image file selection
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		if (resultCode == RESULT_OK && requestCode == ACTIVITY_SELECT_IMAGE) {
			Uri selectedImage = imageReturnedIntent.getData();
			String[] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			this.newImagePath = filePath;
			((ImageView) findViewById(R.id.profileImage))
					.setImageBitmap(BitmapFactory.decodeFile(this.newImagePath));
		}
	}

	/**
	 * Clears all fields and sets the current member to null
	 * 
	 * @param view
	 */
	public void clearProfile(final View view) {
		this.user = null;
		UserCache.setMyself(null);
		((EditText) findViewById(R.id.profileUserName)).setText("");
		((EditText) findViewById(R.id.profileFirstName)).setText("");
		((EditText) findViewById(R.id.profilePassword)).setText("");
		((EditText) findViewById(R.id.profileLastName)).setText("");
		((EditText) findViewById(R.id.profileProfession)).setText("");
		((EditText) findViewById(R.id.profileCompany)).setText("");
		((EditText) findViewById(R.id.profileAge)).setText("");
		((Spinner) findViewById(R.id.profileSex)).setSelection(0);
		((EditText) findViewById(R.id.profileUserName)).setEnabled(true);
	}

	/**
	 * Saves the profile; if member exists already, the values are only updated
	 * 
	 * @param view
	 */
	@SuppressWarnings({ "boxing" })
	public void saveProfile(final View view) {
		AlertDialog alert = new AlertDialog.Builder(ProfileActivity.this)
				.create();
		alert.setCancelable(true);
		alert.setTitle("Field values error");
		String errorMessage = "";

		if (((EditText) findViewById(R.id.profileFirstName)).getText().length() == 0) {
			errorMessage = "Please provide a value for field 'First name'!";
		} else if (((EditText) findViewById(R.id.profileLastName)).getText()
				.length() == 0) {
			errorMessage = "Please provide a value for field 'Last name'!";
		} else if (((EditText) findViewById(R.id.profileAge)).getText()
				.length() == 0) {
			errorMessage = "Please provide a value for field 'Age'!";
		} else if (((EditText) findViewById(R.id.profileProfession)).getText()
				.length() == 0) {
			errorMessage = "Please provide a value for field 'Profession'!";
		} else if (((EditText) findViewById(R.id.profileCompany)).getText()
				.length() == 0) {
			errorMessage = "Please provide a value for field 'Company'!";
		} else if (((EditText) findViewById(R.id.profilePassword)).getText()
				.length() == 0 && this.user == null) {
			errorMessage = "Please provide a value for field 'Password'!";
		}

		if (!errorMessage.equals("")) {
			alert.setMessage(errorMessage);
			alert.show();
		} else {
			if (this.user == null) {
				this.user = new User();
			}
			final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
			progressDialog.setTitle("saving User ...");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();

			this.user
					.setUserName(((EditText) findViewById(R.id.profileUserName))
							.getText().toString());
			this.user
					.setFirstName(((EditText) findViewById(R.id.profileFirstName))
							.getText().toString());
			this.user
					.setLastName(((EditText) findViewById(R.id.profileLastName))
							.getText().toString());
			this.user.setCompany(((EditText) findViewById(R.id.profileCompany))
					.getText().toString());
			this.user.setAge(Integer
					.parseInt(((EditText) findViewById(R.id.profileAge))
							.getText().toString()));
			this.user
					.setProfession(((EditText) findViewById(R.id.profileProfession))
							.getText().toString());
			this.user.setSex(((Spinner) findViewById(R.id.profileSex))
					.getSelectedItem().toString());

			if (!((EditText) findViewById(R.id.profilePassword)).getText()
					.toString().equals("")) {
				this.user
						.setPassword(((EditText) findViewById(R.id.profilePassword))
								.getText().toString());
			}

			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria c = new Criteria();
			c.setPowerRequirement(Criteria.POWER_LOW);
			Location location = lm.getLastKnownLocation(lm.getBestProvider(c,
					true));
			this.user.setLocLongitude(location.getLongitude());
			this.user.setLocLatitude(location.getLatitude());

			this.user.loadMeAsync(new AOMEmptyCallback() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void isDone(ApiomatRequestException exception) {
					if (exception != null) {
						ProfileActivity.this.user
								.saveAsync(new AOMEmptyCallback() {

									@Override
									public void isDone(
											ApiomatRequestException exception) {

										String pw = ProfileActivity.this.user
												.getPassword();
										ProfileActivity.this.user
												.setPassword(pw);

										if (ProfileActivity.this.newImagePath != null) {
											SaveMemberImageTask task2 = new SaveMemberImageTask();
											pw = ProfileActivity.this.user
													.getPassword();
											task2.execute(ProfileActivity.this.newImagePath);
											try {
												task2.get();
											} catch (InterruptedException e) {
												e.printStackTrace();
											} catch (ExecutionException e) {

												e.printStackTrace();
											}
											ProfileActivity.this.user
													.setPassword(pw);
										}

										UserCache
												.setMyself(ProfileActivity.this.user
														.getUserName());
										UserCache
												.putUser(ProfileActivity.this.user);
										progressDialog.dismiss();
										goBack(view);

									}
								});
					} else {
						progressDialog.dismiss();
						Toast.makeText(ProfileActivity.this, "User already exists and is loaded", Toast.LENGTH_LONG).show();
					}

				}
			});
		}
	}

	/**
	 * Goes back to the {@link MainActivity}
	 * 
	 * @param view
	 */

	public void goBack(View view) {
		if (this.user == null) {
			AlertDialog alert = new AlertDialog.Builder(ProfileActivity.this)
					.create();
			alert.setCancelable(true);
			alert.setTitle("Member needed");
			alert.setMessage("You need to create a profile before going on. Please fill out all fields and hit 'save'.");
			alert.show();
		} else if (this.user.getPassword() != "") {
			Intent intent = new Intent();
			UserCache.putUser(this.user);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
		}
	}

	/**
	 * Opens the gallery to let the user select a profile image. The result is
	 * handled in {@link #onActivityResult(int, int, Intent)}
	 * 
	 * @param view
	 */
	public void changeProfileImage(final View view) {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
	}

	/**
	 * save image from member in a async task
	 * 
	 * @author Tim
	 */
	private class SaveMemberImageTask extends AsyncTask<String, Void, Void> {
		@SuppressWarnings("synthetic-access")
		@Override
		protected Void doInBackground(String... m) {
			try {
				String filePath = m[0];
				byte[] imageBytes = readFile(new File(filePath));

				ProfileActivity.this.user.postImage(imageBytes);

				Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0,
						imageBytes.length);
				UserCache
						.putImage(ProfileActivity.this.user.getUserName(), bmp);
			} catch (Exception e) {
				Log.e("ProfileActivity", "Error uploading profile image", e);
			}
			return null;
		}

		private byte[] readFile(File file) throws IOException {
			// Open file
			RandomAccessFile f = new RandomAccessFile(file, "r");

			try {
				// Get and check length
				long longlength = f.length();
				int length = (int) longlength;
				if (length != longlength) {
					throw new IOException("File size >= 10 MB");
				}

				// Read file and return data
				byte[] data = new byte[length];
				f.readFully(data);
				return data;
			} finally {
				f.close();
			}
		}
	}

	private class LoadMemberImageTask extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... nix) {
			try {
				@SuppressWarnings("synthetic-access")
				URL newurl = new URL(ProfileActivity.this.user.getImageURL());
				return BitmapFactory.decodeStream(newurl.openConnection()
						.getInputStream());
			} catch (Exception e) {
				Log.e("MemberAdapter", "Error loading member image");
				return null;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences mPrefs = getSharedPreferences(
				MainActivity.EXTRA_MEMBER, MODE_PRIVATE);
		SharedPreferences.Editor ed = mPrefs.edit();

		if (this.user != null) {
			ed.putString("userName", this.user.getUserName());
			if (this.user.getPassword() != "") {
				ed.putString("password", this.user.getPassword());
			}
		} else {
			ed.remove("userName");
			ed.remove("password");
		}
		ed.commit();
	}
}
