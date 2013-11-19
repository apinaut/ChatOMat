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
package com.apiomat.frontend.basics;

import java.util.*;
import com.apiomat.frontend.*;
import com.apiomat.frontend.basics.*;
import com.apiomat.frontend.callbacks.*;
import com.apiomat.frontend.helper.*;

import rpc.json.me.*;


/**
* Generated default class representing a user in your app 
*/
public class User extends AbstractClientDataModel
{
    public static final String apiKey = "9160863907729554565";
    public static final String baseURL = "https://apiomat.org/yambas/rest/apps/ChatOMatAsync";
    public static final String system = "LIVE";
    public static final String sdkVersion = "1.7-69";
    /**
    * Default constructor. Needed for internal processing.
    */
    public User ( )
    {
        super( );
    }

    /**
    * Returns the simple name of this class 
    */
    public String getSimpleName( )
    {
        return "User";
    }

    /**
    * Returns the name of the module where this class belongs to
    */
    public String getModuleName( )
    {
        return "Basics";
    }
    
    /**
    * Returns the system to connect to
    */
    public String getSystem( )
    {

        return system;
    }

    
    /**
    * Initialize Datastore with username/password if not done yet
    *
    * @Exception IllegalStateException Throw if no username/password given
    */
    private void initDatastoreWithMembersCredentialsIfNeeded() 
    {
        try 
        {
            Datastore.getInstance();
        } 
        catch (IllegalStateException e) 
        {
            //if the datastore is not initialized then do so
            if (getUserName() != null && getPassword() != null)
            {
                Datastore.configure(baseURL, apiKey, this.getUserName(), this.getPassword(), sdkVersion, system);
            }
            else
            {
                throw new IllegalStateException("Can't init Datastore without username/password" );
            }
        }
    }
    
    /**
    * Updates this class from server 
    */
    public void loadMe( ) throws ApiomatRequestException
    {
        initDatastoreWithMembersCredentialsIfNeeded();
        load( "models/me" );
    }
    
    /**
     * Updates this class from server in the background and not on the UI thread
     * 
     * @param callback
     */
    public void loadMeAsync(AOMEmptyCallback callback) 
    {
        initDatastoreWithMembersCredentialsIfNeeded();
        loadAsync("models/me", callback);
    }
    
    /**
    * Saves this data model. If it has no HREF this leads to a post and the model 
    * is created on the server, else it is updated. After the save a load will
    * be called to load the actual object from the server. 
    */
    @Override
    public void save() throws ApiomatRequestException 
    {
        initDatastoreWithMembersCredentialsIfNeeded();
        super.save();
    }
    
    /**
    * Saves this data model. If it has no HREF this leads to a post and the model 
    * is created on the server, else it is updated. After the save a load will
    * be called to load the actual object from the server. 
    */
    public void saveAsync( final AOMEmptyCallback callback )
    {
        initDatastoreWithMembersCredentialsIfNeeded();
        super.saveAsync(callback);
    }

    /**
    * Requests a new password; user will receive an email to confirm
    */
    public void requestNewPassword( )
    {
        AOMCallback<String> cb = new AOMCallback<String>() {
            @Override
            public void isDone(String refHref, ApiomatRequestException ex) {
            }
        };
        Datastore.getInstance( ).postOnServerAsync(this, "models/requestResetPassword/", cb );
    }
    
    /**
    * Reset password 
    * @param newPassword the new password
    */
    public void resetPassword( String newPassword ) throws ApiomatRequestException
    {
        this.setPassword( newPassword );
        Datastore.getInstance( ).updateOnServer( this );
        Datastore.configure(baseURL, apiKey, this.getUserName(), this.getPassword(), sdkVersion, system);
    }

    /**
    * Reset password asynchronously
    * @param newPassword the new password
    */
    public void resetPasswordAsync( final String newPassword, final AOMEmptyCallback callback ) throws ApiomatRequestException
    {
        if ( getCurrentState( ).equals( ObjectState.PERSISTING ) )
        {
            throw new IllegalStateException(
                "Object is in persisting process. Please try again later" );
        }
        AOMTask<Void> task = new AOMTask<Void>( )
        {
            @Override
            public Void doRequest( ) throws ApiomatRequestException
            {
                resetPassword( newPassword );
                return null;
            }
        };
        task.execute( callback );
    }

