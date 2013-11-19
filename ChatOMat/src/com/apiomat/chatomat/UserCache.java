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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.Datastore;
import com.apiomat.frontend.basics.User;
import com.apiomat.frontend.callbacks.AOMCallback;
import com.apiomat.frontend.callbacks.AOMEmptyCallback;

/**
 * Small cache which stores all member objects and their images
 * 
 * @author apiomat
 */
public class UserCache {
	private final Map<String, User> mapUserNameToUser = new HashMap<String, User>();
	private final Map<String, Bitmap> mapUsernameToImage = new HashMap<String, Bitmap>();
	private String myself;

	private static UserCache instance;
	private static AOMEmptyCallback userLoadMeAsync;

	private UserCache() {
	}

	private static UserCache getInstance() {
		if (instance == null) {
			instance = new UserCache();
		}
		return instance;
	}

	/**
	 * Map contains a item with the following string?
	 * 
	 * @param userName
	 * @return true or false
	 */
	public static boolean containsMember(String userName) {
		return getInstance().mapUserNameToUser.containsKey(userName);
	}

	/**
	 * Map contains a image from the user
	 * 
	 * @param userName
	 * @return true or false
	 */
	public static boolean containsImage(String userName) {
		return getInstance().mapUsernameToImage.containsKey(userName)
				&& getInstance().mapUsernameToImage.get(userName) != null;
	}

	/**
	 * Get memberModel from string username
	 * 
	 * @param userName
	 * @return MemberModel
	 */
	public static User getUser(String userName) {
		return getInstance().mapUserNameToUser.get(userName);
	}

	/**
	 * Get a Bitmap from string username
	 * 
	 * @param userName
	 * @return Bitmap
	 */
	public static Bitmap getImage(final String userName) {
		if (!getInstance().mapUsernameToImage.containsKey(userName)) {
			AOMCallback<List<User>> memberModelsList = new AOMCallback<List<User>>() {
				URL newurl;
				Bitmap bm;

				@Override
				public void isDone(List<User> resultObject,
						ApiomatRequestException exception) {

					if (exception == null) {
						for (int i = 0; i < resultObject.size(); i++) {
							//select user with username
							if (resultObject.get(i).getUserName()
									.equals(userName)) {
								//Get image url
								if (resultObject.get(i).getImageURL() != null) {
									try {
										this.newurl = new URL(
												resultObject.get(i)
														.getImageURL());
										this.bm = BitmapFactory
												.decodeStream(this.newurl
														.openConnection()
														.getInputStream());
									} catch (MalformedURLException e) {

										Log.e("getImage",
												"MalformedURLException");
									} catch (IOException e) {

										Log.e("getImage", "IOException");
									}
								}

							}
						}
						getInstance().mapUsernameToImage.put(userName,
								this.bm);
					} else {
						Log.e("getImage", "MemberModel Exception");
					}

				}
			};
			User.getUsersAsync("",
					memberModelsList);
		}
		return getInstance().mapUsernameToImage.get(userName);
	}

	/**
	 * Get MemberModel from string myself
	 * 
	 * @return MemberModel
	 */
	public static User getMySelf() {
		return getInstance().mapUserNameToUser.get(getInstance().myself);
	}

	/**
	 * Get Bitmap from string myself
	 * 
	 * @return Bitmap
	 */
	public static Bitmap getMySelfImage() {
		return getInstance().mapUsernameToImage.get(getInstance().myself);
	}

	/**
	 * put Member from MemberModel
	 * 
	 * @param member
	 */
	public static void putUser(User member) {
		if (member != null) {
			getInstance().mapUserNameToUser.put(member.getUserName(), member);
		}
	}

	/**
	 * Put Image from username and image
	 * 
	 * @param userName
	 * @param image
	 */
	public static void putImage(String userName, Bitmap image) {
		if (image != null) {
			getInstance().mapUsernameToImage.put(userName, image);
		}
	}

	/**
	 * get string myself -->username
	 * 
	 * @return String
	 */
	public static final String getMyself() {
		return getInstance().myself;
	}

	/**
	 * Set myself
	 * 
	 * @param myself
	 */
	public static final void setMyself(String myself) {
		getInstance().myself = myself;
	}

	/**
	 * Load a memberModel to cache
	 * 
	 * @param userName
	 * @param password
	 */
	public static void loadMemberToCache(final String userName, String password) {
		if (!containsMember(userName)) {
			final User user = new User();
			user.setUserName(userName);
			user.setPassword(password);

			if (user.getUserName().equals("")) {
				user.setUserName(user.getFirstName() + user.getLastName());
			}
			Datastore.configure(user);
			userLoadMeAsync = new AOMEmptyCallback() {

				@Override
				public void isDone(ApiomatRequestException exception) {
					if (exception != null) {
						Log.w("MemberCache", "Error loading member");
						AOMEmptyCallback memberSaveAsnc = new AOMEmptyCallback() {

							@Override
							public void isDone(ApiomatRequestException exception) {
								
								getInstance().mapUserNameToUser.put(userName,
										user);
							}
						};
						user.saveAsync(memberSaveAsnc);
					}
				}
			};
			user.loadMeAsync(userLoadMeAsync);

		}
	}
}
