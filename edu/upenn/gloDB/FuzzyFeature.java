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
 * @(#)FuzzyFeature.java
 */

package edu.upenn.gloDB;

/**
 * These objects code Features that don't have definite boundaries.
 * It's not clear whether it's more advantagous to have separate class
 * definitions:
 *   FuzzyFeature - make abstract class
 *   BetweenFeature - same fields as below
 *   AfterFeature - no stop -> stop = sequence length; stopExtension = 0
 *   BeforeFeature - no start -> start = 0; startExtension = 0
 *
 * @author  Stephen Fisher
 * @version $Id: FuzzyFeature.java,v 1.1.2.9 2007/03/01 21:17:32 fisher Exp $
 */

public class FuzzyFeature extends AbstractFeature { 
	 /** The initial position defining this Feature. */
	 private int start;

	 /** The number of the positions defining the start position. */
	 private int startExt;

	 /** The last position defining this Feature. */
	 private int stop;

	 /** The number of the positions defining the stop position. */
	 private int stopExt;


	 /** 
	  * Create a new FuzzyFeature object and add it to the set of
	  * Feature objects.
	  */
	 public FuzzyFeature(int start, int startExt, int stop, int stopExt, Sequence source) { 
		  this(start, startExt, stop, stopExt, source, true);
	 }

	 /** 
	  * Create a new FuzzyFeature object and add the newly created
	  * FuzzyFeature object to the set of Feature objects if addToPool
	  * is true.
	  * @XXX This should probably be 'protected' instead of 'public'
	  * because all FuzzyFeatures should really be added to
	  * featurePool.
	  */
	 public FuzzyFeature(int start, int startExt, int stop, int stopExt, Sequence source, boolean addToPool) {
		  super(source);

		  // these aren't allowed to change, so this is the only place
		  // they should be set.
		  this.start = start;
		  this.startExt = startExt;
		  this.stop = stop;
		  this.stopExt = stopExt;

		  // add self to set of all Features
		  if (addToPool) ObjectHandles.addFeature(this);
	 }


    //--------------------------------------------------------------------------
    // Setters and Getters
   
    /** Returns the start position. */
    public int getStart() { return start; }
	 
    /** Returns the startExt position. */
    public int getStartExt() { return startExt; }
	 
    /** Returns the stop position. */
    public int getStop() { return stop; }
	 
    /** Returns the stopExt. */
    public int getStopExt() { return stopExt; }
	 
    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /** 
	  * Returns the Sequence data from start to stopExt.  Note that
	  * this includes the fuzzy boundaries.
	  */
	 public String getData() { 
		  return getSource().getDataBounded(start, stopExt); 
	 }

	 /** 
	  * Returns the Sequence data from start to stopExt, with "\n"
	  * inserted every 80 characters.
	  */
	 public String getDataFormatted() { 
		  return getSource().getDataBoundedFormatted(start, stopExt); 
	 }

	 /**
	  * Returns the maximum number of positions contained in the
	  * Feature (((stop + stopExt) - start) + 1).
	  */
	 public int length() { return (((stop + stopExt) - start) + 1); }

	 /**
	  * Returns the initial position of the Feature.
	  */
	 public int getMin() { return start; }

	 /**
	  * Returns the maximum position of the Feature.
	  */
	 public int getMax() { return stopExt; }

	 /**
	  * Returns the minimum number of positions contained in the
	  * Feature ((stop - start) + 1).
	  */
	 public int minLength() { return ((stop - start) + 1); }

	 /**
	  * This is the same as {@link #length() length()}.
	  */
	 public int maxLength() { return length(); }

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
	  * @XXX This assumes 'pos' is positive within this Feature's
	  * Sequence boundaries.
	  */
	 public int contains(int pos) {
		  return FeatureUtils.contains(this, pos);
	 }

	 /**
	  * Returns 'true' if the Feature 'feature' is contained in this
	  * Feature.
	  */
	 public boolean contains(Feature feature) { 
		  return FeatureUtils.contains(this, feature);
	 }

	 /**
	  * Returns 'true' if the feature 'featCk' has positions that
	  * overlap positions in this feature.
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
	  *
	  */
	 public int compareTo(Object o) throws ClassCastException {
		  // convert object to Feature
		  Feature featureTo;
		  try {
				featureTo = (Feature) o;
		  } catch (ClassCastException e) {
				throw new ClassCastException("FuzzyFeature.compareTo() requires an argument of type Feature.");
		  }
		  return FeatureUtils.compareFeatures(this, featureTo);
	 }

	 /**
	  * This will return true if the features are equal and the
	  * sources are the same.  If can't cast argument as a
	  * FuzzyFeature, then throws a java.lang.ClassCastException.
	  */
	 public boolean equals(Object o) throws ClassCastException {
		  // make sure we got an 'ExactFeature' object
		  if (GloDBUtils.getClassName(o) != "FuzzyFeature") return false;

		  // convert object to Feature
		  FuzzyFeature featureTo;
		  try {
				featureTo = (FuzzyFeature) o;
		  } catch (ClassCastException e) {
				throw new ClassCastException("FuzzyFeature.equals() requires an argument of type FuzzyFeature.");
		  }

		  // check of same source
		  if (source != featureTo.source) { return false; }

		  // check if start, stop, and extensions are all equal
		  if ((start == featureTo.getStart()) &&
				(startExt == featureTo.getStartExt()) &&
				(stop == featureTo.getStop()) &&
				(stopExt == featureTo.getStopExt())) {
				return true;
		  } else {
				return false;
		  }
	 }

	 /** Only returns basic Feature information. */
	 public String toString() {
		  String out = "Fuzzy Feature (" + source + "): ";
		  out += "(" + Integer.toString(start) + ", " + Integer.toString(startExt) + ")..";
		  out += "(" + Integer.toString(stop) + ", " + Integer.toString(stopExt) + ") \n";
		  return out;
	 }

	 /** Only returns start/stop position information. */
	 public String toStringMin() {
		  String out = "";
		  out += "(" + Integer.toString(start) + ", " + Integer.toString(startExt) + ")..";
		  out += "(" + Integer.toString(stop) + ", " + Integer.toString(stopExt) + ")";
		  return out;
	 }

	 /** Returns all Feature information. */
	 public String toStringFull() {
		  String out = "Fuzzy Feature:\n";
		  //		  String out = "Fuzzy Feature (" + source + "): ";
		  //		  out += "(" + Integer.toString(start) + ", " + Integer.toString(startExt) + ")..";
		  //		  out += "(" + Integer.toString(stop) + ", " + Integer.toString(stopExt) + ") \n";

		  out += "Start: " + Integer.toString(start) + "\n";
		  out += "StartExt: " + Integer.toString(startExt) + "\n";
		  out += "Stop: " + Integer.toString(stop) + "\n";
		  out += "StopExt: " + Integer.toString(stopExt) + "\n";

		  out += super.toString();  // get attributes and source info

		  return out;
	 }

} // FuzzyFeature.java
