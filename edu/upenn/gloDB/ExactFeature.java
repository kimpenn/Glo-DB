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
 * @(#)ExactFeature.java
 */

package edu.upenn.gloDB;

/**
 * These objects store exact positions bounding a Feature.  For point
 * Features just set start and stop equal.  At some point it might be
 * advantageous to create a PointFeature class which would only have
 * one position.
 *
 * @author  Stephen Fisher
 * @version $Id: ExactFeature.java,v 1.1.2.9 2005/01/07 19:56:59 fisher Exp $
 */

public class ExactFeature extends AbstractFeature {

	 /** The initial position defining this Feature. */
	 private int start;

	 /** The last position defining this Feature. */
	 private int stop;


	 /** 
	  * Create a new ExactFeature object and add it to the set of
	  * Feature objects.
	  */
	 public ExactFeature(int start, int stop, Sequence source) { 
		  this(start, stop, source, true);
	 }

	 /** 
	  * Create a new ExactFeature object and add the newly created
	  * ExactFeature object to the set of Feature objects if addToPool
	  * is true.
	  * @XXX This should probably be 'protected' instead of 'public'
	  * because all ExactFeatures should really be added to
	  * featurePool.
	  */
	 public ExactFeature(int start, int stop, Sequence source, boolean addToPool) {
		  super(source);

		  // start/stop aren't allowed to change, so this is the only
		  // place they should be set.
		  this.start = start;
		  this.stop = stop;

		  // add self to set of all Features
		  if (addToPool) ObjectHandles.addFeature(this);
	 }


    //--------------------------------------------------------------------------
    // Setters and Getters
   
    /** Returns the start position. */
    public int getStart() { return start; }
	 
    /** Returns the stop position. */
    public int getStop() { return stop; }
	 
    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /** 
	  * Returns the Sequence data from start to stop. 
	  */
	 public String getData() { return getSource().getDataBounded(start, stop); }

	 /** 
	  * Returns the Sequence data from start to stop, with "\n"
	  * inserted every Sequence.FORMAT_WIDTH characters (usually 50 to
	  * 80 chars).
	  */
	 public String getDataFormatted() { 
		  return getSource().getDataBoundedFormatted(start, stop); 
	 }

	 /**
	  * Returns the number of positions contained in the Feature ((stop
	  * - start) + 1).
	  */
	 public int length() { return ((stop - start) + 1); }

	 /**
	  * Returns the initial position of the Feature.
	  * @XXX This will return the same value as getStart().
	  */
	 public int getMin() { return start; }

	 /**
	  * Returns the maximum position of the Feature.
	  * @XXX This will return the same value as getStop().
	  */
	 public int getMax() { return stop; }

	 /**
	  * Inverts the positions, returning a new Feature object.  For
	  * example, if the Feature had a start position of 10 and a stop
	  * position of 20 on a contig that was 100 positions long, then
	  * flipping the Feature would result in a new Feature object with
	  * a start position of 80 and a stop position of 90.
	  * @XXX Not yet implemented.
	  */
	 public Feature flip() { return null; }

	 /**
	  * Returns '-1' if this Feature exists after the integer 'pos',
	  * returns '0' if 'pos' is contained in this Feature, and '1' if
	  * 'pos' occurs after this Feature.
	  * @XXX This assumes 'pos' is positive and within this Feature's
	  * Sequence boundaries.
	  */
	 public int contains(int pos) {
		  return FeatureUtils.contains(this, pos);
	 }

	 /**
	  * Returns 'true' if the Feature 'feature' exists within this
	  * Feature.
	  */
	 public boolean contains(Feature feature) { 
		  return FeatureUtils.contains(this, feature);
	 }

	 /**
	  * Returns 'true' if the Feature 'feature' has a position that
	  * overlaps this Feature.
	  */
	 public boolean overlaps(Feature feature) { 
		  return FeatureUtils.overlaps(this, feature);
	 }

	 /**
	  * Returns the overlapping region between the two Features.  If no
	  * overlap, then null is returned.
	  */
	 public Feature overlap(Feature feature) { 
		  return FeatureUtils.overlap(this, feature);
	 }

	 /**
	  * Compares this object with the specified object for order.
	  * Returns a negative integer, zero, or a positive integer as this
	  * object is less than, equal to, or greater than the specified
	  * object.  If can't cast argument as an Feature, then throws a
	  * java.lang.ClassCastException.  If different sources, then sorts
	  * on the source ID.
	  */
	 public int compareTo(Object o) throws ClassCastException {
		  // convert object to Feature
		  Feature featureTo;
		  try {
				featureTo = (Feature) o;
		  } catch (ClassCastException e) {
				throw new ClassCastException("ExactFeature.compareTo() requires an argument of type Feature.");
		  }
		  return FeatureUtils.compareFeatures(this, featureTo);
	 }

	 /**
	  * This will return true if the features are equal and the
	  * sources are the same.  If can't cast argument as an
	  * ExactFeature, then throws a java.lang.ClassCastException.
	  */
	 public boolean equals(Object o) throws ClassCastException {
		  // make sure we got an 'ExactFeature' object
		  if (GloDBUtils.getClassName(o) != "ExactFeature") return false;

		  // convert object to Feature
		  ExactFeature featureTo;
		  try {
				featureTo = (ExactFeature) o;
		  } catch (ClassCastException e) {
				throw new ClassCastException("ExactFeature.equals() requires an argument of type ExactFeature.");
		  }

		  // check of same source
		  if (source != featureTo.source) return false;

		  // check if start and stop are equal
		  if ((start == featureTo.getStart()) && (stop == featureTo.getStop())) {
				return true;
		  } else {
				return false;
		  }
	 }

	 /** Only basic Feature information. */
	 public String toString() {
		  String out = "Exact Feature (" + source + "): ";
		  out += "(" + Integer.toString(start) + ".." + Integer.toString(stop) + ") \n";
		  return out;
	 }

	 /** Only returns start/stop position information. */
	 public String toStringMin() {
		  return "(" + Integer.toString(start) + ".." + Integer.toString(stop) + ")";
	 }

	 /** Returns all Feature information. */
	 public String toStringFull() {
		  String out = "Exact Feature:\n";
		  //		  String out = "Exact Feature (" + source + "): ";
		  //		  out += "(" + Integer.toString(start) + ".." + Integer.toString(stop) + ") \n";

		  out += "Start: " + Integer.toString(start) + "\n";
		  out += "Stop: " + Integer.toString(stop) + "\n";
		 
		  out += super.toString();  // get attributes and source info

		  return out;
	 }
	
} // ExactFeature.java
