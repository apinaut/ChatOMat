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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.apiomat.chatomat.adapter.AttendeeAdapter;
import com.apiomat.chatomat.adapter.ChatMessageAdapter;
import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;
import com.apiomat.frontend.chat.ChatMessageModel;
import com.apiomat.frontend.chat.ConversationModel;

/**
 * Activity which shows the conversation details, starting with the attendees,
 * the subject and all messages as baloons
 * 
 * @author apiomat
 */
public class SubjectActivity extends Activity {
	private ConversationModel conv;
	private ChatMessageAdapter chatMessageAdapter;
	private AttendeeAdapter attendeeAdapter;
	private Timer t;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subject);

		Intent i = getIntent();
		this.conv = (ConversationModel) i.getExtras().getSerializable(
				MainActivity.EXTRA_CONVERSATION);

		/* Draw grid of attendees */
		final GridView list = (GridView) findViewById(R.id.attendeesList);
		this.attendeeAdapter = new AttendeeAdapter(this,
				this.conv.getAttendeeUserNames());
		list.setAdapter(this.attendeeAdapter);

		Resources res = getResources();
		Bitmap bMap = BitmapFactory.decodeResource(res,
				R.drawable.apinauts_header_clean);
		BitmapDrawable actionBarBackground = new BitmapDrawable(res, bMap);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(actionBarBackground);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle("Subject: " + this.conv.getSubject());

		/* Draw subject title */
		TextView subjectText = (TextView) findViewById(R.id.subjectTitle);
		subjectText.setText("Conversation subject: " + this.conv.getSubject());

		/* Draw messages */

		final ListView mlist = (ListView) findViewById(R.id.messageList);
		this.chatMessageAdapter = new ChatMessageAdapter(this,
				UserCache.getMyself());
		mlist.setAdapter(this.chatMessageAdapter);

		/* new message */
		final EditText newMessage = (EditText) findViewById(R.id.newMessageText);
		newMessage.setHint(R.string.newMessage);
		newMessage.setOnEditorActionListener(new OnEditorActionListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public boolean onEditorAction(TextView paramTextView, int paramInt,
					KeyEvent paramKeyEvent) {
				if (newMessage.getText().toString().length() > 0) {
					final ChatMessageModel chatMessageModel = new ChatMessageModel();
					chatMessageModel.setText(newMessage.getText().toString());
					chatMessageModel.setSenderUserName(UserCache.getMyself());
					AOMEmptyCallback chatMessageSaveAsync = new AOMEmptyCallback() {

						@Override
						public void isDone(ApiomatRequestException exception) {
							if (exception == null) {
								// post messages <Async task>
								SubjectActivity.this.conv.postMessagesAsync(
										chatMessageModel,
										new AOMEmptyCallback() {

											@Override
											public void isDone(
													ApiomatRequestException exception) {
												chatMessageModel
														.loadAsync(new AOMEmptyCallback() {

															@Override
															public void isDone(
																	ApiomatRequestException exception) {
																Toast.makeText(SubjectActivity.this, "Your message has been sent successfully", Toast.LENGTH_LONG).show();
																SubjectActivity.this.chatMessageAdapter
																		.add(chatMessageModel);
																newMessage
																		.setText("");
																mlist.setSelection(mlist
																		.getCount() - 1);

															}
														});

											}
										});

							} else {
								Log.e("MessageModel", "save failed");
							}

						}
					};
					chatMessageModel.saveAsync(chatMessageSaveAsync);

				}
				return false;
			}
		});

	}

	/**
	 * Navigates back to the main activity
	 * 
	 * @param view
	 */
	public void goBack(View view) {
		/* called from Main screen */
		Intent intent = new Intent();

		if (this.chatMessageAdapter.getCount() > 0) {
			setResult(RESULT_OK, intent);
			ChatMessageModel msg = this.chatMessageAdapter
					.getItem(this.chatMessageAdapter.getCount() - 1);
			intent.putExtra(MainActivity.EXTRA_LAST_MESSAGE, msg);
			intent.putExtra(MainActivity.EXTRA_MEMBER,
					this.chatMessageAdapter.getUserFromLastMessage());
		} else {
			setResult(RESULT_CANCELED, intent);
		}
		finish();
	}

	/**
	 * Opens the list of members
	 * 
	 * @param view
	 */
	public void addAttendee(View view) {
		Intent intent = new Intent(this, UserSelectionActivity.class);
		intent.putExtra(MainActivity.EXTRA_CONVERSATION, this.conv);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 0 && resultCode == RESULT_OK) {
			String newAttendee = intent.getExtras().getString(
					MainActivity.EXTRA_USERNAME);
			this.attendeeAdapter.add(newAttendee);
		}
	}

	@SuppressWarnings("synthetic-access")
	@Override
	protected void onResume() {
		super.onRestart();

		/* Start timer to fetch messages periodically */
		this.t = new Timer();
		this.t.scheduleAtFixedRate(new RefreshMessagesTimer(), 0, 2000) ;
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.t.cancel();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_subject, menu);
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

	/**
	 * refresh messages in a TimerTask
	 * 
	 * @author Tim
	 */
	private class RefreshMessagesTimer extends TimerTask {
		@SuppressWarnings("synthetic-access")
		@Override
		public void run() {
			SubjectActivity.this.conv.loadMessagesAsync("",
					new AOMEmptyCallback() {

						@Override
						public void isDone(ApiomatRequestException exception) {

							List<ChatMessageModel> chatMessageModels = SubjectActivity.this.conv
									.getMessages();

							for (final ChatMessageModel mm : chatMessageModels) {
								boolean alreadyExists = false;
								for (int i = 0; i < SubjectActivity.this.chatMessageAdapter
										.getCount(); i++) {
									if (SubjectActivity.this.chatMessageAdapter
											.getItem(i).getHref() == null) {
										SubjectActivity.this.chatMessageAdapter
												.remove(SubjectActivity.this.chatMessageAdapter
														.getItem(i));
										break;
									}
									if (SubjectActivity.this.chatMessageAdapter
											.getItem(i).getHref()
											.equals(mm.getHref())) {
										alreadyExists = true;
										break;
									}
								}
								if (!alreadyExists) {
									SubjectActivity.this
											.runOnUiThread(new Runnable() {
												@Override
												public void run() {
													SubjectActivity.this.chatMessageAdapter
															.add(mm);
												}
											});

								}
							}

						}
					});

		}
	}
}
