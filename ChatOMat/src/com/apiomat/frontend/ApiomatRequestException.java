/* Copyright (c) 2012 All Rights Reserved, http://www.apiomat.com/
 * 
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 * 
 * Apinauten UG haftungsbeschraenkt, Botzstrasse 1, 07743 Jena, Germany
 * 
 * 25.09.2012
 * andreas */
package com.apiomat.frontend;

import com.apiomat.frontend.Status;

/**
 * Custom exception class
 * 
 * @author andreas
 */
public class ApiomatRequestException extends Exception
{
	private static final long serialVersionUID = -319955397589517084L;
	private final int expectedReturnCode;
	private final int returnCode;
	private final String reason;
	private final Status status;

	/**
	 * @param returnCode
	 * @param expectedReturnCode
	 * @param reasonTitle
	 */
	public ApiomatRequestException( int returnCode, int expectedReturnCode,
		String reasonTitle )
	{
		this( returnCode, expectedReturnCode, reasonTitle, reasonTitle );
	}

	/**
	 * Constructor
	 * 
	 * @param expectedReturnCode
	 * @param returnCode
	 * @param reasonTitle
	 * @param reasonBody
	 */
	public ApiomatRequestException( int returnCode, int expectedReturnCode, String reasonTitle, String reasonBody )
	{
		super( parseReason( returnCode, expectedReturnCode, reasonTitle, reasonBody ) );

		this.expectedReturnCode = expectedReturnCode;
		this.returnCode = returnCode;
		this.reason = getMessage( );
		this.status = Status.getStatusForCode( returnCode );
	}
	
	public ApiomatRequestException( final Status status)
	{
		this(status.getStatusCode( ), 0, null,null);
	}

	public int getExpectedReturnCode( )
	{
		return this.expectedReturnCode;
	}

	public int getReturnCode( )
	{
		return this.returnCode;
	}

	public String getReason( )
	{
		return this.reason;
	}

	public Status getStatus( )
	{
		return this.status;
	}

	private static String parseReason( int returnCode, int expectedReturnCode, String reasonTitle, String reasonBody )
	{
		String text = "Return code " + returnCode +
			" does not match expected one (" + expectedReturnCode + ")";
		Status s = Status.getStatusForCode( returnCode );

		if ( reasonTitle != null && reasonTitle.length( ) > 0 )
		{
			text = reasonTitle + " " + reasonBody;
		}
		else if ( s != null )
		{
			text = s.getReasonPhrase( );
		}
		return text;
	}
}