    /**
    * Returns a list of objects of this class filtered by the given query from server
    * @query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
    */
    public static final List<User> getUsers( String query ) throws ApiomatRequestException
    {
        User o = new User();
        return Datastore.getInstance( ).loadFromServer( User.class, o.getModuleName( ),
            o.getSimpleName( ), query );
    }
    
    /**
     * Get a list of objects of this class filtered by the given query from server
     * This method works in the background and call the callback function when finished
     *
     * @param query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
     * @param listAOMCallback The callback method which will called when request is finished
     */
    public static void getUsersAsync(final String query, final AOMCallback<List<User>> listAOMCallback) 
    {
       getUsersAsync(query, false, listAOMCallback);
    }
    
    /**
    * Returns a list of objects of this class filtered by the given query from server
    *
    * @query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
    * @param withReferencedHrefs set to true to get also all HREFs of referenced models
    */
    public static final List<User> getUsers( String query, boolean withReferencedHrefs ) throws Exception
    {
        User o = new User();
        return Datastore.getInstance( ).loadFromServer( User.class, o.getModuleName( ),
            o.getSimpleName( ), withReferencedHrefs, query);
    }
    
    /**
     * Get a list of objects of this class filtered by the given query from server
     * This method works in the background and call the callback function when finished
     *
     * @param query a query filtering the results in SQL style (@see <a href="http://doc.apiomat.com">documentation</a>)
     * @param withReferencedHrefs set true to get also all HREFs of referenced models
     * @param listAOMCallback The callback method which will called when request is finished
     */
    public static void getUsersAsync(final String query, final boolean withReferencedHrefs, final AOMCallback<List<User>> listAOMCallback) 
    {
         User o = new  User();
        Datastore.getInstance().loadFromServerAsync(User.class,o.getModuleName(), o.getSimpleName(), withReferencedHrefs, query, listAOMCallback);
    }


        public String getProfession()
    {
        if(this.data.optJSONObject( "dynamicAttributes" ).isNull( "profession" ))
        {
            return null;
        }
        return (String)this.data.optJSONObject( "dynamicAttributes" ).get( "profession" );
    }

    public void setProfession( String arg )
    {
        String profession = arg;
        this.data.optJSONObject( "dynamicAttributes" ).put( "profession",  profession );
    }

        public String getPassword()
    {
         return this.data.optString( "password" );
    }

    public void setPassword( String arg )
    {
        String password = arg;
        this.data.put( "password", password );
    }

        public String getSex()
    {
        if(this.data.optJSONObject( "dynamicAttributes" ).isNull( "sex" ))
        {
            return null;
        }
        return (String)this.data.optJSONObject( "dynamicAttributes" ).get( "sex" );
    }

    public void setSex( String arg )
    {
        String sex = arg;
        this.data.optJSONObject( "dynamicAttributes" ).put( "sex",  sex );
    }

        public Map getDynamicAttributes()
    {
        return this.data.optJSONObject( "dynamicAttributes" ).getMyHashMap( );
    }

    public void setDynamicAttributes( Map map )
    {
        if( !this.data.has( "dynamicAttributes" ))
        {
            this.data.put( "dynamicAttributes", new Hashtable( ) );
        }
        else
        {
            this.data.optJSONObject( "dynamicAttributes" ).getMyHashMap( ).clear();
        }
        this.data.optJSONObject( "dynamicAttributes" ).getMyHashMap( ).putAll(map);
    }

        public String getLastName()
    {
         return this.data.optString( "lastName" );
    }

    public void setLastName( String arg )
    {
        String lastName = arg;
        this.data.put( "lastName", lastName );
    }

        public String getFirstName()
    {
         return this.data.optString( "firstName" );
    }

    public void setFirstName( String arg )
    {
        String firstName = arg;
        this.data.put( "firstName", firstName );
    }

