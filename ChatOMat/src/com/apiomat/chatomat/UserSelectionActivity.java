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
 * OF THE POSSIBILITY OF SUCH DAMAGE. */package com.apiomat.chatomat;

import java.util.Arrays;
import java.util.List;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.EditText;

import android.widget.ListView;

import com.apiomat.chatomat.adapter.UserAdapter;
import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.basics.User;
import com.apiomat.frontend.callbacks.AOMCallback;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;
import com.apiomat.frontend.chat.ChatMessageModel;
import com.apiomat.frontend.chat.ConversationModel;

/**
 * Acitivity which shows a list of members; the user can select one to add him
 * to a new or existing conversation. <br/>
 * This activity may be called by pressing the plus button either from the
 * {@link MainActivity}, which means member for a <b>new</b> conversation is
 * selected, or from the {@link SubjectActivity}, which means a member will be
 * added to an existing conversation.
 * 
 * @author apiomat
 */


public class UserSelectionActivity extends Activity {
	private ConversationModel conv;
	private UserAdapter adapter;
	boolean startNewConversation = true;
	ListView list;
	int position;
	ProgressDialog progressDialog;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		final Intent i = getIntent();
		if (i.getExtras() != null
				&& i.getExtras().containsKey(MainActivity.EXTRA_CONVERSATION)) {
			UserSelectionActivity.this.startNewConversation = false;
			UserSelectionActivity.this.conv = (ConversationModel) i
					.getExtras().getSerializable(
							MainActivity.EXTRA_CONVERSATION);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member_selection);
		Resources res = getResources();
		Bitmap bMap = BitmapFactory.decodeResource(res,
				R.drawable.apinauts_header_clean);
		BitmapDrawable actionBarBackground = new BitmapDrawable(res, bMap);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(actionBarBackground);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(R.string.action_add_attendee);

