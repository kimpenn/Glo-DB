/*
 * Copyright 2007, 2012 Stephen Fisher and Junhyong Kim, University of
 * Pennsylvania.
 *
 * This file is part of Glo-DB.
 * 
 * Glo-DB is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Glo-DB is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Glo-DB. If not, see <http://www.gnu.org/licenses/>.
 *
 * @(#)Sequence.java
 */

package edu.upenn.gloDB;

import java.util.HashMap;
import java.util.Random;

/**
 * Sequence.
 *
 * @author  Stephen Fisher
 * @version $Id: Sequence.java,v 1.30.2.20 2007/03/01 21:17:33 fisher Exp $
 */

public class Sequence { 

	 /** 
	  * This is the number of characters to print out per line when
	  * formatting the output in getDataFormatted().
	  * @XXX This should probably be a user adjustable parameter.
	  */
	 public static int FORMAT_WIDTH = 60;

	 /** 
	  * When true then sequence data will be stored in the Sequence
	  * object in a compressed format.  When false, the data will be
	  * stored as a String object.
	  * @XXX This should probably be a user adjustable parameter.
	  */
	 public static boolean USE_COMPRESSION = false;

	 /** 
	  * This is a unique name for the sequence, that is used by the
	  * parser to identify the sequence.  This can not be changed
	  * to preserve Feature references.
	  */
	 private String id;

	 /** 
	  * This is the object that will handle getting the data for this
	  * Sequence.  The object referenced by dataLoader will use the
	  * values in 'loaderArgs' and return the data as a String.
	  */
	 private SequenceLoader dataLoader = null;

	 /**
	  * This is a map of key:value pairs needed to load the data from
	  * the data source, as defined by 'dataLoader': URL, file, database,
	  * etc.
	  */
	 private HashMap loaderArgs = new HashMap();

	 /** 
	  * This is a flag for whether data has been loaded.  It's possible
	  * that data was 'loaded' from a source that returned an empty
	  * string.
	  */
	 private boolean dataLoaded = false;

	 /** 
	  * This is the starting position for this Sequence on the
	  * chromosome.  If the Sequence is a chromosome, then offset will
	  * be 0.
	  */
	 private int offset;

	 /** 
	  * Metadata related to the sequence.  ex: source, locus, accession
	  * no., version, GI, protein_id.  Should any of these be hardcoded
	  * as fields?
	  */
	 private HashMap attributes = new HashMap();

	 /** 
	  * The sequence raw data as an unformatted string.  The data is
	  * not loaded by default, rather it is loaded when the user
	  * performs an operation that requires the data.  Note that
	  * concatination operations should be done on StringBuffer objects
	  * with the results stored as a String, since Strings are
	  * immutable and thus converted to StringBuffers during the
	  * operations.  This is particularly important when loading data
	  * from a file which might entail a lot of concatinations.
	  */
	 private String data = "";
	 private byte[] cData;
	 private int dataLength = 0;

	 /** Used to create random ids. */
    private static Random random = new Random(System.currentTimeMillis());

	 /** 
	  * Create a new Sequence object and add it to the set of Sequence
	  * objects.
	  */
	 public Sequence() { 
		  this(true, "");
	 }

	 /** 
	  * Create a new Sequence object with the specified id, and add it
	  * to the set of Sequence objects.
	  */
	 public Sequence(String id) { 
		  this(true, id);
	 }

	 /** 
	  * Create a new Sequence object and add the newly created Sequence
	  * object to the set of sequence objects if addToPool is true.
	  * @XXX This should probably be 'protected' instead of 'public'
	  * because all Sequences should really be added to sequencePool.
	  */
	 public Sequence(boolean addToPool) {
		  this(addToPool, "");
	 }

	 /** 
	  * Create a new Sequence object and add the newly created Sequence
	  * object to the set of sequence objects if addToPool is true.
	  * @XXX This should probably be 'protected' instead of 'public'
	  * because all Sequences should really be added to sequencePool.
	  */
	 public Sequence(boolean addToPool, String id) {
		  // if no ID, then create a random ID for this Sequence
		  if (id == "") id = randomID("_S");
		  this.id = id;

		  if (addToPool) { 
				try {
					 // add self to set of all Sequences
					 ObjectHandles.addSequence(this);
				} catch (InvalidIDException e) {
					 String id_new = randomID("_S");
					 String msg = "ID \"" + id
						  + "\" already exists, using ID \"" + id_new + "\" instead.";
					 GloDBUtils.printWarning(msg);
					 
					 // add self to set of all Sequences, using new id
					 this.id = id_new;
					 ObjectHandles.addSequence(this);
				}
		  }
	 }

    //--------------------------------------------------------------------------
    // Setters and Getters
   
	 /** Returns Feature type (see GloDBUtils) */
	 public int getType() { return GloDBUtils.SEQUENCE; }