        public Date getDateOfBirth( )
    {
        return new Date( this.data.getLong( "dateOfBirth" ) );
    }

    public void setDateOfBirth( Date dateOfBirth )
    {
        this.data.putOpt( "dateOfBirth", dateOfBirth.getTime( ) );
    }


        public String getUserName()
    {
         return this.data.optString( "userName" );
    }

    public void setUserName( String arg )
    {
        String userName = arg;
        this.data.put( "userName", userName );
    }

    
    /**
    * Returns the URL of the resource.
    * @return the URL of the resource
    */
    public String getImageURL()
    {
        if(this.data.optJSONObject( "dynamicAttributes" ).isNull( "imageURL" ))
        {
            return null;
        }
        return (String)this.data.optJSONObject( "dynamicAttributes" ).get( "imageURL" ) 
            + ".img?apiKey=" + User.apiKey + "&system=" + this.getSystem();
    }

    public String postImage( byte[] data ) throws Exception
    {
        String href = null;
        if(Datastore.getInstance().sendOffline("POST"))
        {
            final String sendHREF = Datastore.getInstance().createStaticDataHref(true);
            href = Datastore.getInstance().getOfflineHandler().addTask("POST", sendHREF, data);
        }
        else
        {
            href = Datastore.getInstance( ).postStaticDataOnServer( data, true);
        }
        
        if(href != null && href.length() > 0)
        {
            this.data.optJSONObject( "dynamicAttributes" ).put( "imageURL",  href );
            this.save();
        }
        return href;
    }
    
    public void postImageAsync( final byte[] data, final AOMEmptyCallback _callback )
    {
        AOMCallback<String> cb = new AOMCallback<String>() {
            @Override
            public void isDone(String href, ApiomatRequestException ex) {
                if(ex == null && href!=null && href.length()>0)
                {
                    User.this.data.optJSONObject( "dynamicAttributes" ).put( "imageURL",  href );
                    /* save new image reference in model */
                    User.this.saveAsync(new AOMEmptyCallback() {
                        @Override
                        public void isDone(ApiomatRequestException exception) {
                            if(_callback != null)
                            {
                                _callback.isDone(exception);
                            }
                            else
                            {
                                System.err.println("Exception was thrown: " + exception.getMessage());
                            }
                        }
                    });
                }
                else
                {
                    if(_callback != null && ex != null)
                    {
                        _callback.isDone(ex);
                    }
                    else if(_callback != null && ex == null)
                    {
                        _callback.isDone(new ApiomatRequestException(Status.HREF_NOT_FOUND));
                    }
                    else
                    {
                        System.err.println("Exception was thrown: " + (ex != null?ex.getMessage(): Status.HREF_NOT_FOUND.toString()));
                    }
                }
            }
        };
        
        if(Datastore.getInstance().sendOffline("POST"))
        {
            final String sendHREF = Datastore.getInstance().createStaticDataHref(true);
            String refHref = Datastore.getInstance().getOfflineHandler().addTask("POST", sendHREF, data);
            cb.isDone(refHref, null);
        }
        else
        {
            Datastore.getInstance( ).postStaticDataOnServerAsync( data, true, cb);
        }
    }
    
    public void deleteImage() throws Exception
    {
        final String imageURL = getImageURL();
        this.data.optJSONObject( "dynamicAttributes" ).remove( "imageURL" );
        if(Datastore.getInstance().sendOffline("DELETE"))
        {
            Datastore.getInstance().getOfflineHandler().addTask("DELETE", imageURL);
            this.save();
        }
        else
        {
            Datastore.getInstance( ).deleteOnServer(imageURL);
            this.save();
        }
    }
    