		this.list = (ListView) findViewById(R.id.listViewAttendees);
		this.adapter = new UserAdapter(this);
		this.list.setAdapter(this.adapter);
		this.list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (UserSelectionActivity.this.startNewConversation) {
					addConversation(view,
							(User) UserSelectionActivity.this.list
									.getItemAtPosition(position));
				} else {
					UserSelectionActivity.this.position = position;
					final User user = (User) UserSelectionActivity.this.list
							.getItemAtPosition(position);
					progressDialog = new ProgressDialog(getApplicationContext());
					progressDialog.setTitle("Processing...");
					progressDialog.setMessage("Please wait.");
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(true);
					progressDialog.show();
					user.loadMeAsync(new AOMEmptyCallback() {

						@Override
						public void isDone(ApiomatRequestException exception) {
							progressDialog.dismiss();
							if (exception == null) {
								addAttendeeInConversation(user,
										UserSelectionActivity.this.conv);
							} else {
								Log.e(UserSelectionActivity.class.getName(),
										"Couldn't load MemberModel");
							}

						}
					});

				}
			}

		});

		StringBuffer filter = new StringBuffer();
		if (UserSelectionActivity.this.conv != null) {
			for (String attendee : (List<String>) UserSelectionActivity.this.conv
					.getAttendeeUserNames()) {

				if (filter.length() > 0) {
					filter.append(" AND ");
				}
				filter.append("userName != \"" + attendee + "\"");
			}
		} else {
			filter.append("userName != \"" + UserCache.getMyself() + "\"");
		}

		progressDialog = new ProgressDialog(getApplicationContext());
		progressDialog.setTitle("Processing...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		// Get Users with filter
		User.getUsersAsync(filter.toString(),
				new AOMCallback<List<User>>() {

					
					@Override
					public void isDone(List<User> resultObject,
							ApiomatRequestException exception) {
						for (User u : resultObject) {
							progressDialog.dismiss();
							UserSelectionActivity.this.adapter.add(u);
						}

					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_attendee_selection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			goBack(null);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** pass back results to main screen */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			User sender = (User) intent.getExtras()
					.getSerializable(MainActivity.EXTRA_MEMBER);
			ChatMessageModel msg = (ChatMessageModel) intent.getExtras()
					.getSerializable(MainActivity.EXTRA_LAST_MESSAGE);

			setResult(RESULT_OK, intent);
			intent.putExtra(MainActivity.EXTRA_LAST_MESSAGE, msg);
			intent.putExtra(MainActivity.EXTRA_MEMBER, sender);

		}
		finish();
	}

	/**
	 * Navigates back to the main activity
	 * 
	 * @param view
	 */
	public void goBack(View view) {
		finish();
	}

	/**
	 * add attendee to conversation
	 * @param m
	 */
	private void addAttendee(final User u) {

		@SuppressWarnings("unchecked")
		List<String> userNames = UserSelectionActivity.this.conv
				.getAttendeeUserNames();
		userNames.add(u.getUserName());
		UserSelectionActivity.this.conv.setAttendeeUserNames(userNames);
		UserSelectionActivity.this.conv.saveAsync(new AOMEmptyCallback() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void isDone(ApiomatRequestException exception) {
				if (exception != null) {
					Log.e("addAttendee", "add attendee failed");
				}

			}
		});

	}
	
	/**
	 * add attendee to existing conversation
	 * @param m
	 * @param conversationModel
	 */
	private void addAttendeeInConversation(final User u,
			final ConversationModel conversationModel) {
		if (conversationModel != null) {
			final AOMEmptyCallback conversationSaveAsync = new AOMEmptyCallback() {

				@Override
				public void isDone(ApiomatRequestException exception) {

					if (exception != null) {
						Log.e("addAttendee", "add attendee failed");
					} else {
						Intent intent = new Intent();
						User u = (User) UserSelectionActivity.this.list
								.getItemAtPosition(UserSelectionActivity.this.position);
						intent.putExtra(MainActivity.EXTRA_USERNAME,
								u.getUserName());
						setResult(RESULT_OK, intent);
						finish();
					}

				}
			};
			AOMEmptyCallback conversationLoadAsync = new AOMEmptyCallback() {

				@Override
				public void isDone(ApiomatRequestException exception) {

					@SuppressWarnings("unchecked")
					List<String> userNames = conversationModel
							.getAttendeeUserNames();

					userNames.add(u.getUserName());
					conversationModel.setAttendeeUserNames(userNames);
					
					conversationModel.saveAsync(conversationSaveAsync);

				}
			};
			conversationModel.loadAsync(conversationLoadAsync);
		} else {
			Log.e("MemberSelectionActivity", "Conv is null");
		}

	}

	/**
	 * add a conversation
	 * @param view
	 * @param m
	 */
	private void addConversation(View view,
			final User u) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		View popupView = getLayoutInflater().inflate(R.layout.subject_popup,
				null);
		final EditText subject = (EditText) popupView
				.findViewById(R.id.subjectText);
		dialog.setTitle(R.string.subjectpopup_text);

		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@SuppressWarnings("synthetic-access")
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (subject.getText().length() == 0) {
							// dialog.dismiss();
							AlertDialog.Builder alert = new AlertDialog.Builder(
									UserSelectionActivity.this);
							alert.setCancelable(true);
							alert.setTitle("Subject must not be empty");
							alert.setMessage("We need a subject to create a new conversation.");
							alert.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();

										}
									});
							alert.create();
							alert.show();
							return;
						}
						UserSelectionActivity.this.conv = new ConversationModel();
						UserSelectionActivity.this.conv
								.setAttendeeUserNames(Arrays
										.asList(new String[] { UserCache
												.getMyself() }));
						UserSelectionActivity.this.conv.setSubject(subject
								.getText().toString());
						UserSelectionActivity.this.conv
								.saveAsync(new AOMEmptyCallback() {

									@Override
									public void isDone(
											ApiomatRequestException exception) {

										if (exception == null) {
											addAttendee(u);
											Intent intent = new Intent(
													UserSelectionActivity.this,
													SubjectActivity.class);
											intent.putExtra(
													MainActivity.EXTRA_CONVERSATION,
													UserSelectionActivity.this.conv);
											startActivityForResult(
													intent,
													MainActivity.EXPECTED_SUBJECT_CODE);

										}
									}
								});

					}
				});
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});
		dialog.setView(popupView);
		dialog.create();
		dialog.show();

	}

}
