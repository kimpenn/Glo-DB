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
 * @(#)AbstractFeature.java
 */

package edu.upenn.gloDB;

import java.util.HashMap;
import java.util.Iterator;

/**
 * AbstractFeature.
 *
 * @author  Stephen Fisher
 * @version $Id: AbstractFeature.java,v 1.1.2.10 2007/03/01 21:17:32 fisher Exp $
 */

public abstract class AbstractFeature implements Feature {

	 /** The contig that underlies the positions. */
	 protected String source;

	 /** 
	  * This is similar to "qualifiers" in GenBank.  ex: scores, strand
	  * (+/-), phase (within codon).  Each key/value pair is delimited
	  * by a ";".  the keys and values are separated by "=". ex:
	  * "scores=.; strand=+". Key based attribute searches are case
	  * sensitive.
	  * @XXX Should any of these be hardcoded as fields?
	  */
	 //	 protected HashMap attributes = new HashMap();
	 protected String attributes = "";


	 /** 
	  * Set the source when the Feature is created.  Don't allow the
	  * source to change there after.
	  * @XXX Should throw an exception if source is null.
	  */
	 public AbstractFeature(Sequence source) { 
		  if (source == null) {
				GloDBUtils.printMsg("No sequence info for feature.", 2); // error
		  }
		  this.source = source.getID();
	 }


    //--------------------------------------------------------------------------
    // Setters and Getters
   
	 /** Returns Feature type (see GloDBUtils) */
	 public int getType() { return GloDBUtils.FEATURE; }

    /** 
	  * Set the attributes. 
	  * @param attributes a String of Feature attributes
	  */
    public void setAttributes(String attributes) { 
		  // make sure attributes is never set to null
		  if (GloDBUtils.isEmpty(attributes)) attributes = "";
		  this.attributes = attributes; 
	 }

    /** 
	  * Set the attributes using a HashMap. 
	  * @param attribMap a HashMap of Feature attributes
	  */
    public void setAttributes(HashMap attribMap) { 
		  // test for null or empty maps
		  if ((attribMap == null) || (attribMap.isEmpty())) {
				this.attributes = "";
				return;
		  }
		  
		  Iterator keys = attribMap.keySet().iterator();
		  String key = (String) keys.next();
		  String value = (String) attribMap.get(key);
		  String attribs = key + "=" + value;

		  while (keys.hasNext()) {
				key = (String) keys.next();
				value = (String) attribMap.get(key);
				attribs += ";" + key + "=" + value;
		  }

		  this.attributes = attribs; 
	 }

    /** Get the attributes. */
    public String getAttributes() { return attributes; }
	 //    public HashMap getAttributes() { return attributes; }

    /** 
	  * Returns the underlying Sequence object. 
	  * @return Returns the sequence object.
	  */
    public Sequence getSource() { return ObjectHandles.getSequence(source); }

	 /** Returns the underlying Sequence object's ID. */
	 public String getSourceID() { return source; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
    /** Add an attribute. */
    public void addAttribute(String key, String value) { 
		  if (GloDBUtils.isEmpty(attributes)) attributes += key + "=" + value; 
		  else attributes += ";" + key + "=" + value; 
	 }

    /** Add an attribute (key/value pair). */
    public void addAttribute(String value) { 
		  if (GloDBUtils.isEmpty(attributes)) attributes += value; 
		  else attributes += ";" + value; 
	 }
	 
    /** Remove an attribute. This is case sensitive. */
    public void delAttribute(String key) { 
		  if (GloDBUtils.isEmpty(attributes)) return;

		  String[] pairs = attributes.split(";");
		  String newAttribs = "";
		  
		  for (int i = 0; i < pairs.length; i++) {
				if (! pairs[i].startsWith(key)) {
					 if (i == 0) newAttribs += pairs[i];
					 else newAttribs += ";" + pairs[i];
				}
		  }
		 
		  this.attributes = newAttribs;
	 }

    /** Returns true if attribute 'key' exists. This is case sensitive. */
    public boolean containsAttribute(String key) { 
		  return attributes.matches(".*key\\s?=.+"); 
	 }

    /** 
	  * Get the attributes as HashMap. If no attributes, then an empty
	  * HashMap is returned. 
	  */
	 public HashMap getAttributesMap() { 
		  if (GloDBUtils.isEmpty(attributes)) return new HashMap();

		  String[] pairs = attributes.split(";");

		  HashMap attribMap = new HashMap();
		  for (int i = 0; i < pairs.length; i++) {
				String[] keyVal = pairs[i].split("=");
				if (keyVal.length == 1) {
					 // this should never happen
					 attribMap.put(keyVal[0], keyVal[0]);
					 GloDBUtils.printWarning("Found potentially invalid Feature attributes (" + keyVal[0] + ") in " + attributes);
				} else {
					 attribMap.put(keyVal[0], keyVal[1]);
				}
		  }

		  return attribMap; 
	 }

    /** Get value for attribute 'key'. This is case sensitive. */
    public String getAttribute(String key) { 
		  if (GloDBUtils.isEmpty(attributes)) return "";

		  String[] pairs = attributes.split(";");
		  
		  for (int i = 0; i < pairs.length; i++) {
				if (pairs[i].startsWith(key)) {
					 return pairs[i].split("=")[1];
				}
		  }

		  return "";
	 }

	 /** Returns description and Feature information. */
	 public String toString() {
		  String out = "";

		  // will convert itself to a string
		  if (attributes == null) {
				out += "Attributes: none";  
		  } else {
				out += "Attributes:\n  " + attributes;  
		  }
		  out += "\nSource:  " + source + "\n";

		  return out;
	 }
	
} // AbstractxFeature.java
