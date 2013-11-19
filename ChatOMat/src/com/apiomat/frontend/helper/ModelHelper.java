/*
 * Copyright (c) 2012 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten UG haftungsbeschraenkt, Botzstrasse 1, 07743 Jena, Germany
 *
 * 19.03.2013
 * phimi
 */
package com.apiomat.frontend.helper;

import java.util.List;

import com.apiomat.frontend.AbstractClientDataModel;

/**
 * Static class which provides helper methods for apiOmat models
 *
 * @author phimi
 */
public class ModelHelper
{
	/**
	 * Method returns true if given list contains HREF already
	 *
	 * @param list list for search
	 * @param href HREF for which we search
	 * @return true if mdoel with HREF already in list
	 */
	public static boolean containsHref( final List<? extends AbstractClientDataModel> list, final String href )
	{
		boolean containsHref = false;
		for ( AbstractClientDataModel model : list )
		{
			if ( model.getHref( ).equals( href ) )
			{
				containsHref = true;
				break;
			}
		}
		return containsHref;
	}
}
