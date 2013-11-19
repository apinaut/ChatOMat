/*
 * Copyright (c) 2011-2013, Apinauten GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
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
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THIS FILE IS GENERATED AUTOMATICALLY. DON'T MODIFY IT.
 */
package com.apiomat.frontend;

/**
 * apiOmat status codes
*/
public enum Status
{
    SCRIPT_ERROR( 701, "Script error!" ),
    APPLICATION_NOT_ACTIVE( 702, "Application is deactivated!" ),
    BAD_IMAGE( 703, "Image format seems to be corrupted!" ),
    BAD_ID( 704, "ID format is wrong!" ),
    CONCURRENT_ACCESS( 705, "Concurrent access forbidden!" ),
    APPLICATION_SANDBOX( 706, "Application is in sandbox mode!" ),
    MODEL_NOT_DEPLOYED( 707, "Model is not deployed!" ),
    QUERY_ERROR( 708, "Query could not be parsed!" ),
    WRONG_REF_TYPE( 709, "Wrong reference type!" ),
    ATTRIBUTE_NOT_SET( 710, "Attribute not set!" ),
    OPERATION_NOT_POSSIBLE( 711, "CRUD operation not possible on this model!" ),
    APPLICATION_NAME_MISMATCH( 712, "Application name does not match the one defined in the model!" ),
    WRONG_AUTH_HEADER( 713, "Wrong authentication header format, must be 'username:password'" ),
    MODEL_STILL_USED( 714, "Model is still used by other attributes, scripts or subclasses!'" ),
    COLLECTION_NOT_ALLOWED( 715, "Collection is not supported for this model type!" ),
    FB_NO_VALID_MEMBER( 716, "Request send from no valid member" ),
    FB_NO_OAUTH_TOKEN( 717, "Requesting member has no oAuth token, please authenticate! See http://doc.apiomat.com" ),
    FB_POST_ID_MISSING( 718, "Facebook post id has to be set!" ),
    RESTORE_NO_DUMPS_FOUND( 719, "No dumps for app on this date exist!" ),
    TW_NO_VALID_MEMBER( 720, "Request send from no valid member" ),
    TW_NO_OAUTH_TOKEN( 721, "Requesting member has no oAuth token, please authenticate! See http://doc.apiomat.com" ),
    IMEXPORT_WRONG_ENCODING( 722, "Wrong Encoding" ),
    IMEXPORT_WRONG_CONTENT( 723, "Wrong Filecontent" ),
    PUSH_PAYLOAD_EXCEEDED( 724, "Payload size exceeded!" ),
    PUSH_ERROR( 725, "Error in push request!" ),
    BAD_EMAIL( 726, "eMail format is wrong!" ),
    BAD_PROMOTIONCODE_DISCOUNT( 727, "Discount value is wrong!" ),
    BAD_PROMOTIONCODE_CODE( 728, "Code is invalid" ),
    PLAN_PRICE( 729, "Plan price must be >= 0!" ),
    PLAN_NO_SYSTEMS( 730, "Plan must have at leat one system!" ),
    SCRIPT_TIME_ERROR( 731, "Script was interrupted, execution took too long." ),
    INVALID_NAME( 732, "Name must start with a letter, followed only by letters or numbers." ),
    ATTRIBUTE_IN_SUPERCLASS( 733, "Attribute is already defined in superclass." ),
    JSON_TYPE_ERROR( 734, "The @type is not correctly defined in your JSON (must be: APPNAMEMain$CLASSNAME" ),
    TBLR_NO_VALID_MEMBER( 735, "Request send from no valid member" ),
    TBLR_NO_OAUTH_TOKEN( 736, "Requesting member has no oAuth token, please authenticate! See http://doc.apiomat.com" ),
    TBLR_POST_ID_MISSING( 737, "Tumblr post id has to be set!" ),
    LOCATION_INVALID( 738, "Location data is invalid (latitude or longitude missing)!" ),
    SCRIPT_EXCEPTION( 739, "Exception was raised in script!" ),
    APPLICATION_NOT_FOUND( 801, "Application was not found!" ),
    CUSTOMER_NOT_FOUND( 802, "Customer was not found!" ),
    ID_NOT_FOUND( 803, "ID was not found!" ),
    MODEL_NOT_FOUND( 804, "Model was not found!" ),
    MODULE_NOT_FOUND( 805, "Module was not found!" ),
    METAMODEL_NOT_FOUND( 806, "Meta Model was not found!" ),
    PLAN_NOT_FOUND( 807, "Plan was not found!" ),
    PROMOCODE_NOT_FOUND( 808, "Promotion code not valid!" ),
    MODULE_USE_FORBIDDEN( 820, "Required module is not attached to app" ),
    PUSH_ERROR_APIKEY( 821, "No API Key defined for Push service!" ),
    PUSH_ERROR_CERTIFICATE( 822, "No certificate defined for Push service!" ),
    SAME_NAME_USED_IN_SUPERCLASS( 823, "Same name is already used in a superclass." ),
    PAYMENT_MAX_MODULE( 824, "Maximum number of used modules exceeded for this plan." ),
    PAYMENT_SYSTEM( 825, "Selected system use is not allowed for this plan." ),
    PAYMENT_DOWNGRADE( 826, "Up/Downgrading plans is only allowed for super admins." ),
    SAVE_REFERENECE_BEFORE_REFERENCING( 827, "Object you are trying to reference is not on the server. Please save it first." ),
    PAYMENT_DB_SIZE( 828, "Used database size exceeds plan" ),
    ENDPOINT_PATH_NOT_ALLOWED( 829, "Endpoint not allowed for app; please add path to your app's config." ),
    PAYMENT_NO_CRON( 1820, "Cronjobs are not allowed for this plan." ),
    ID_EXISTS( 830, "ID exists!" ),
    NAME_RESERVED( 831, "Name is reserved!" ),
    CIRCULAR_DEPENDENCY( 832, "One of the parent classes must not be the same class!" ),
    UNAUTHORIZED( 840, "Authorization failed!" ),
    WRONG_APIKEY( 841, "API Key was not correct!" ),
    CRUD_ERROR( 901, "Internal error during CRUD operation" ),
    IMEXPORT_ERROR( 902, "Error during im/export!" ),
    COMPILE_ERROR( 903, "Data models could not be compiled!" ),
    REFERENCE_ERROR( 904, "Error in model reference!" ),
    PUSH_PAYLOAD_ERROR( 905, "Failed to create payload!" ),
    PUSH_SEND_ERROR( 906, "Failed to send message(s)!" ),
    PUSH_INIT_FAILED( 907, "Failed to initialize push service!" ),
    FACEBOOK_ERROR( 908, "Error communicationg with facebook!" ),
    FACEBOOK_OAUTH_ERROR( 910, "facebook throws oAuth error! Please show login dialog again" ),
    MEMBER_NOT_FOUND( 911, "Can't find member with this id/username" ),
    WORDPRESS_FETCH_DATA_ERROR( 912, "Can't fetch data for wordpress blog" ),
    TUMBLR_OAUTH_ERROR( 913, "tumblr throws oAuth error! Please show login dialog again" ),
    TUMBLR_ERROR( 914, "Error communicationg with tumblr!" ),
    EXECUTE_METHOD_ERROR_PRIMITIVE( 915, "Only primitive types are allowed" ),
    EXECUTE_METHOD_ERROR( 916, "Execute method fails" ),
    HREF_NOT_FOUND( 601, "Model has no HREF; please save it first!" ),
    WRONG_URI_SYNTAX( 602, "URI syntax is wrong" ),
    WRONG_CLIENT_PROTOCOL( 603, "Client protocol is wrong" ),
    IO_EXCEPTION( 604, "IOException was thrown" ),
    UNSUPPORTED_ENCODING( 605, "Encoding is not supported" ),
    INSTANTIATE_EXCEPTION( 606, "Error on class instantiation" ),
    IN_PERSISTING_PROCESS( 607, "Object is in persisting process. Please try again later" ),
    VERIFY_SOCIALMEDIA( 608, "Can't verify against social media provider" ),
    TOO_MANY_LOCALIDS( 6089, "Can't create more localIDs. Please try again later" ),
    MAX_CACHE_SIZE_REACHED( 6090, "The maximum cache size is reached." ),
    CANT_WRITE_IN_CACHE( 6091, "Can't persist data to cache." ),
    MALICIOUS_MEMBER( 950, "Malicious use of member detected!" ),
    NULL(9999, ""); //placeholder

	private final int code;
	private final String reason;

	Status( final int statusCode, final String reasonPhrase )
	{
		this.code = statusCode;
		this.reason = reasonPhrase;
	}
	
	public int getStatusCode( )
	{
		return this.code;
	}

	public String getReasonPhrase( )
	{
		return this.reason;
	}
	
	public static Status getStatusForCode( int code )
	{
		for ( Status s : Status.values( ) )
		{
			if ( s.getStatusCode( ) == code )
			{
				return s;
			}
		}
		return null;
	}
}
