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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;

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

import com.apiomat.chatomat.adapter.MemberAdapter;
import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.basics.MemberModel;
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
 * @author andreasfey
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MemberSelectionActivity extends Activity {
	private ConversationModel conv;
	private MemberAdapter adapter;
	boolean startNewConversation = true;
	ListView list;
	int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		final Intent i = getIntent();
		if (i.getExtras() != null
				&& i.getExtras().containsKey(MainActivity.EXTRA_CONVERSATION)) {
			MemberSelectionActivity.this.startNewConversation = false;
			MemberSelectionActivity.this.conv = (ConversationModel) i
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
		this.adapter = new MemberAdapter(this);
		this.list.setAdapter(this.adapter);
		this.list.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (MemberSelectionActivity.this.startNewConversation) {
					addConversation(view,
							(MemberModel) MemberSelectionActivity.this.list
									.getItemAtPosition(position));
				} else {
					MemberSelectionActivity.this.position = position;
					final MemberModel memberModel = (MemberModel) MemberSelectionActivity.this.list
							.getItemAtPosition(position);
					memberModel.loadMeAsync(new AOMEmptyCallback() {

						@Override
						public void isDone(ApiomatRequestException exception) {
							if (exception == null) {
								addAttendeeInConversation(memberModel,
										MemberSelectionActivity.this.conv);
							} else {
								Log.e("MemberSelectionActivity",
										"Couldn't load MemberModel");
							}

						}
					});

				}
			}

		});

		StringBuffer filter = new StringBuffer();
		if (MemberSelectionActivity.this.conv != null) {
			for (String attendee : (List<String>) MemberSelectionActivity.this.conv
					.getAttendeeUserNames()) {

				if (filter.length() > 0) {
					filter.append(" AND ");
				}
				filter.append("userName != \"" + attendee + "\"");
			}
		} else {
			filter.append("userName != \"" + MemberCache.getMyself() + "\"");
		}

		// Get MemberModels with filter
		MemberModel.getMemberModelsAsync(filter.toString(),
				new AOMCallback<List<MemberModel>>() {

					@SuppressWarnings("synthetic-access")
					@Override
					public void isDone(List<MemberModel> resultObject,
							ApiomatRequestException exception) {
						for (MemberModel m : resultObject) {

							MemberSelectionActivity.this.adapter.add(m);
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
			MemberModel sender = (MemberModel) intent.getExtras()
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
	public void goBack(@SuppressWarnings("unused") View view) {
		finish();
	}

	/**
	 * add attendee to conversation
	 * @param m
	 */
	private void addAttendee(final MemberModel m) {

		List<String> userNames = MemberSelectionActivity.this.conv
				.getAttendeeUserNames();
		userNames.add(m.getUserName());
		MemberSelectionActivity.this.conv.setAttendeeUserNames(userNames);
		MemberSelectionActivity.this.conv.saveAsync(new AOMEmptyCallback() {

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
	private void addAttendeeInConversation(final MemberModel m,
			final ConversationModel conversationModel) {
		if (conversationModel != null) {
			final AOMEmptyCallback conversationSaveAsync = new AOMEmptyCallback() {

				@Override
				public void isDone(ApiomatRequestException exception) {

					if (exception != null) {
						Log.e("addAttendee", "add attendee failed");
					} else {
						Intent intent = new Intent();
						MemberModel m = (MemberModel) MemberSelectionActivity.this.list
								.getItemAtPosition(MemberSelectionActivity.this.position);
						intent.putExtra(MainActivity.EXTRA_USERNAME,
								m.getUserName());
						setResult(RESULT_OK, intent);
						finish();
					}

				}
			};
			AOMEmptyCallback conversationLoadAsync = new AOMEmptyCallback() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void isDone(ApiomatRequestException exception) {

					List<String> userNames = conversationModel
							.getAttendeeUserNames();

					userNames.add(m.getUserName());
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
	private void addConversation(@SuppressWarnings("unused") View view,
			final MemberModel m) {

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
									MemberSelectionActivity.this);
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
						MemberSelectionActivity.this.conv = new ConversationModel();
						MemberSelectionActivity.this.conv
								.setAttendeeUserNames(Arrays
										.asList(new String[] { MemberCache
												.getMyself() }));
						MemberSelectionActivity.this.conv.setSubject(subject
								.getText().toString());
						MemberSelectionActivity.this.conv
								.saveAsync(new AOMEmptyCallback() {

									@Override
									public void isDone(
											ApiomatRequestException exception) {

										if (exception == null) {
											addAttendee(m);
											Intent intent = new Intent(
													MemberSelectionActivity.this,
													SubjectActivity.class);
											intent.putExtra(
													MainActivity.EXTRA_CONVERSATION,
													MemberSelectionActivity.this.conv);
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