    public void deleteImageAsync(final AOMEmptyCallback _callback)
    {
        AOMEmptyCallback cb = new AOMEmptyCallback() {
            @Override
            public void isDone(ApiomatRequestException ex)
            {
                if(ex == null )
                {
                    User.this.data.optJSONObject( "dynamicAttributes" ).remove( "imageURL" );
                    /* save deleted image reference in model */
                    User.this.saveAsync(new AOMEmptyCallback() {
                        @Override
                        public void isDone(ApiomatRequestException exception) {
                            if(_callback != null)
                            {
                                _callback.isDone(exception);
                            }
                            else
                            {
                                System.err.println("Exception was thrown: " + exception.getMessage());
                            }
                        }
                    });
                }
                _callback.isDone(ex);
            }
        };
        final String url = getImageURL();
        if(Datastore.getInstance().sendOffline("DELETE"))
        {
            Datastore.getInstance().getOfflineHandler().addTask("DELETE", url);
            cb.isDone(null);
        }
        else
        {
            Datastore.getInstance( ).deleteOnServerAsync( url, cb);
        }
    }

    /**
    * Returns an URL of the image. <br/>
    * You can provide several parameters to manipulate the image:
    * @param width the width of the image, 0 to use the original size. If only width or height are provided, 
    *        the other value is computed.
    * @param height the height of the image, 0 to use the original size. If only width or height are provided, 
    *        the other value is computed.
    * @param backgroundColorAsHex the background color of the image, null or empty uses the original background color. Caution: Don't send the '#' symbol!
    *        Example: <i>ff0000</i>
    * @param alpha the alpha value of the image, null to take the original value.
    * @param format the file format of the image to return, e.g. <i>jpg</i> or <i>png</i>
    * @return the URL of the image
    */
    public String getImageURL(int width, int height, String backgroundColorAsHex, 
        Double alpha, String format)
    {
        if(this.data.optJSONObject( "dynamicAttributes" ).isNull( "imageURL" ))
        {
            return null;
        }
        String parameters =  ".img?apiKey=" + User.apiKey + "&system=" + this.getSystem();
        parameters += "&width=" + width + "&height=" + height;
        if(backgroundColorAsHex != null) 
        {
            parameters += "&bgcolor=" + backgroundColorAsHex;
        }
        if(alpha != null)
            parameters += "&alpha=" + alpha;
        if(format != null)
            parameters += "&format=" + format;
        return (String)this.data.optJSONObject( "dynamicAttributes" ).get( "imageURL" ) + parameters;
    }


        public Integer getAge()
    {
        if(this.data.optJSONObject( "dynamicAttributes" ).isNull( "age" ))
        {
            return null;
        }
        return (Integer)this.data.optJSONObject( "dynamicAttributes" ).get( "age" );
    }

    public void setAge( Integer arg )
    {
        Integer age = arg;
        this.data.optJSONObject( "dynamicAttributes" ).put( "age",  age );
    }

        public double getLocLatitude( )
    {
         final JSONArray loc = this.data.optJSONArray( "loc" );
         final Object raw = loc.get( 0 );

         return convertNumberToDouble( raw );
    }
    
    public double getLocLongitude( )
    {
         final JSONArray loc = this.data.optJSONArray( "loc" );
         final Object raw = loc.get( 1 );

         return convertNumberToDouble( raw );
    }
    public void setLocLatitude( double latitude )
    {
        if ( this.data.has( "loc" ) == false )
        {
            this.data.put( "loc", new JSONArray( ) );
        }

        this.data.getJSONArray( "loc" ).put( 0, latitude );
    }
    
    public void setLocLongitude( double longitude )
    {
        if ( this.data.has( "loc" ) == false )
        {
            this.data.put( "loc", new JSONArray( ) );
        }
        if ( this.data.getJSONArray( "loc" ).length( ) == 0 )
        {
            this.data.getJSONArray( "loc" ).put( 0, 0 );
        }

        this.data.getJSONArray( "loc" ).put( 1, longitude );
    }


        public String getCompany()
    {
        if(this.data.optJSONObject( "dynamicAttributes" ).isNull( "company" ))
        {
            return null;
        }
        return (String)this.data.optJSONObject( "dynamicAttributes" ).get( "company" );
    }

    public void setCompany( String arg )
    {
        String company = arg;
        this.data.optJSONObject( "dynamicAttributes" ).put( "company",  company );
    }

}