    /** 
	  * Set the ID.  If the new ID is the same as the current ID, then
	  * doesn't do anything.  If the new ID already exists in the
	  * sequencePool, then throws an exception.
	  * @param id a String that is a unique identifier for the sequence.
	  */
	 /*
	 public void setID(String id) throws InvalidIDException { 
		  try { setID(id, true); } 
		  catch (InvalidIDException e) { throw e; }
	 }
	 */

    /** 
	  * Set the ID.  If the new ID is the same as the current ID, then
	  * doesn't do anything.  If the new ID already exists in the
	  * sequencePool, then throws an exception.  If 'updatePool' is
	  * true, then the sequencePool is updated.  'updatePool' must be
	  * true if the Sequence is in the sequencePool, else the sequencePool
	  * will become out of sync.
	  * @param id a String that is a unique identifier for the sequence.
	  */
	 /*
	 public void setID(String id, boolean updatePool) throws InvalidIDException { 
		  // don't do anything if new and old values are the same
		  if (this.id == id) return;

		  if (updatePool) {
				// renameSequence() will do the actual changing of the
				// Sequence's id.
				try { ObjectHandles.renameSequence(this, id); }
				catch (InvalidIDException e) { throw e; }
		  } else {
				// since not in sequencePool, just change ID
				this.id = id;    
		  }
	 }
	 */

    /** Get the id. */
    public String getID() { return id; }

    /** Set the Sequence source parser. */
	 public void setDataLoader(SequenceLoader dataLoader) { this.dataLoader = dataLoader; }

    /** Returns the parser for the Sequence source. */
    public SequenceLoader getDataLoader() { return dataLoader; }

    /** Set the sequence loaderArgs. */
    public void setLoaderArgs(HashMap loaderArgs) { this.loaderArgs = loaderArgs; }

    /** Get the sequence loaderArgs. */
    public HashMap getLoaderArgs() { return loaderArgs; }

    /** Add a sequence parserArg. */
    public void addLoaderArg(Object key, Object value) { loaderArgs.put(key, value); }

    /** Get a sequence parserArg. */
    public Object getLoaderArg(Object key) { return loaderArgs.get(key); }

    /** Returns true if data was loaded. */
    public boolean isDataLoaded() { return dataLoaded; }

    /** Set the Sequence starting position on the chromosome. */
    public void setOffset(int offset) { this.offset = offset; }

    /** Returns the Sequence starting position on the chromosome. */
    public int getOffset() { return offset; }

    /** 
	  * Set the Sequence data, expecting a single unformatted string.
	  * This will set the dataLoaded flag to 'true'.
	  */
    public void setData(String data) { 
		  if (GloDBUtils.isEmpty(data)) {
				// no data so remove stored data
				this.dataLength = 0;
				this.data = "";
				this.cData = null;
				dataLoaded = false;

		  } else {
				this.dataLength = data.length();
				if (USE_COMPRESSION) {
					 this.cData = GloDBUtils.compressString(data); 
				} else {
					 this.data = data;
				}
				dataLoaded = true;
		  }
	 }

    /** Returns the Sequence data as a single unformatted string. */
    public String getData() { 
		  // loadData() returns "" if data already loaded
		  String locData = loadData();

		  if (GloDBUtils.isEmpty(locData)) {
				// data already loaded
				if (USE_COMPRESSION) {
					 // uncompress data
					 if (cData == null) return "";
					 else return GloDBUtils.uncompressString(cData);
				} else {
					 return this.data;
				}
		  } else {
				return locData;
		  }
	 }

    /** Set the sequence attributes. */
    public void setAttributes(HashMap attributes) { 
		  // make sure attributes is never set to null
		  if (attributes == null) attributes = new HashMap();
		  this.attributes = attributes; 
	 }

