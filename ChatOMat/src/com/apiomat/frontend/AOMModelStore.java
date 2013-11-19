/*
 * Copyright (c) 2012 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten UG haftungsbeschraenkt, Botzstrasse 1, 07743 Jena, Germany
 *
 * 20.02.2013
 * phimi
 */
package com.apiomat.frontend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import org.apache.http.impl.cookie.DateUtils;

/**
 * Class handles caching of apiomat models
 *
 * @author phimi
 */
public class AOMModelStore
{
	/* Maps href to json-model */
	final Map<String, String> entries = new HashMap<String, String>( );
	/* This map holds listHref/referenceHref-->List with Hrefs of each element */
	final Map<String, AOMModelStoreEntries> listEntries =
		new HashMap<String, AOMModelStoreEntries>( );
    private String TAG = "AOMModelStore";
    
    final SimpleDateFormat sdf = new SimpleDateFormat( "dd MMM yy HH:mm:ss:SS zz", Locale.US );

	/**
	 * Add a list of models (reference or GET-on-list-Result) to the store
	 *
	 * @param listHref
	 * @param eTag
	 * @param models
	 */
	public <T extends AbstractClientDataModel> void addModels( final String listHref, final String eTag,
		final List<T> models )
	{
		AOMModelStoreEntries elementSet = this.listEntries.get( listHref );
		/* if there no entry, create one */
		if ( elementSet == null )
		{
			elementSet = new AOMModelStoreEntries( eTag, new HashSet<String>( ) );
			this.listEntries.put( listHref, elementSet );
		}
		for ( AbstractClientDataModel elem : models )
		{
			/* add to entries store */
			addModel( elem );
			/* Add href to list */
			elementSet.addHref( elem.getHref( ) );
		}
	}

	/**
	 * Remove list from cache with given href
	 * That method only removes the entry for list and not the entries self
	 * 
	 * @param listHref list HREF
	 */
	public void removeModels( final String listHref )
	{
		this.listEntries.remove( listHref );
	}

	/**
	 * Add single model to store
	 * This method adds only the json represenation to avoid manipulation from extern
	 *
	 * @param model the model
	 */
	public void addModel( final AbstractClientDataModel model )
	{
		addModel( model.getHref( ), model );
	}

	/**
	 * Add a model with given href
	 *  
	 * @param href HREF of model
	 * @param model the model itself
	 */
	public void addModel( final String href, final AbstractClientDataModel model )
	{
		this.entries.put( href, model.toJson( ) );
	}

	/**
	 * Delete single model from cache
	 *
	 * @param model the model to delete
	 */
	public void removeModel( final AbstractClientDataModel model )
	{
		this.entries.remove( model.getHref( ) );
	}

	/**
	 * Get eTag for list HREF
	 * 
	 * @param listHref the list HREF
	 * @return eTag or null if no eTag for list in cache
	 */
	public String getEtagForModels( final String listHref )
	{
		String eTag = null;
		final AOMModelStoreEntries entries = this.listEntries.get( listHref );
		if ( entries != null )
		{
			eTag = entries.getETag( );
		}
		return eTag;
	}

	public String getEtagForModel( final String href, final Class<? extends AbstractClientDataModel> clazz )
	{
		AbstractClientDataModel entry = null;
		String eTag = null;
		String jsonStr = this.entries.get( href );
		try
		{
			if ( jsonStr != null && jsonStr.equals( "" ) == false )
			{
				entry = clazz.newInstance( );
				entry.fromJson( jsonStr );
			}
			if ( entry != null )
			{
				eTag = sdf.format(entry.getLastModifiedAt());
			}
		}
		catch ( Exception e )
		{
			/*Let's log exception but don't throw them */
			Log.w(TAG, "Filling list with models throw error: " + e.getMessage());
		}
		entry = null;
		return eTag;
	}

