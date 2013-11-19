/*
 * Copyright (c) 2007 - 2011 All Rights Reserved, http://www.match2blue.com/
 * 
 * This source is property of match2blue.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 * 
 * match2blue software development GmbH, Leutragraben 1, 07743 Jena, Germany
 * 
 * 24.01.2013
 * phimi
 */
package com.apiomat.frontend.callbacks;

import com.apiomat.frontend.ApiomatRequestException;

/**
 * @author phimi
 */
public abstract class AOMEmptyCallback extends AOMCallback<Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apiomat.frontend.helper.AOMCallback#isDone(java.lang.Object,
	 * com.apiomat.frontend.ApiomatRequestException)
	 */
	@Override
	public void isDone(Void resultObject, ApiomatRequestException exception) {
		isDone(exception);
	}

	public abstract void isDone(ApiomatRequestException exception);

}
