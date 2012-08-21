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
 * @(#)Operator.java
 */

package edu.upenn.gloDB.parser;

import edu.upenn.gloDB.*;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Methods for processing the different types of operators.
 *
 * <pre>
 * T1:  r-s    t-------u v---w  x-y      
 *  aa|------------------------------|bb 
 *    |012345678901234567890123456789|   
 * T2:  a-b   c-d e-f g---h i-j          
 * </pre>
 *
 * The type of Operator:
 * <ul>
 * <li>  -1 = null : (no preceeding operator - first track in a group).
 * <li>  0 = POS : all contiguous features in T1 and T2, appropriately spaced
 *       <ul> T1 POS{5} T2:     [(t,u), (i,j)] </ul>
 *       <ul> T2 POS{5} T1:     [(a,b), (t,u), (e,f), (v,w)] </ul>
 *       <ul> T1 POS{-5} T2:    [(t,u), (e,f)] </ul>
 *       <ul> T1 POS{3,6} T2:   [(r,s), (c,d), (t,u), (i,j)] </ul>
 *       <ul> T1 POS{-5,-1} T2: [(r,s), (t,u), (e,f), (g,h), (v,w), (i,j)] </ul>
 * <li>  1 = AND : all features in T1 which overlap features in T2.
 *       <ul> T1 AND T2: [(r,s), (t,u), (v,w), (c,d), (e,f), (g,h), (i,j)] </ul>
 * <li>  2 = OR : all features in T1 and T2.
 *       <ul> T1 OR T2: [(r,s), (t,u), (v,w), (x,y), (c,d), (e,f), (g,h), (i,j)] </ul>
 * <li>  3 = MINUS : all features in T1 that don't overlap features in T2.
 *       <ul> T1 MINUS T2: [(x,y)] </ul>
 *       <ul> T2 MINUS T1: [] </ul>
 * <li>  4 = sAND : all features in T1 which exactly overlap features in T2.
 *       <ul> T1 sAND T2: [(r,s)] </ul>
 * <li>  5 = sMINUS : all features in T1 that don't exactly overlap features in T2.
 *       <ul> T1 sMINUS T2: [(t,u), (v,w), (x,y)] </ul>
 *       <ul> T2 sMINUS T1: [(c,d), (e,f), (g,h), (i,j)] </ul>
 * <li>  10 = . (bPOS) : all contiguous features in T1 and T2, appropriately spaced
 *       <ul> T1 .{0} T2: [] </ul>
 *       <ul> T1 .{5} T2: [(t,u), (i,j)] </ul>
 *       <ul> T2 .{5} T1: [(a,b), (t,u), (e,f), (v,w)] </ul>
 *       <ul> T1 .{-5} T2: [(t,u), (e,f)] </ul>
 * <li>  11 = && (bAND) : all positions in T1 that overlap with positions in T2.
 *       <ul> T1 && T2: [(r,s), (t,d), (e,f), (g,u), (v,h), (i,w)] </ul>
 * <li>  12 = || (bOR) : all positions in T1 and T2.
 *       <ul> T1 || T2: [(r,s), (c,j), (x,y)] </ul>
 * <li>  13 = - (bMINUS) : the asymetrical difference between T1 and T2.
 *       <ul> T1 - T2: [(d,e), (f,g), (h,i), (x,y)] </ul>
 *       <ul> T2 - T1: [(c,t), (u,v), (w,j)] </ul>
 * </ul>
 *
 * @author  Stephen Fisher
 * @version $Id: Operator.java,v 1.1.2.22 2007/03/01 21:17:33 fisher Exp $
 */

public class Operator { 

    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /** Return the representative String for a type value. */
	 static String getType(int type) {
		  switch (type) {
				case -1: return "null";
				case 0:  return "POS";
				case 1:  return "AND";
				case 2:  return "OR";
				case 3:  return "MINUS";
				case 4:  return "sAND";
				case 5:  return "sMINUS";
					 //				case 10: return ".";
				case 11: return "&&";
				case 12: return "||";
				case 13: return "-";
		  }
		  return "";
	 }

	 /**
	  * The first argument 'left' is the set of Features on the left
	  * hand side of the operation, the second argument 'operation'
	  * contains both the operation type and the set of Features on
	  * the right hand side of the operation.
	  */
	 static Track processOperation(Track left, Operation operation) {
		  switch(operation.getType()) {
		  case 0:   // POS : all contiguous F in T1 and T2, appropriately spaced
				return fxn_POS(left, operation);
		  case 1:   // AND : all features in T1 which overlap features in T2.
				return fxn_AND(left, operation);
		  case 2:   // OR : all features in T1 and T2. 
				return fxn_OR(left, operation);
		  case 3:   // MINUS : all features in T1 that don't overlap features in T2.
				return fxn_MINUS(left, operation);
		  case 4:   // sAND : all features in T1 which exactly overlap features in T2.
				return fxn_sAND(left, operation);
		  case 5:   // sMINUS : all features in T1 that don't exactly overlap features in T2.
				return fxn_sMINUS(left, operation);
				//		  case 10:  // . (bPOS) : all contiguous F in T1 and T2, appropriately spaced
				//				return fxn_bPOS(left, operation);
		  case 11:  // && (bAND) : all positions in T1 that overlap with positions in T2.
				return fxn_bAND(left, operation);
		  case 12:  // || (bOR) : all positions in T1 and T2.
				return fxn_bOR(left, operation);
		  case 13:  // - (bMINUS) : the asymetrical difference between T1 and T2.
				return fxn_bMINUS(left, operation);
		  }

		  // if we got here, then we didn't have an appropriate 'type'.
		  return null;
	 }

	 /** POS : all contiguous F in T1 and T2, appropriately spaced 
	  *
	  * For each T2 need to loop through all T1 (on same seq), until
	  * past range.  Loop through T2 and if find match, then add T1 and
	  * T2, don't worry about repeatedly adding T1's but no need to
	  * ever test a T2 again once added -- only ever have to go through
	  * T2 once.
	  * @XXX can we make this more efficient by looping from end?  Use
	  * different strategy for - and +?  Consider that the Sets are
	  * sorted by their min values which means we can assume the T2's
	  * are incremental but we can't assume this about the T1's since
	  * we are concerned with the T1 max values but T1 is sorted by min
	  * values.
	  */
	 public static Track fxn_POS(Track left, Operation operation) {
		  Track out = new Track(false);
		  
		  // if either Track is empty then there will be no matches so
		  // just return an empty Track
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return out;
		  }

		  // Source Sets for Tracks
		  HashMap sourcesA = left.getSources();
		  HashMap sourcesB = operation.track.getSources();

		  GloDBUtils.printMsg("Working:...", GloDBUtils.FEEDBACK, false);
		  int cnt = 0;

		  // step through each Sequence from the second Set and if there
		  // are Features from the first Set that match the POS
		  // requirements, then add them Features.
		  for (Iterator s = sourcesB.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// if sourcesA doesn't include 'source' then try next
				// source from sourcesB
				if (! sourcesA.containsKey(source)) continue;
					 
				// get Features for this source
				TreeSet featuresA = (TreeSet) sourcesA.get(source);
				TreeSet featuresB = (TreeSet) sourcesB.get(source);

				if ((featuresA.size() == 0) || (featuresB.size() == 0)) continue;

				int interval = (featuresA.size() + featuresB.size()) / 50;
				if (interval == 0) interval = 1;

				// create TreeSet that stores Features in descending order
				TreeSet featuresADesc = Track.sortByMax(featuresA);

				Iterator iA = featuresADesc.iterator();
				Iterator iB = featuresB.iterator();
				
				Feature featureA = (Feature) iA.next();
				Feature featureB = (Feature) iB.next();

				int minA = featureA.getMax() + operation.minPos;
				int maxA = featureA.getMax() + operation.maxPos;
				int minB = featureB.getMin();
				while (true) {
					 //					 System.out.print("loopA: " + minA + " " + maxA + " " + featureA);
					 //					 System.out.print("loopB: " + featureB);

					 if (maxA < minB) { // increment A
						  if (iA.hasNext()) {
								featureA = (Feature) iA.next();
								minA = featureA.getMax() + operation.minPos;
								maxA = featureA.getMax() + operation.maxPos;
								//								System.out.print("incA: " + minA + " " + maxA + " " + featureA);

								if ((cnt++ % interval) == 0) GloDBUtils.printMsg(".", GloDBUtils.FEEDBACK, false);
						  } else {
								break;
						  }
					 } else if (minA > minB) {
						  // featureB less than featureA, so increment featureB
						  if (iB.hasNext()) { 
								featureB = (Feature) iB.next(); 
								//								System.out.print("incB: " + featureB);
								minB = featureB.getMin();
						  } else { 
								// have run out of Features in B
								break;
						  }
					 } else {  // found a match
						  //						  System.out.println("match A to B");
						  out.addFeature(featureA);
						  out.addFeature(featureB);

						  // increment A until no more overlapping Features
						  // and store the Features in a temporary set so
						  // that we can compare them to B.
						  TreeSet tmpASet = new TreeSet();
						  Feature newA = featureA;
						  while (iA.hasNext()) {
								newA = (Feature) iA.next(); 
								int newMinA = newA.getMax() + operation.minPos;
								int newMaxA = newA.getMax() + operation.maxPos;
								//								System.out.print("newA: " + newMinA + " " + newMaxA + " " + newA);
								if ((newMinA <= minB) && (minB <= newMaxA)) {
									 //									 System.out.println("match newA to B");
									 out.addFeature(newA);
									 tmpASet.add(newA);
								} else {
									 break;
								}
						  }
						  
						  // increment B until not overlapping with current
						  // A Feature, then test if it overlaps with any
						  // of the A Features just added.
						  TreeSet tmpBSet = new TreeSet();
						  Feature newB = featureB;
						  while (iB.hasNext()) {
								newB = (Feature) iB.next(); 
								//								System.out.print("newB: " + newB);
								int newMinB = newB.getMin();
								if ((minA <= newMinB) && (newMinB <= maxA)) {
									 //									 System.out.println("match A to newB");
									 out.addFeature(newB);
									 tmpBSet.add(newB); // need to test these against newA
								} else {
									 for (Iterator tmpAIt = tmpASet.iterator(); tmpAIt.hasNext();) {
										  Feature tmpA = (Feature) tmpAIt.next();
										  int newMinA = tmpA.getMax() + operation.minPos;
										  int newMaxA = tmpA.getMax() + operation.maxPos;
										  //										  System.out.print("compA: " + newMinA + " " + newMaxA + " " + newA);
										  if ((newMinA <= newMinB) && (newMinB <= newMaxA)) {
												//												System.out.println("match newA(m) to newB(u)");
												out.addFeature(newB);
										  }
									 }

									 for (Iterator tmpBIt = tmpBSet.iterator(); tmpBIt.hasNext();) {
										  Feature tmpB = (Feature) tmpBIt.next();
										  int newMinA = newA.getMax() + operation.minPos;
										  int newMaxA = newA.getMax() + operation.maxPos;
										  //										  System.out.print("compB: " + newB);
										  if ((newMinA <= tmpB.getMin()) && (tmpB.getMin() <= newMaxA)) {
												//												System.out.println("match newA(u) to newB(m)");
												out.addFeature(newA);
										  }
									 }
									 break;
								}
						  }

						  // no more overlap, so test if either Track ran
						  // out of Features then we're done
						  if ((featureA == newA) || (featureB == newB)) break;
							  
						  // more Features in both Tracks so keep going
						  featureA = newA;
						  minA = featureA.getMax() + operation.minPos;
						  maxA = featureA.getMax() + operation.maxPos;
						  featureB = newB;
						  minB = featureB.getMin();
					 }
				}
				GloDBUtils.printMsg("", GloDBUtils.FEEDBACK);
	 
		  }
		  return out;
	 }

	 /** AND : all F in T1 which also exists in T2. */
	 public static Track fxn_AND(Track left, Operation operation) {
		  Track out = new Track(false);

		  // if either Track is empty then there will be no matches so
		  // just return an empty Track
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return out;
		  }

		  HashMap smaller;
		  HashMap larger;
		  if (left.numSources() < operation.track.numSources()) {
				// Source sets for Tracks
				smaller = left.getSources();
				larger = operation.track.getSources();
		  } else {
				smaller = operation.track.getSources();
				larger = left.getSources();
		  }
		  
		  // step through each Sequence from the second set and if there
		  // are Features from the first set that overlap
		  for (Iterator s = smaller.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// if larger doesn't include 'source' then try next source
				// from smaller
				if (! larger.containsKey(source)) continue;
					 
				// get Features for this source
				TreeSet featuresSm = (TreeSet) smaller.get(source);
				TreeSet featuresLg = (TreeSet) larger.get(source);

				Feature featureSm, featureLg;
				Iterator iSm = featuresSm.iterator();
				Iterator iLg = featuresLg.iterator();

				// get initial Features
				// @XXX do we need to test for empty sets?
				if (iSm.hasNext() && iLg.hasNext()) { 
					 featureSm = (Feature) iSm.next(); 
					 featureLg = (Feature) iLg.next();
				} else { 
					 continue;
				}
				int minSm = featureSm.getMin();
				int maxSm = featureSm.getMax();
				int minLg = featureLg.getMin();
				int maxLg = featureLg.getMax();
				
				while (true) {
					 ///					 System.out.println("sm: (" + minSm + ", " + maxSm + ") :: lg: (" + minLg + ", " + maxLg + ")");

					 if (minLg > maxSm) {
						  ///						  System.out.println("sm less than lg");

						  // featureSm less than featureLg, so increment Sm
						  if (iSm.hasNext()) { 
								featureSm = (Feature) iSm.next(); 
								minSm = featureSm.getMin();
								maxSm = featureSm.getMax();
						  } else { 
								///								System.out.println("no more sm");

								// have run out of Features in Sm
								if (iLg.hasNext()) {
									 // more Features in Lg so keep going
									 featureLg = (Feature) iLg.next(); 
									 minLg = featureLg.getMin();
									 maxLg = featureLg.getMax();
								} else {
									 // no more Features in Lg, so stop
									 break;
								}
						  }
					 } else if (minSm > maxLg) {
						  ///						  System.out.println("lg less than sm");

						  // featureLg less than featureSm, so increment featureLg
						  if (iLg.hasNext()) { 
								featureLg = (Feature) iLg.next(); 
								minLg = featureLg.getMin();
								maxLg = featureLg.getMax();
						  } else { 
								break;
						  }
					 } else {
						  ///						  System.out.print("adding (overlap) sm: " + featureSm);
						  ///						  System.out.print("adding (overlap) lg: " + featureLg);

						  // Features overlap so add the Features
						  out.addFeature(featureSm);
						  out.addFeature(featureLg);

						  // test if featureSm overlaps any remaining
						  // features in featuresLg before incrementing
						  SortedSet lgSet = featuresLg.tailSet(featureLg);
						  Iterator i = lgSet.iterator();
						  // the first element is "featureLg" which was
						  // already added, so just step past this element
						  i.next(); 
						  while (i.hasNext()) {
								Feature tmpLg = (Feature) i.next();
								if (featureSm.overlaps(tmpLg)) {
									 ///						  System.out.print("matched (adding) lg: " + tmpLg);

									 out.addFeature(tmpLg);
								} else if (tmpLg.getMin() > minSm) {
									 ///						  System.out.print("not matched lg: " + tmpLg);

									 // can't just stop when they don't
									 // overlap, need to make sure that we've
									 // gone far enough because one feature
									 // might be tiny and not overlap while the
									 // next might be huge and overlap
									 break;
								}
						  }

						  // test if featureLg overlaps any remaining
						  // features in featuresSm before incrementing
						  SortedSet smSet = featuresSm.tailSet(featureSm);
						  i = smSet.iterator();
						  // the first element is "featureSm" which was
						  // already added, so just step past this element
						  i.next(); 
						  while (i.hasNext()) {
								Feature tmpSm = (Feature) i.next();
								if (featureLg.overlaps(tmpSm)) {
									 ///						  System.out.print("matched (adding) sm: " + tmpSm);

									 out.addFeature(tmpSm);
								} else if (tmpSm.getMin() > minLg) {
									 ///						  System.out.print("not matched sm: " + tmpSm);

									 // can't just stop when they don't
									 // overlap, need to make sure that we've
									 // gone far enough because one feature
									 // might be tiny and not overlap while the
									 // next might be huge and overlap
									 break;
								}
						  }

						  // no more overlap, so test if either Track ran
						  // out of Features then we're done
						  if ((! iSm.hasNext()) || (! iLg.hasNext())) break;
							  
						  // more Features in both Tracks so keep going
						  featureSm = (Feature) iSm.next();
						  minSm = featureSm.getMin();
						  maxSm = featureSm.getMax();
						  featureLg = (Feature) iLg.next();
						  minLg = featureLg.getMin();
						  maxLg = featureLg.getMax();
					 }
				}
		  }
		  return out;
	 }

	 /** OR : all F in T1 and T2. */
	 public static Track fxn_OR(Track left, Operation operation) {
		  if (left.numFeatures() < operation.numFeatures()) {
				Track out = (Track) operation.track.cloneTrack(false);
				out.addFeatures(left.getFeatures());
				return out;
		  } else {
				Track out = (Track) left.cloneTrack(false);
				out.addFeatures(operation.track.getFeatures());
				return out;
		  }
	 }

	 /** 
	  * MINUS : all F in T1 that don't overlap with F in T2. 
	  */
	 public static Track fxn_MINUS(Track left, Operation operation) {
		  // if 'left' is empty then there will be no matches so just
		  // return an empty Track (ie 'left') and if 'operation' is empty
		  // then the entire 'left' will match so again just return
		  // 'left'.
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return (Track) left.cloneTrack(false);
		  }

		  Track out = new Track(false);

		  // Source Sets for Tracks
		  HashMap sourcesA = left.getSources();
		  HashMap sourcesB = operation.track.getSources();

		  // step through each Sequence
		  for (Iterator s = sourcesA.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// get Features for this source
				TreeSet featuresA = (TreeSet) sourcesA.get(source);

				// if sourcesB doesn't include 'source' then add all of
				// the Features on the current Sequence and continue.
				if (! sourcesB.containsKey(source)) {
					 out.addFeatures(featuresA);
					 continue;
				}
					 
				// get Features for this source
				TreeSet featuresB = (TreeSet) sourcesB.get(source);

				Feature featureA, featureB;
				Iterator iA = featuresA.iterator();
				Iterator iB = featuresB.iterator();

				// get initial Features
				// @XXX do we need to test for empty sets?
				if (iA.hasNext() && iB.hasNext()) { 
					 featureA = (Feature) iA.next(); 
					 featureB = (Feature) iB.next();
				} else { 
					 continue;
				}
				int minA = featureA.getMin();
				int maxA = featureA.getMax();
				int minB = featureB.getMin();
				int maxB = featureB.getMax();
				
				while (true) {
					 if (minB > maxA) {
						  // featureA is less than featureB, so add featureA and
						  // increment A
						  out.addFeature(featureA);
						  if (iA.hasNext()) { 
								featureA = (Feature) iA.next(); 
								minA = featureA.getMin();
								maxA = featureA.getMax();
						  } else { 
								// have run out of Tracks in A, but not sure if
								// there are more in B that might overlap with the
								// current A.  So increment B and continue, until
								// B is no longer less than A or run out of B.
								if (iB.hasNext()) {
									 featureB = (Feature) iB.next(); 
									 minB = featureB.getMin();
									 maxB = featureB.getMax();
								} else {
									 // have run out of Tracks in B
									 break;  // loop to next source
								}
						  }
					 } else if (minA > maxB) {
						  // featureB is less than featureA, so increment featureB
						  if (iB.hasNext()) { 
								featureB = (Feature) iB.next(); 
								minB = featureB.getMin();
								maxB = featureB.getMax();
						  } else { 
								// have run out of Tracks in B, so need to add
								// current and remaining Tracks in A.
								out.addFeature(featureA);
								while (iA.hasNext()) { 
									 out.addFeature((Feature) iA.next());
								}
								break; // loop to next source
						  }
					 } else {
						  // Tracks overlap so increment A without adding A
						  if (iA.hasNext()) { 
								featureA = (Feature) iA.next(); 
								minA = featureA.getMin();
								maxA = featureA.getMax();
						  } else { 
								// have run out of Tracks in A
								break;  // loop to next source
						  }
					 }
				}
		  }
		  return out;
	 }

	 /** 
	  * sAND : all features in T1 which exactly overlap features in T2.
	  */
	 public static Track fxn_sAND(Track left, Operation operation) {
		  Track out = new Track(false);

		  // if either Track is empty then there will be no matches so
		  // just return an empty Track
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return out;
		  }

		  HashMap smaller;
		  HashMap larger;
		  if (left.numFeatures() < operation.numFeatures()) {
				// Source Sets for Tracks
				smaller = left.getSources();
				larger = operation.track.getSources();
		  } else {
				smaller = operation.track.getSources();
				larger = left.getSources();
		  }
		  
		  // step through each Sequence from the second set and test if
		  // there are Features from the first set that are equal
		  for (Iterator s = smaller.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// if larger doesn't include 'source' then try next source
				// from smaller
				if (! larger.containsKey(source)) continue;
					 
				// get Features for this source
				TreeSet featuresSm = (TreeSet) smaller.get(source);
				TreeSet featuresLg = (TreeSet) larger.get(source);

				// loop over the smaller Set
				for (Iterator i = featuresSm.iterator(); i.hasNext();) {
					 Feature feature = (Feature) i.next();
					 if (featuresLg.contains(feature)) {
						  out.addFeature(feature);
					 }
				}
		  }
		  return out;
	 }

	 /** 
	  * sMINUS : all features in T1 that don't exactly overlap features in T2.
	  */
	 public static Track fxn_sMINUS(Track left, Operation operation) {
		  // if 'left' is empty then there will be no matches so just
		  // return an empty Track (ie 'left') and if 'operation' is empty
		  // then the entire 'left' will match so again just return
		  // 'left'.
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return (Track) left.cloneTrack(false);
		  }

		  Track out = new Track(false);

		  // Source Sets for Tracks
		  HashMap sourcesA = left.getSources();
		  HashMap sourcesB = operation.track.getSources();

		  // step through each Sequence
		  for (Iterator s = sourcesA.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// get Features for this source
				TreeSet featuresA = (TreeSet) sourcesA.get(source);

				// if sourcesB doesn't include 'source' then add all of
				// the Features on the current Sequence and continue.
				if (! sourcesB.containsKey(source)) {
					 out.addFeatures(featuresA);
				} else {
					 // get Features from B for this source
					 TreeSet featuresB = (TreeSet) sourcesB.get(source);

					 // loop over Features in A and see if they don't exist
					 // in B, then add them to the output
					 for (Iterator i = featuresA.iterator(); i.hasNext();) {
						  Feature feature = (Feature) i.next();
						  if (! featuresB.contains(feature)) {
								out.addFeature(feature);
						  }
					 }
				}
		  }

		  return out;
	 }

	 /** OR : all F in T1 and T2. */
	 public static Track fxn_bOR(Track left, Operation operation) {
		  // create a Track that contains all Features, so we can use
		  // the Track's mergeContiguous() function to do the work for
		  // us.
		  // @XXX this is probably not very efficient.
		  Track track = fxn_OR(left, operation);
		  track.mergeContiguous();
		  return track;
	 }

	 /** && (bAND) : all positions in T1 that overlap with positions in T2.
	  * @XXX This will return only overlapping regions between 2
	  * Features.
	  * @XXX This assumes that mergeContiguous() has already been run
	  * on each set of Features; that is, neither set contains
	  * contiguous Features.
	  */
	 public static Track fxn_bAND(Track left, Operation operation) {
		  // output Track
		  Track out = new Track(false);

		  // if either Track is empty then there will be no matches so
		  // just return an empty Track
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return out;
		  }

		  // first do a Track.mergeContiguous() for each TreeSet but
		  // this isn't probably very efficient.
		  left.mergeContiguous();
		  HashMap sourcesA = left.getSources();

		  //		  operation.track.mergeContiguous();
		  //		  HashMap sourcesB = operation.track.getSources();
		  Track trackB = operation.track.cloneMerged();
		  HashMap sourcesB = trackB.getSources();

		  // step through each Sequence from the first Set and if there
		  // are Features from the second Set on the same Sequence,
		  // then check for overlaps.  
		  for (Iterator s = sourcesA.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();
				Sequence sourceObj = (Sequence) ObjectHandles.sequencePool.get(source);

				// if sourcesB doesn't include 'sourcesA' then try next
				// source from sourcesA
				if (! sourcesB.containsKey(source)) continue;
					 
				// get Features for this source
				TreeSet featuresA = (TreeSet) sourcesA.get(source);
				TreeSet featuresB = (TreeSet) sourcesB.get(source);

				Iterator iA = featuresA.iterator();
				Iterator iB = featuresB.iterator();

				Feature featureA;
				Feature featureB;

				// get initial Features
				if (iA.hasNext() && iB.hasNext()) { 
					 featureA = (Feature) iA.next(); 
					 featureB = (Feature) iB.next();
				} else { 
					 continue; 
				}
				int minA = featureA.getMin();
				int maxA = featureA.getMax();
				int minB = featureB.getMin();
				int maxB = featureB.getMax();

				// loop until run out of Features on the current
				// Sequence, in either set
				while (true) {
					 if (minB <= maxA) {
						  if (minA <= maxB) { // found overlap
								// since neither set contains contiguous
								// Features, we do not need to look past the
								// current Features in handling the current
								// overlap.  we now need to figure out the
								// bounds of the overlap.
								int minL = (minA > minB) ? minA : minB;
								int maxL;
								if (maxA < maxB) {
									 maxL = maxA;

									 if ((minL == minA) && (maxL == maxA)) {
										  // if same dimenions as featureA, then featureA/B
										  // overlap entirely
										  out.addFeature(featureA);
									 } else {
										  // featureA/B don't entirely overlap, so
										  // create new Feature object
										  out.addFeature(new ExactFeature(minL, maxL, sourceObj));
									 }

									 // featureA ends first, so increment featureA
									 if (iA.hasNext()) { 
										  // featureA is less than featureB, so increment featureA
										  featureA = (Feature) iA.next(); 
										  minA = featureA.getMin();
										  maxA = featureA.getMax();
									 } else { 
										  // have run out of Features in A
										  break; // loop to next source
									 }
								} else {
									 maxL = maxB;

									 if ((minL == minA) && (maxL == maxA)) {
										  // if same dimenions as featureA, then featureA/B
										  // overlap entirely
										  out.addFeature(featureA);
									 } else {
										  // featureA/B don't entirely overlap, so
										  // create new Feature object
										  out.addFeature(new ExactFeature(minL, maxL, sourceObj));
									 }

									 // featureB ends first, so increment featureB
									 if (iB.hasNext()) { 
										  // featureB is less than featureA, so increment featureB
										  featureB = (Feature) iB.next(); 
										  minB = featureB.getMin();
										  maxB = featureB.getMax();
									 } else { 
										  // have run out of Features in B
										  break; // loop to next source
									 }
								}

						  } else {
								if (iB.hasNext()) { 
									 // featureB is less than featureA, so increment featureB
									 featureB = (Feature) iB.next(); 
									 minB = featureB.getMin();
									 maxB = featureB.getMax();
								} else { 
									 // have run out of Features in B
									 break; // loop to next source
								}
						  }
					 } else {
						  if (iA.hasNext()) { 
								// featureA is less than featureB, so increment featureA
								featureA = (Feature) iA.next(); 
								minA = featureA.getMin();
								maxA = featureA.getMax();
						  } else { 
								// have run out of Features in A
								break; // loop to next source
						  }
					 }
				}
		  }
				
		  return out;
	 }

	 /** bMINUS : all positions in T1 that don't exist in T2.
	  *
	  * This will return only overlapping regions between 2 Features.
	  * @XXX This assumes that mergeContiguous() has already been run
	  * on each set of Features; that is, neither set contains
	  * contiguous Features.
	  */
	 public static Track fxn_bMINUS(Track left, Operation operation) {
		  // if 'left' is empty then there will be no matches so just
		  // return an empty Track (ie 'left') and if 'operation' is empty
		  // then the entire 'left' will match so again just return
		  // 'left'.
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return (Track) left.cloneTrack(false);
		  }

		  left.mergeContiguous();
		  HashMap sourcesA = left.getSources();

		  //		  operation.track.mergeContiguous();
		  //		  HashMap sourcesB = operation.track.getSources();
		  Track trackB = operation.track.cloneMerged();
		  HashMap sourcesB = trackB.getSources();

		  // output Track
		  Track out = new Track(false);

		  // step through each Sequence from the first Set and if there
		  // are Features from the second Set on the same Sequence,
		  // then check for overlaps.  
		  for (Iterator s = sourcesA.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();
				Sequence sourceObj = (Sequence) ObjectHandles.sequencePool.get(source);

				// if sourcesB doesn't include 'sourceA' then try next
				// source from sourcesA
				if (! sourcesB.containsKey(source)) continue;
					 
				// get Features for this source
				TreeSet featuresA = (TreeSet) sourcesA.get(source);
				TreeSet featuresB = (TreeSet) sourcesB.get(source);

				Iterator iA = featuresA.iterator();
				Iterator iB = featuresB.iterator();

				Feature featureA;
				Feature featureB;

				// get initial Features
				if (iA.hasNext() && iB.hasNext()) { 
					 featureA = (Feature) iA.next(); 
					 featureB = (Feature) iB.next();
				} else { 
					 continue; 
				}

				int minA = featureA.getMin();
				int maxA = featureA.getMax();
				int minB = featureB.getMin();
				int maxB = featureB.getMax();
		  
				int minL = -1; // store start value for new Feature
				boolean spanA = false; // flag if spanning A or B Feature

				// loop until run out of Features on the current
				// Sequence, in either set
				while (true) {
					 if (minL < 0) { // starting new Feature
						  if (minL == -1) { // compare min's
								if (minA < minB) {
									 minL = minA;
									 spanA = true; // progressing along A
								} else if (minB < minA) {
									 minL = minB;
									 spanA = false; // progressing along B
								} else {
									 // min's equal, so compare max's
									 minL = -2; 
								}
						  }
						  if (minL == -2) { // equal min's so compare max's
								if (maxA < maxB) {
									 minL = maxA;
									 spanA = false; // progressing along B

									 // starting at maxA, so need to increment A
									 if (iA.hasNext()) { 
										  featureA = (Feature) iA.next(); 
										  minA = featureA.getMin();
										  maxA = featureA.getMax();
									 } else { // no more Features in A
										  break; // loop to next source
									 }
								} else if (maxB < maxA) {
									 //									 minL = maxB;
									 minL = maxB + 1;
									 spanA = true; // progressing along A

									 // starting at maxB, so need to increment B
									 if (iB.hasNext()) { 
										  featureB = (Feature) iB.next(); 
										  minB = featureB.getMin();
										  maxB = featureB.getMax();
									 } else { 
										  // no more Features in B, so add featureA
										  // and all remaining Features in A
										  out.addFeature(new ExactFeature(minL, maxA, sourceObj));
										  while (iA.hasNext()) {
												out.addFeature((Feature) iA.next());
										  }
										  break; // loop to next source
									 }
								} else { // A/B are equal so increment both
									 minL = -1; // reset to compare min's next time
									 if (iB.hasNext()) { 
										  featureB = (Feature) iB.next(); 
										  minB = featureB.getMin();
										  maxB = featureB.getMax();

										  if (iA.hasNext()) { 
												featureA = (Feature) iA.next(); 
												minA = featureA.getMin();
												maxA = featureA.getMax();
										  } else { // no more Features in A
												break; // loop to next source
										  }
									 } else { 
										  // no more Features in B, so add remaining 
										  // Features in A and exit
										  while (iA.hasNext()) {
												out.addFeature((Feature) iA.next());
										  }
										  break; // loop to next source
									 }
								}
						  }
					 } else { // already have minL, so need to compute maxL
						  if (spanA) { // progressing along A, so test maxA/minB
								if (maxA <= minB) {
									 if (maxA == minB) {
										  // create new Feature for portion of
										  // featureA that doesn't overlap B
										  out.addFeature(new ExactFeature(minL, maxA-1, sourceObj));
									 } else {
										  if (minL == minA) {
												// same dimenions as featureA so add featureA
												out.addFeature(featureA);
										  } else { 
												// create new Feature for portion
												// of featureA that doesn't
												// overlap with B
												out.addFeature(new ExactFeature(minL, maxA, sourceObj));
										  }
									 }

									 if (iA.hasNext()) { // increment featureA
										  featureA = (Feature) iA.next(); 
										  minA = featureA.getMin();
										  maxA = featureA.getMax();
										  minL = -1;  // reset to compare min's next time
									 } else { // no more Features in A
										  break; // loop to next source
									 }
								} else {
									 // create new Feature for portion of featureA
									 // that doesn't overlap with B
									 out.addFeature(new ExactFeature(minL, minB-1, sourceObj));
									 minL = -2;  // reset to compare max's next time
								}
						  } else { // progressing along B, so test maxB/minA
								if (maxB <= minA) {
									 if (iB.hasNext()) { // increment featureB
										  featureB = (Feature) iB.next(); 
										  minB = featureB.getMin();
										  maxB = featureB.getMax();
										  minL = -1;  // reset to compare min's next time
									 } else { 
										  // no more Features in B, so add featureA
										  // and remaining Features in A
										  out.addFeature(featureA);
										  while (iA.hasNext()) {
												out.addFeature((Feature) iA.next());
										  }
										  break; // loop to next source
									 }
								} else {
									 minL = -2;  // reset to compare max's next time
								}
						  }
					 }
				}
		  }
		  return out;
	 }

	 /** . (bPOS) : all contiguous F in T1 and T2, appropriately spaced */
	 /*
	 public static Track fxn_bPOS(Track left, Operation operation) {
		  Track out = new Track(false);
		  Track oTrack = new Track(false);
		  
		  // if either Track is empty then there will be no matches so
		  // just return an empty Track
		  if ((left.numFeatures() == 0) || (operation.numFeatures() == 0)) {
				return out;
		  }

		  // put each set into a Track to get the Source Sets
		  HashMap sourcesA = left.getSources();

		  // merge all B's because only concerned with exact positions
		  // in B that fall within the range specified.  By merging B's
		  // we don't need to worry about overlapping Features in B.
		  //		  operation.track.mergeContiguous();
		  //		  HashMap sourcesB = operation.track.getSources();
		  Track trackB = operation.track.cloneMerged();
		  HashMap sourcesB = trackB.getSources();

		  // step through each Sequence from the second Set and if there
		  // are Features from the first Set that match the POS
		  // requirements, then add them Features.
		  for (Iterator s = sourcesB.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// if sourcesA doesn't include 'source' then try next
				// source from sourcesB
				if (! sourcesA.containsKey(source)) continue;
					 
				// get Features for this source
				TreeSet featuresA = (TreeSet) sourcesA.get(source);
				TreeSet featuresB = (TreeSet) sourcesB.get(source);

				for (Iterator iA = featuresA.iterator(); iA.hasNext();) {
					 Feature featureA = (Feature) iA.next();
					 int minA = featureA.getMax() + operation.minPos;
					 int maxA = featureA.getMax() + operation.maxPos;
					 
					 for (Iterator iB = featuresB.iterator(); iB.hasNext();) {
						  Feature featureB = (Feature) iB.next();
						  
						  Feature tmp = FeatureUtils.contained(featureB, minA, maxA);
						  if (tmp != null) {
								out.addFeature(featureA);
								oTrack.addFeature(tmp);
						  }
					 }
				}
		  }
		  oTrack.mergeContiguous();
		  out.addFeatures(oTrack.getFeatures());
		  return out;
	 }
	 */
} // Operator.java
