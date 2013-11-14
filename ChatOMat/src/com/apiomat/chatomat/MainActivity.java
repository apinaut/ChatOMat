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

import java.io.Serializable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.apiomat.chatomat.adapter.ConversationAdapter;
import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.User;
import com.apiomat.frontend.callbacks.AOMCallback;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;
import com.apiomat.frontend.chat.ChatMessageModel;
import com.apiomat.frontend.chat.ConversationModel;

/**
 * First screen showing a list of conversations
 * 
 * @author apiomat
 */

public class MainActivity extends Activity {

	public static final String EXTRA_POSITION = "position";
	public static final String EXTRA_CONVERSATION = "conv";
	public static final String EXTRA_USERNAME = "username";
	public static final String EXTRA_MEMBER = "member";
	public static final String EXTRA_LAST_MESSAGE = "lastMessageText";

	static final int EXPECTED_SUBJECT_CODE = 1;
	static final int EXPECTED_PROFILE_CODE = 0;

	private ConversationAdapter adapter;
	private Timer t;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources res = getResources();
		Bitmap bMap = BitmapFactory.decodeResource(res,
				R.drawable.apinauts_header_clean);
		BitmapDrawable actionBarBackground = new BitmapDrawable(res, bMap);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(actionBarBackground);

		actionBar.setTitle(R.string.action_conversations);

		this.adapter = new ConversationAdapter(this);

		final ListView convList = (ListView) findViewById(R.id.conversationsList);
		convList.setAdapter(this.adapter);
		final ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector();
		convList.setOnTouchListener(activitySwipeDetector);
		convList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (activitySwipeDetector.isSwipeDetected()) {
					deleteItem(MainActivity.this.adapter.getItem(position));
				} else {
					Intent intent = new Intent(parent.getContext(),
							SubjectActivity.class);
					intent.putExtra(EXTRA_CONVERSATION, (Serializable) convList
							.getAdapter().getItem(position));
					startActivityForResult(intent, EXPECTED_SUBJECT_CODE);
				}
			}
		});

		/* get member back from store or create a new one */
		SharedPreferences mPrefs = getSharedPreferences(
				MainActivity.EXTRA_MEMBER, MODE_PRIVATE);
		if (!mPrefs.contains("userName") || !mPrefs.contains("password")
				|| mPrefs.getString("password", "") == "") {
			Intent intent = new Intent(this, ProfileActivity.class);
			startActivityForResult(intent, EXPECTED_PROFILE_CODE);
		} else {
			UserCache.loadMemberToCache(mPrefs.getString("userName", ""),
					mPrefs.getString("password", ""));
			UserCache.setMyself(mPrefs.getString("userName", ""));
		}
	}

	/**
	 * onPause --> cancel task
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.t.cancel();
	}

	/**
	 * onResume --> start task
	 */
	@SuppressWarnings("synthetic-access")
	@Override
	protected void onResume() {
		super.onResume();

		this.t = new Timer();
		if (UserCache.getMySelf() != null) {
			/* Start timer to fetch messages periodically */
			this.t.scheduleAtFixedRate(new RefreshConversationsTimer(), 0,
					10000);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == EXPECTED_PROFILE_CODE) {
			if (resultCode == RESULT_OK) {
				User user = new User();

				user.setUserName(UserCache.getMySelf().getUserName());
				user.setPassword(UserCache.getMySelf().getPassword());
				Datastore.configure(user);
				onResume();
			} else if (UserCache.getMyself() == null) {
				User user = new User();
				user.setUserName("");
				user.setPassword("");
				Datastore.configure(user);
				this.adapter.clear();
				AlertDialog dialog = new AlertDialog.Builder(this).create();
				dialog.setMessage("You have to create a profile using the apinaut button before going on!");
				dialog.show();
			}
		} else if (requestCode == EXPECTED_SUBJECT_CODE
				&& resultCode == RESULT_OK) {
			ChatMessageModel msg = (ChatMessageModel) intent.getExtras()
					.getSerializable(EXTRA_LAST_MESSAGE);
			this.adapter.setLastMessage(msg);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_add_attendee:
			addAttendee(null);
			return true;
		case R.id.action_profile:
			openProfile(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Opens the profile activity
	 * 
	 * @param view
	 */
	public void openProfile(View view) {
		Intent intent = new Intent(this, ProfileActivity.class);
		startActivityForResult(intent, EXPECTED_PROFILE_CODE);
	}

	/**
	 * Opens the list of members
	 * 
	 * @param view
	 */
	public void addAttendee(View view) {
		Intent intent = new Intent(this, UserSelectionActivity.class);
		startActivity(intent);
	}

	/**
	 * Delete Conversation
	 * 
	 * @param model
	 */
	private void deleteItem(final ConversationModel model) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("Are you sure to delete this conversation?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				AOMEmptyCallback conversationDeleteAsync = new AOMEmptyCallback() {
					@SuppressWarnings("synthetic-access")
					@Override
					public void isDone(ApiomatRequestException exception) {
						if (exception != null) {
							Log.e("MainActivity", "Error deleting conversation");
						} else {
							MainActivity.this.adapter.remove(model);
						}

					}
				};
				model.deleteAsync(conversationDeleteAsync);

			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface,
					int paramInt) {
				paramDialogInterface.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * refresh all conversations in a timerTask
	 * 
	 */
	private class RefreshConversationsTimer extends TimerTask {

		@Override
		public void run() {

			ConversationModel.getConversationModelsAsync("",
					MainActivity.this.conversationList);

		}
	}

	/**
	 * Get all conversations in a list <Async Task>
	 */
	private AOMCallback<List<ConversationModel>> conversationList = new AOMCallback<List<ConversationModel>>() {

		@Override
		public void isDone(List<ConversationModel> resultObject,
				ApiomatRequestException exception) {
			if (exception == null) {
				for (final ConversationModel mm : resultObject) {
					boolean alreadyExists = false;
					for (int i = 0; i < MainActivity.this.adapter.getCount(); i++) {
						if (MainActivity.this.adapter.getItem(i).getHref()
								.equals(mm.getHref())) {
							alreadyExists = true;
							break;
						}
					}
					if (!alreadyExists) {
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MainActivity.this.adapter.add(mm);
							}
						});
					}
				}

			} else {
				Log.e("RefreshConversationsTimer",
						"Error refreshing conversations");
			}
		}
	};

	/**
	 * detect a swipe on a item
	 * 
	 */
	class ActivitySwipeDetector implements View.OnTouchListener {
		static final String logTag = "ActivitySwipeDetector";
		static final int MIN_DISTANCE = 100;
		private float downX, upX;
		private boolean swipeDetected = false;

		public final boolean isSwipeDetected() {
			return this.swipeDetected;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				this.downX = event.getX();
				return true;
			}
			case MotionEvent.ACTION_UP: {
				this.upX = event.getX();
				float deltaX = this.downX - this.upX;

				this.swipeDetected = Math.abs(deltaX) > MIN_DISTANCE
						&& (deltaX < 0 || deltaX > 0);
				return false;
			}
			default:
				return false;
			}
		}
	}
}
