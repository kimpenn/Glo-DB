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
 * @(#)FeatureUtils.java
 */

package edu.upenn.gloDB;

import java.util.TreeSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * FeatureUtils.  Miscellaneous functions that act on Features and
 * Sets of Features.
 *
 * @author  Stephen Fisher
 * @version $Id: FeatureUtils.java,v 1.1.2.17 2007/03/01 21:17:32 fisher Exp $
 */

public class FeatureUtils {

    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /**
	  * Returns a new Feature containing the region in 'feature' that
	  * is bounded by minB/maxB.  Returns null if 'feature' doesn't
	  * overlap with minB/maxB.
	  * @XXX Doesn't deal with multiple Sequences.
	  */
	 public static Feature contained(Feature feature, int minB, int maxB) {
		  int minA = feature.getMin();
		  int maxA = feature.getMax();

		  // check for overlap
		  if ((minA > maxB) || (maxA < minB)) return null;

		  int minOut = minA;
		  int maxOut = maxA;
 		  if (minA < minB) minOut = minB;
		  if (maxA > maxB) maxOut = maxB;

		  return new ExactFeature(minOut, maxOut, feature.getSource());
	 }

	 /**
	  * Returns '-1' if 'feature' exists after the integer 'pos',
	  * returns '0' if 'pos' is contained in 'feature', and '1' if
	  * 'pos' occurs after 'feature'.
	  * @XXX This assumes 'pos' is positive within this Feature's
	  * Sequence boundaries.
	  */
	 public static int contains(Feature feature, int pos) {
		  if (feature.getMin() > pos) { 
				return -1;
		  } else if (feature.getMax() < pos) { 
				return 1;
		  } else {
				return 0;
		  }
	 }

	 /**
	  * Returns 'true' if the second Feature ('featureB') is contained
	  * in the first Feature ('featureA').  If the Features are on
	  * different Sources, then returns 'false'.
	  */
	 public static boolean contains(Feature featureA, Feature featureB) { 
		  if (featureA.getSource() != featureB.getSource()) return false;

		  if ((featureA.getMin() > featureB.getMin()) 
				|| (featureA.getMax() < featureB.getMax())) {
				return false;
		  } else {
				return true;
		  }
	 }

	 /**
	  * Returns 'true' if the Feature and Track overlap.  If the
	  * Features are on different Sources, then returns 'false'.
	  *
	  * f1:         t-------u
	  * aa|------------------------------|bb
	  *   |012345678901234567890123456789|
	  * f2:  a-b   c-d e-f g---h i-j
	  *
	  * f2.max <  f1.min : continue
	  * f2.min <= f1.max : true else false
	  */
	 public static boolean overlaps(Feature featureA, Track track) { 
		  // make sure the Features are on the same Sequence
		  TreeSet featuresB = track.featuresBySource(featureA.getSource());
		  if (featuresB == null) return false;

		  int minA = featureA.getMin();
		  int maxA = featureA.getMax();

		  for (Iterator i = featuresB.iterator(); i.hasNext();) {
				Feature featureB = (Feature) i.next();

				if (featureB.getMax() < minA) {
					 // no overlap
					 continue;
				} else {
					 if (featureB.getMin() <= maxA) {
						  return true;
					 } else {
						  return false;
					 }
				}
		  }

		  // if we got this far then we didn't find an overlap
		  return false;
	 }

	 /**
	  * Returns 'true' if the Features have overlapping positions.  If
	  * the Features are on different Sources, then returns 'false'.
	  */
	 public static boolean overlaps(Feature featureA, Feature featureB) { 
		  if (featureA.getSource() != featureB.getSource()) return false;

		  if ((featureA.getMin() <= featureB.getMax()) && 
				(featureB.getMin() <= featureA.getMax())) {
				// A.min <= B.max and B.min <= A.max
				return true;
		  } else {
				// no overlap
				return false;
		  }
	 }

	 /**
	  * Returns the overlapping region between the two Features.  If no
	  * overlap, then null is returned.
	  * @XXX should test for same source 
	  */
	 public static Feature overlap(Feature featureA, Feature featureB) { 
		  // this will also make sure they are on the same Sequence
		  if (! overlaps(featureA, featureB)) return null;

		  int aMin = featureA.getMin();
		  int aMax = featureA.getMax();
		  int bMin = featureB.getMin();
		  int bMax = featureB.getMax();

		  // we know the Features overlap so we don't need to test for
		  // that here
		  if (aMin >= bMin) {
				if (aMax <= bMax) {
					 return featureA;
				} else {
					 return new ExactFeature(aMin, bMax, featureA.getSource());
				}
		  } else {  // bMin > aMin
				if (bMax <= aMax) {
					 return featureB;
				} else {
					 return new ExactFeature(bMin, aMax, featureA.getSource());
				}
		  }
	 }

	 /**
	  * Compares two Features for order.  Returns '-1', '0', or '1' if
	  * the first Feature ('featureA') is less than, equal to, or
	  * greater than the second Feature ('featureB').  If the Features
	  * have different Source sequences, then they will be sorted by
	  * Source ID.
	  */
	 public static int compareFeatures(Feature featureA, Feature featureB) {
		  //		  int source = featureA.getSourceID().compareToIgnoreCase(featureB.getSourceID());
		  int source = featureA.getSourceID().compareTo(featureB.getSourceID());
		  
		  if (source == 0) { // same source
				// who ever has min is less.
				if (featureA.getMin() < featureB.getMin()) {
					 return -1;
				} else if (featureA.getMin() > featureB.getMin()) {
					 return 1;
				}

				// min are equal.
				if (featureA.getMax() < featureB.getMax()) {
					 return -1;
				} else if (featureA.getMax() > featureB.getMax()) {
					 return 1;
				}

				if (GloDBUtils.ignoreAttributes()) {
					 // don't use attributes to compare Features, so at
					 // this point the Features are the same
					 return 0;
				} else {
					 // min and max are equal, so return the comparison of
					 // the hashCodes for each of the attributes.  If we
					 // don't then when 2 Features overlap only one will be
					 // included in the Track.
					 Integer hashA = new Integer((featureA.getAttributes()).hashCode());
					 Integer hashB = new Integer((featureB.getAttributes()).hashCode());
					 return hashA.compareTo(hashB);
				}
		  } else {
				// different sources so sort by source
				return source;
		  }
	 }

	 /**
	  * Returns 'true' if the set of Features does not contain gaps
	  * between Features.  This assumes that the Features all occur on
	  * the same sequence (ie they refer to the same Sequence object).
	  */
	 public static boolean isContiguous(TreeSet features) { 
		  // return false if no Features
		  if ((features == null) || (features.size() == 0)) return false;

		  Iterator i = features.iterator();
		  Feature feature = (Feature) i.next();
		  int end = feature.getMax();

		  while (i.hasNext()) {
				feature = (Feature) i.next();

				// if find a gap then exit, returning false
				if (feature.getMin() > (end + 1)) return false;

				// still overlapping, so increase the end position if
				// necessary
				if (feature.getMax() > end) end = feature.getMax();
		  }
				
		  // if made it this far, then no gaps in features
		  return true;
	 }

	 /**
	  * This will merge all Features in the Track that are within
	  * maxSpace of each other.  New Features will be created to span
	  * the entire cluster.  Threshold sets the minimum number of
	  * Features necessary to be considered a cluster and thus included
	  * in the output set.  A new Track will be returned containing the
	  * clusters.
	  * @deprecated replaced with cluster.py
	  */
	 public static TreeSet cluster(Track track, int maxSpace, int threshold) { 
		  TreeSet clusters = new TreeSet();

		  // if Track is empty then there will be no matches so just
		  // return an empty set
		  if (track.numFeatures() == 0) return clusters;

		  // make sure maxSpace is legal
		  if (maxSpace < 0) {
				GloDBUtils.printError("Illegal \"maxSpace\" argument in FeatureUtils.cluster.");
				return clusters;
		  }

		  // make sure maxSpace is legal
		  if (threshold < 0) {
				GloDBUtils.printError("Illegal \"threshold\" argument in FeatureUtils.cluster.");
				return clusters;
		  }

		  // Source Sets for Track
		  HashMap sources = track.getSources();

		  // test Features based on source
		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// get Features for this source
				TreeSet features = (TreeSet) sources.get(source);

				Iterator i = features.iterator();
				Feature fLast = (Feature) i.next();
				// max here refers to the range for the beginning of the
				// next Feature
				int max = fLast.getMax() + maxSpace;

				TreeSet group = new TreeSet();
				group.add(fLast);
				// stores the minimum position for the group
				int gMin = fLast.getMin();

				while (i.hasNext()) {
					 Feature fCurrent = (Feature) i.next();

					 if (fCurrent.getMin() <= max) {
						  // within cluster spacing
						  group.add(fCurrent);

						  if (fCurrent.getMax() > fLast.getMax()) {
								// new maximum
								fLast = fCurrent;
								max = fLast.getMax() + maxSpace;
						  }
					 } else {
						  if (group.size() >= threshold) {
								// enough Features in cluster to warrent
								// inclusion in output

								if (group.size() > 1) {
									 // more than one Feature in cluster to
									 // make new Feature to span the set of
									 // Features
									 clusters.add(new ExactFeature(gMin, fLast.getMax(), 
																			 ObjectHandles.getSequence(source)));
								} else {
									 // only one Feature in group so just add
									 // that Feature
									 clusters.add(group.first());
								}
						  }

						  fLast = fCurrent;
						  max = fLast.getMax() + maxSpace;

						  group.clear();
						  group.add(fLast);
						  gMin = fLast.getMin();
					 }
				}

				// add last group, if present
				if (group.size() >= threshold) {
					 // enough Features in cluster to warrent inclusion in
					 // output
					 if (group.size() > 1) {
						  // more than one Feature in cluster so make new
						  // Feature to span the set of Features
						  clusters.add(new ExactFeature(gMin, fLast.getMax(), 
																  ObjectHandles.getSequence(source)));
					 } else {
						  // only one Feature in group so just add that
						  // Feature
						  clusters.add(group.first());
					 }
				}
				
		  }
		  return clusters;
	 }

	 /**
	  * This will merge all overlapping Features in the track,
	  * creating new Feature objects as necessary.  If no Features in
	  * 'track', then this returns an empty TreeSet.
	  */
	 public static TreeSet mergeContiguous(Track track) { 
		  TreeSet newFeatures = new TreeSet();

		  // don't do anything if no Features
		  if (track.numFeatures() == 0) return newFeatures;

		  // step through the Features one Sequence at a time
		  for (Iterator s = track.getSourceSet().iterator(); s.hasNext();) {
				Sequence source = (Sequence) ObjectHandles.sequencePool.get(s.next());
				TreeSet features = track.featuresBySource(source);

				Iterator i = features.iterator();
				Feature prevFeature = (Feature) i.next();
				int max = prevFeature.getMax();
				// merge the attributes for all Features that are merged
				HashMap attribs = prevFeature.getAttributesMap();

				// loop through all Features on the current Sequence.
				// Since they are in a TreeSet, they are already sorted by
				// their minimum values.
				while (i.hasNext()) {
					 Feature feature = (Feature) i.next();

					 if ((max + 1) < feature.getMin()) { 
						  // we found a gap, so the current Feature 'feature'
						  // doesn't overlap the previous Feature(s).  So we
						  // need to add the previous Feature(s) and then
						  // start again using the current Feature.
						  if (prevFeature.getMax() == max) {
								// didn't find any overlapping Features, so
								// just add the previous Feature
								newFeatures.add(prevFeature);
						  } else {
								// need to create a new Feature that spans
								// the current overlapping Features
								Feature newFeature = new ExactFeature(prevFeature.getMin(), max, source);
								newFeature.setAttributes(attribs);
								newFeatures.add(newFeature);
						  }

						  prevFeature = feature;
						  max = feature.getMax();
						  attribs = feature.getAttributesMap();
					 } else {
						  // overlapping Features, so incremement 'max' if
						  // necessary and add the current Feature
						  // attributes
						  if (feature.getMax() > max) max = feature.getMax();
						  attribs.putAll(feature.getAttributesMap());
					 }
				}

				// need to add the final Feature
				if (prevFeature.getMax() == max) {
					 // didn't find any overlapping Features, so
					 // just add the previous Feature
					 newFeatures.add(prevFeature);
				} else {
					 // need to create a new Feature that spans
					 // the current overlapping Features
					 Feature newFeature = new ExactFeature(prevFeature.getMin(), max, source);
					 newFeature.setAttributes(attribs);
					 newFeatures.add(newFeature);
				}
		  }

		  return newFeatures;
	 }

} // FeatureUtils.java