	/**
	 * Get list of models from cache
	 *
	 * @param listHref Href of the list (reference or GET-Req-Result)
	 * @return list of cached models
	 */
	public <T extends AbstractClientDataModel> List<T> getModels( final String listHref,
		final Class<T> clazz )
	{
		List<T> elems = null;
		final AOMModelStoreEntries entries = this.listEntries.get( listHref );
		if ( entries != null )
		{
			elems = new ArrayList<T>( );
			fillListWithModels( elems, entries, clazz );
		}
		return elems;
	}

	/**
	 * Get JSON representation of model from cache by href
	 *
	 * @param href href of the model
	 * @return Return model if in cache else null
	 */
	public String getModel( final String href )
	{
		return this.entries.get( href );
	}

	/**
	 * Get list of models from cache if eTag equals else return null
	 *
	 * @param listHref Href of the list (reference or GET-Req-Result)
	 * @param eTag the etag to check
	 * @return list of cached models
	 */
	public <T extends AbstractClientDataModel> List<T> getModelsIfEtagEquals(
		final String listHref, final String eTag, final Class<T> clazz )
	{
		List<T> elems = null;
		final AOMModelStoreEntries entries = this.listEntries.get( listHref );
		if ( entries != null && entries.getETag( ) != null && entries.getETag( ).equals( eTag ) )
		{
			elems = new ArrayList<T>( );
			fillListWithModels( elems, entries, clazz );
		}
		return elems;
	}

	/**
	 * Get model from cache by href if eTag is correct
	 *
	 * @param href href of the model
	 * @param eTag eTag to check
	 * @return Return model if in cache else null
	 */
	public AbstractClientDataModel getModelIfEtagEquals( final String href,
		final Class<? extends AbstractClientDataModel> clazz, final String eTag )
	{
		AbstractClientDataModel element = null;
		final String jsonStr = this.entries.get( href );
		AbstractClientDataModel entry;
		try
		{
			entry = clazz.newInstance( );
			entry.fromJson( jsonStr );
			if ( entry.getLastModifiedAt( ).toString( ).equals( eTag ) )
			{
				element = entry;
			}
		}
		catch ( Exception e )
		{
			/* Let's log exception but don't throw them */
			Log.w(TAG, "get models from cache throw error: " + e.getMessage());
		}
		return element;
	}

	/**
	 * @param elems
	 * @param entries
	 * @param clazz
	 */
	private <T extends AbstractClientDataModel> void fillListWithModels( List<T> elems,
		final AOMModelStoreEntries entries, final Class<T> clazz )
	{
		try
		{
			for ( String elementHref : entries.getHrefs( ) )
			{
				final String jsonStr = getModel( elementHref );
				T tmpElem;

				tmpElem = ( T ) clazz.newInstance( ).fromJson( jsonStr );
				if ( tmpElem != null )
				{
					elems.add( tmpElem );
				}
			}
		}
		catch ( Exception e )
		{
			/* Let's log exception but don't throw them */
			Log.w(TAG, "Filling list with models throws error: " + e.getMessage());
        }
	}

	/**
	 * remove all entries of modelstore
	 */
	public void clearStore( )
	{
		this.listEntries.clear( );
		this.entries.clear( );
	}

	/**
	 * Class which holds information about a list of HREFs and his relevant eTag
	 * 
	 * @author phimi
	 */
	class AOMModelStoreEntries
	{
		String eTag;
		Set<String> hrefList = new HashSet<String>( );

		/**
		 * Constructor for class
		 * @param eTag eTag which came back from server
		 * @param hrefList list of HREFs
		 */
		public AOMModelStoreEntries( final String eTag, final Set<String> hrefList )
		{
			this.eTag = eTag;
			this.hrefList = hrefList;
		}

		public void addHref( final String href )
		{
			this.hrefList.add( href );
		}

		public void removeHref( final String href )
		{
			this.hrefList.remove( href );
		}

		public Set<String> getHrefs( )
		{
			return this.hrefList;
		}

		public String getETag( )
		{
			return this.eTag;
		}
	}	
}