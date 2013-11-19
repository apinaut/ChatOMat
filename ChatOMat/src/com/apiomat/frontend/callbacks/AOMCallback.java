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
package com.apiomat.frontend.callbacks;

import com.apiomat.frontend.ApiomatRequestException;

/**
 * @author phimi
 * @param <T>
 */
public abstract class AOMCallback<T> {
	/**
	 * Called when request finished
	 * 
	 * @param resultObject
	 *            Contains resultObject if there else null
	 * 
	 * @param exception
	 *            An exception object on failure else null
	 */
	public abstract void isDone(T resultObject,
			ApiomatRequestException exception);
}