    /** Get the sequence attributes. */
    public HashMap getAttributes() { return attributes; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

    /** Add a sequence attribute. */
    public void addAttribute(Object key, Object value) { attributes.put(key, value); }

    /** Remove an attribute. */
    public void delAttribute(Object key) { attributes.remove(key); }

    /** Returns true if attribute 'key' exists. */
    public boolean containsAttribute(Object key) { return attributes.containsKey(key); }

    /** Get a sequence attribute. */
    public Object getAttribute(Object key) { return attributes.get(key); }

	 /**
	  * This will load the data from 'dataLoader' if overwriting the
	  * current value of data.  If dataLoader is null, then this won't
	  * do anything.
	  */
	 public void reloadData() {
		  if (dataLoader != null) {
				// attempt to get data from dataLoader
				setData(dataLoader.getData(loaderArgs));
		  }
	 }

	 /**
	  * This will load the data from 'dataLoader' if data is currently
	  * empty.  If data is not empty, then this won't do anything.
	  * This method is called internally whenever data is used, so the
	  * user should never need to call this method.  We return the
	  * uncompressed data, because in some instances, the method
	  * calling loadData() requires uncompressed data.  If we use
	  * setData() and getData(), then the data will be compressed and
	  * then uncompressed.
	  */
	 public String loadData() {
		  if ((! isDataLoaded()) && (dataLoader != null) && (dataLength == 0)) {
				// data is empty so attempt to get data from dataLoader.
				String data = dataLoader.getData(loaderArgs);
				setData(data);
				return data;
		  }

		  return "";
	 }

    /** 
	  * Returns the length of the data string.  If the dataLoader isn't
	  * set and thus no data is loaded, then will return -1.
	  */
    public int length() { 
		  loadData();  // make sure data is loaded before using data

		  // if data still not loaded, then return -1
		  if (isDataLoaded()) return dataLength;
		  else return -1; 
	 }

	 /**
	  * Returns the initial position of the Sequence on the chromosome.
	  * This will return the same value as getOffset().
	  */
	 public int getMin() { return offset; }

	 /**
	  * Returns the maximum position of the Sequence on the chromosome.
	  * If the dataLoader isn't set and thus no data is loaded, then
	  * will return -1.
	  */
	 public int getMax() { 
		  // if no data and dataLoader not set then flag this by
		  // returning a length of -1
		  if ((! isDataLoaded()) && (dataLoader == null)) return -1;

		  loadData();  // make sure data is loaded before using data
		  return offset + dataLength; 
	 }

	 /**
	  * Returns 'true' if the position 'pos' is contained in this
	  * Sequence object.  
	  */
	 public boolean contains(int pos) { 
		  if ((pos >= offset) && (pos <= getMax())) return true;
		  else return false;
	 }

	 /**
	  * Returns 'true' if 'feature' is contained in this Sequence
	  * object.  This will return 'false' if the Feature's source ID
	  * doesn't match this Sequence's ID.
	  */
	 public boolean contains(Feature feature) { 
		  if (feature.getSourceID() != id) return false;

		  if ((feature.getMin() >= offset) && (feature.getMax() <= getMax())) {
				return true;
		  } else {
				return false;
		  }
	 }

	 /** 
	  * Returns the sequence data between position '(min-1)' and
	  * position 'max'.  Goes from ((min-1) to max) because java
	  * Strings go from (0 to (length-1)) and the actual position data
	  * assumes (1 to length)
	  * @param min the starting position
	  * @param max the ending position
	  */
	 public String getDataBounded(int min, int max) { 
		  // this will load the data if necessary
		  String data = getData();  

		  if (dataLength > 0) {
				// if offset = 0, then not set so need to adjust for
				// sequence starting at 1 and String starting at 0.  If
				// offset is set, then 
				if (offset == 0) {
					 min -= 1;
				} else {
					 min = min - offset;
					 max = (max - offset) + 1;
				}
				//				return data.substring(min-1, max);
				return data.substring(min, max);
		  } else {
				return "";
		  }
	 }

	 /** 
	  * Returns the bounded sequence data with "\n" inserted every
	  * FORMAT_WIDTH characters.
	  */
	 public String getDataBoundedFormatted(int min, int max) {
		  StringBuffer out = new StringBuffer("");

		  String tmp = getDataBounded(min, max);
		  int total = tmp.length();
		  int i = FORMAT_WIDTH;
		  while (i < total) {
				out.append(tmp.substring(i - FORMAT_WIDTH, i) + "\n");
				i += FORMAT_WIDTH;
		  }
		  if (i >= total) out.append(tmp.substring(i - FORMAT_WIDTH, total));

		  return out.toString();
	 }

	 /** 
	  * Returns the sequence data with "\n" inserted every FORMAT_WIDTH
	  * characters.
	  */
	 public String getDataFormatted() {
		  // this will load the data if necessary
		  String data = getData();  

		  StringBuffer out = new StringBuffer("");
		  int i = FORMAT_WIDTH;
		  while (i < dataLength) {
				out.append(data.substring(i - FORMAT_WIDTH, i) + "\n");
				i += FORMAT_WIDTH;
		  }
		  if (i >= dataLength) out.append(data.substring(i - FORMAT_WIDTH, dataLength));

		  return out.toString();
	 }

	 /*
	  * Uses 'base' to create a random ID string that doesn't already
	  * exist in the sequencePool.
	  */
	 public static String randomID(String base) {
		  String id = base + Long.toString(Math.abs(random.nextLong()));
		  while (ObjectHandles.sequencePool.containsKey(id)) {
				id = base + Long.toString(Math.abs(random.nextLong()));
		  }
		  return id;
	 }

	 /** 
	  * Returns attributes information.  The data isn't included here.
	  * To get the data use {@link #getData() getData()} or {@link
	  * #getDataFormatted() getDataFormatted()}.
	  */
	 public String toString() {
		  String out = "";

		  out += "ID: " + id + "\n";
		  out += "Offset: " + offset + "\n";

		  if ((attributes == null) || attributes.isEmpty()) {
				out += "Attributes: none";
		  } else {
				out += "Attributes:\n  " + attributes;  // will convert itself to a string
		  }

		  if (dataLength > 0) {
				out += "\nSequence length: " + dataLength;
		  } else if (dataLoader == null) {
				out += "\nSequence length:  dataLoader not set";
		  } else {
				out += "\nSequence length:  data not yet loaded";
		  }

		  return out;
	 }

} // Sequence.java
