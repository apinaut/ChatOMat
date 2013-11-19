/*
 * Copyright (c) 2007 - 2011 All Rights Reserved, http://www.match2blue.com/
 * 
 * This source is property of match2blue.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 * 
 * match2blue software development GmbH, Leutragraben 1, 07743 Jena, Germany
 * 
 * 23.01.2013
 * phimi
 */
package com.apiomat.frontend.helper;

import android.os.AsyncTask;

import com.apiomat.frontend.ApiomatRequestException;
import com.apiomat.frontend.callbacks.AOMCallback;

/**
 * AsyncTask which is used to communicate with the server in background
 *
 * @author phimi
 * @param <T>
 */
public abstract class AOMTask<T> extends
AsyncTask<AOMCallback<T>, Void, T> {

	private ApiomatRequestException thrownException;
	private AOMCallback<T> callbackMethod;

	public abstract T doRequest() throws ApiomatRequestException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected T doInBackground(AOMCallback<T>... params) {
		this.callbackMethod = params != null ? params[0] : null;
		T result = null;
		try {
			result = doRequest();
		} catch (ApiomatRequestException e) {
			this.thrownException = e;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(T result) {
		super.onPostExecute(result);
		if (this.callbackMethod != null) {
			this.callbackMethod.isDone(result, this.thrownException);
		}
	}
}
