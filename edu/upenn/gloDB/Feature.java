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
 * @(#)Feature.java
 */

package edu.upenn.gloDB;

import java.util.HashMap;
import java.io.Serializable;

/** 
 * Feature interface.  Features implement Comparable and thus must
 * implement a compareTo() method.  This is required to maintain the
 * sorting of Features in Tracks.
 *
 * @author  Stephen Fisher
 * @version $Id: Feature.java,v 1.53.2.9 2007/03/01 21:17:32 fisher Exp $
 */

public interface Feature extends Comparable, Serializable { 

    /** Set the Feature attributes. */
	 public void setAttributes(String attributes);

    /** Set the Feature attributes from a HashMap. */
	 public void setAttributes(HashMap attribMap);

    /** Get the Feature attributes. */
	 public String getAttributes();

    /** Returns true if attribute 'key' exists. */
    public boolean containsAttribute(String key);

    /** Get the Feature attributes as HashMap. */
	 public HashMap getAttributesMap();

    /** Get value for attribute 'key'. */
    public String getAttribute(String key);

	 /**
	  * Returns the start position of the Feature.  This should return
	  * the same value as getMin().
	  */
	 public int getStart();

	 /**
	  * Returns the maximum position of the Feature.  If the Feature
	  * consists of a fuzzy Feature, this may not be the maximum
	  * position.
	  */
	 public int getStop();

	 /**
	  * Returns the number of positions contained in the Feature.
	  */
	 public int length();

	 /**
	  * Returns the initial position of the Feature.  This should return 
	  * the same value as getStart().
	  */
	 public int getMin();

	 /**
	  * Returns the maximum position of the Feature.  If the Feature
	  * consists of a fuzzy Feature, this may not be equal to 'stop'.
	  */
	 public int getMax();

	 /** 
	  * Returns the underlying Sequence object.  
	  */
	 public Sequence getSource();

	 /** 
	  * Returns the underlying Sequence object's ID.  
	  */
	 public String getSourceID();

	 /** 
	  * Returns the underlying sequence data.
	  */
	 public String getData();

	 /** 
	  * Returns the Sequence data, with "\n" inserted every
	  * Sequence.FORMAT_WIDTH characters (usually 50 to 80 chars).
	  */
	 public String getDataFormatted();

	 /**
	  * Compares this object with the specified object for order.
	  * Returns a negative integer, zero, or a positive integer as this
	  * object is less than, equal to, or greater than the specified
	  * object.
	  * @XXX This is necessary for 'Comparable'.
	  */
	 public int compareTo(Object o);

	 /**
	  * This will return true if the features are equal and the sources
	  * are the same.  If can't cast argument as a valid feature, then
	  * throws a java.lang.ClassCastException.
	  */
	 public boolean equals(Object o);

	 /**
	  * Returns '-1' if this Feature exists after the integer 'pos',
	  * returns '0' if 'pos' is contained in this Feature, and '1' if
	  * 'pos' occurs after this Feature.
	  * @XXX This assumes 'pos' is positive within this Feature's
	  * Sequence boundaries.
	  * @XXX Not clear how to deal with Sequences in Tracks.
	  * @XXX For Tracks, this should test contains() for each
	  * Feature within the Track.
	  */
	 public int contains(int pos);

	 /**
	  * Returns 'true' if the Feature 'feature' exists in this Feature.
	  */
	 public boolean contains(Feature feature);

	 /**
	  * Returns 'true' if the Feature 'feature' has at least one
	  * position that overlaps positions in this Feature.
	  */
	 public boolean overlaps(Feature feature);

	 /**
	  * Returns the overlapping region between the two Features.  If no
	  * overlap, then null is returned.
	  */
	 public Feature overlap(Feature feature);

	 /**
	  * Inverts the positions, returning a new Feature object.  For
	  * example, if the Feature had a start position of 10 and a stop
	  * position of 20 on a Sequence that was 100 positions long, then
	  * flipping the Feature would result in a new Feature object with
	  * a start position of 80 and a stop position of 90.  
	  */
	 public Feature flip();

	 /** Only returns basic Feature information. */
	 public String toString();

	 /** Only returns Feature start/stop information. */
	 public String toStringMin();

	 /** Returns all Feature information, except the data. */
	 public String toStringFull();

} // Feature.java
