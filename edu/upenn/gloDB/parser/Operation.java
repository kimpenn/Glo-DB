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
 * @(#)Operation.java
 */

package edu.upenn.gloDB.parser;

import edu.upenn.gloDB.*;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * These objects store the operations parsed by the parser.  An
 * Operation can contain either an array of Operations (ie a set of
 * Operations to be processed as a group), or a Set of Features (ie a
 * Track).
 *
 * @author  Stephen Fisher
 * @version $Id: Operation.java,v 1.8.2.22 2007/03/01 21:17:33 fisher Exp $
 */

public class Operation implements Cloneable { 

	 /** 
	  * If this Operation is a 'group' (ie '()'), then instead of
	  * containing a SortedSet of Features, it will contain an array of
	  * Operations.  This array of Operations, being a group will be
	  * processed and converted into a SortedSet of Features and stored
	  * in 'track'.
	  */
	 private ArrayList group = null;

	 /** 
	  * SortedSet of Features to be searched.  This set is filtered by
	  * sequence, minLength, maxLength, minSeqPos, and maxSeqPos.  We
	  * use a Track to hold the operands because we can then take
	  * advantange of the various properties of a Track when
	  * computing the Operation.
	  * @XXX Needs to be private.
	  */
	 public Track track = null;

	 /** 
	  * Flag for the type of operation to be performed with the
	  * preceeding Operation.  When new types are added
	  * Operator.getType(), Parser.jj and Operator need to be updated.
	  *   -1 = null (no preceeding operator - first Track in a group).
	  *   0 = POS, 1 = AND, 2 = OR, 3 = MINUS, 4 = sAND, 5 = sMINUS
	  *   10 = ., 11 = &&, 12 = ||, 13 = -
	  * @XXX Default = '-1'
	  */
	 private int type = -1;

	 /** 
	  * Flag for whether to invert the output Track, treating the Track
	  * as a mask over the Sequence (ie binary operation).  There is no
	  * comparable option that preserves the Feature sets (ie
	  * non-binary operation).
	  * @XXX Default = 'false'
	  */
	 private boolean negate = false;

	 /** 
	  * Minimum number of positions since the previous Track. 
	  * @XXX Default = '0'
	  * @XXX Needs to be private.
	  */
	 int minPos = 0;

	 /** 
	  * Maximum number of positions since the previous Track.
	  * @XXX Default = '0'
	  * @XXX Needs to be private.
	  */
	 int maxPos = 0;

	 /** 
	  * If not null, then ignore all other Sequences when testing for
	  * Feature matches.
	  * @XXX Default = 'null'
	  */
	 private Sequence sequence = null;

	 /** 
	  * Minimum acceptible Feature width. 
	  * @XXX Default = '0'
	  */
	 private int minLength = 0;

	 /** 
	  * Maximum acceptible Feature width. 
	  * @XXX Default = '0'
	  */
	 private int maxLength = 0;

	 /** 
	  * Minimum acceptible position within 'sequence'. 
	  * @XXX Default = '0'
	  */
	 private int minSeqPos = 0;

	 /** 
	  * Maximum acceptible position within 'sequence'.  If -1, then
	  * goes to maximum Sequence length.
	  * @XXX Default = '0'
	  */
	 private int maxSeqPos = 0;

	 /** 
	  * Minimum number of repeating features.
	  * @XXX Default = '1'
	  */
	 private int minRepeat = 1;

	 /** 
	  * Maximum number of repeating features.
	  * @XXX Default = '1'
	  */
	 private int maxRepeat = 1;

	 /** 
	  * Minimum number of positions between repeating features. If '0',
	  * then any overlap between features will be valid.
	  * @XXX Default = '0'
	  */
	 private int minWithin = 0;

	 /** 
	  * Maximum number of positions between repeating features.
	  * @XXX Default = '0'
	  */
	 private int maxWithin = 0;

	 /** 
	  * Flag for whether the mapped Feature should be included in the
	  * output.
	  * @XXX Default = 'false'
	  */
	 private boolean ignore = false;

	 /** 
	  * Number of times the Operation has been matched.
	  * @XXX Needs to be private.
	  */
	 int matched = 0;

	 /** 
	  * An Iterator over 'Track'.  This is initialized when the
	  * Operation is created and will be increment as successive
	  * Features are tested.
	  */
	 private Iterator iterator = null;

	 /** 
	  * Create a new Operation object, used by clone() below.
	  */
	 private Operation() {
		  initialize();
	 }

	 /** 
	  * Create a new Operation object containing a group.
	  */
	 public Operation(ArrayList group) {
		  this.group = new ArrayList(group);
		  initialize();
	 }

	 /** 
	  * Create a new Operation object with a Track.
	  */
	 public Operation(Track track) {
		  this.track = (Track) track.cloneTrack(false);
		  initialize();
	 }

	 /** 
	  * Create a new Operation object with a set of Tracks.
	  */
	 public Operation(Set tracks) {
		  // iterate over all Tracks in the set
		  Iterator i = tracks.iterator();
		  Track t = (Track) i.next();

		  // create a new Track with the contents of the first Track.
		  // This avoids the need to recreate the sources HashMap.
		  this.track = (Track) t.cloneTrack(false);

		  // add all of the Features for all of the remaining Tracks
		  while (i.hasNext()) {
				t = (Track) i.next();
				this.track.addFeatures(t.getFeatures());
		  }

		  initialize();
	 }

	 private void initialize() {
		  // initialize the 'tracks' iterator.
		  resetTrack();
	 }

    //--------------------------------------------------------------------------
    // Setters and Getters
   
	 /** Sets the array of Operations. */
	 public void setGroup(ArrayList group) { this.group = group; }

	 /** Gets the array of Operations. */
	 public ArrayList getGroup() { return this.group; }

	 /** 
	  * Set the Operation type:
	  *   0 = POS, 1 = AND, 2 = nAND, 3 = OR, 4 = LESS
	  *   10 = ., 11 = &&, 12 = ^&&, 13 = ||, 14 = -
	  */
	 public void setType(int type) { this.type = type; }

	 /** 
	  * Get the Operation type:
	  *   0 = POS, 1 = AND, 2 = nAND, 3 = OR, 4 = LESS
	  *   10 = ., 11 = &&, 12 = ^&&, 13 = ||, 14 = -
	  */
	 public int getType() { return this.type; }

	 /** 
	  * Flag for whether to invert the output Track, treating the Track
	  * as a mask over the Sequence (ie binary operation).  There is no
	  * comparable option that preserves the Feature sets (ie
	  * non-binary operation).
	  */
	 public void setNegate(boolean negate) { this.negate = negate; }

	 /**
	  * Flag for whether to invert the output Track, treating the Track
	  * as a mask over the Sequence (ie binary operation).  There is no
	  * comparable option that preserves the Feature sets (ie
	  * non-binary operation).
	  */
	 public boolean isNegate() { return this.negate; }

	 /** Sets the ignore flag. */
	 public void setIgnore(boolean ignore) { this.ignore = ignore; }

	 /** Gets the ignore flag. */
	 public boolean isIgnore() { return this.ignore; }

	 /**
	  * This will remove all Features from "track" that are not on
	  * "sequence".  If this is a group, then 'seq' is propogated to
	  * all inner groups.
	  */
	 public void setSequence(Sequence seq) {
		  this.sequence = seq;

		  // propogate the Sequence info to all inner groups
		  if (isGroup()) {
				for (Iterator i = group.iterator(); i.hasNext();) {
					 Operation operation = (Operation) i.next();
					 operation.setSequence(seq);
				}
		  }

		  // do the sequence filtering for this Operation
		  filterOnSequence();
	 }

	 /**
	  * This will set the min/max Length for all Features in "track".
	  * If not a group, then the values are set and "filterOnLength()"
	  * is run to perform the filtering.  If a group, then
	  * filterOnLength() is not run and must be run separately, because
	  * should only filter the output of the group operation.
	  * @XXX Should throw an exception if max < min.
	  */
	 public void setLength(int min, int max) {
		  // make sure lengths are legal.
		  if (max < min) { 
				GloDBUtils.printError("The max length is less than the min length, when filtering on length.");
				return; 
		  }

		  this.minLength = min;
		  this.maxLength = max;

		  // if group then don't do anything, this will be handled in
		  // ParserUtils.getOperation()
		  if (isGroup()) return;

		  // not a group so we can just do the length filtering now
		  filterOnLength();
	 }

	 /**
	  * This will set the min/max seqquence position for all Features
	  * in "track".  If not a group, then the values are set and
	  * "filterOnSeqPos()" is run to perform the filtering.  If a
	  * group, then filterOnSeqPos() is not run and must be run
	  * separately.
	  * @XXX Should throw an exception if max < min.
	  */
	 public void setSeqPos(int min, int max) {
		  // make sure lengths are legal.
		  if ((max != -1) && (max < min)) { 
				GloDBUtils.printError("The max pos is less than the min pos, when filtering on sequence position.");
				return; 
		  }

		  this.minSeqPos = min;
		  this.maxSeqPos = max;

		  // if group then don't do anything, this will be handled in
		  // ParserUtils.getOperation()
		  if (isGroup()) return;

		  // not a group so we can just do the filtering now
		  filterOnSeqPos();
	 }

	 /**
	  * This will set the min/max Repeat for all Features in "track".
	  * If not a group, then the values are set and "filterOnRepeat()"
	  * is run to perform the filtering.  If a group, then
	  * filterOnRepeat() is not run and must be run separately, because
	  * should only filter the output of the group operation.
	  * @XXX Should throw an exception if max < min.
	  */
	 public void setRepeat(int min, int max) { setRepeat(min, max, 0, 0); }
	 public void setRepeat(int min, int max, int minW, int maxW) {
		  // make sure repeats are legal.
		  if (max < min) { 
				GloDBUtils.printError("The max repeat is less than the min repeat, when filtering on repeats.");
				return; 
		  }
		  if (maxW < minW) { 
				GloDBUtils.printError("The max within is less than the min within, when filtering on repeats.");
				return; 
		  }

		  this.minRepeat = min;
		  this.maxRepeat = max;
		  this.minWithin = minW;
		  this.maxWithin = maxW;

		  // if group then don't do anything, this will be handled in
		  // ParserUtils.getOperation()
		  if (isGroup()) return;

		  // not a group so we can just do the repeat filtering now
		  filterOnRepeat();
	 }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * This will remove all Features from "track" that are not on
	  * "sequence".
	  */
	 public void filterOnSequence() {
		  // if no Sequence or Features then don't do anything.
		  if ((sequence == null) || (track == null)) return;

		  track.filterOnSequence(sequence.getID());
	 }

	 /**
	  * This will remove all Features from "track" that are not within
	  * the 'minLength'/'maxLength' boundaries.
	  */
	 public void filterOnLength() {
		  // if maxLength is 0 or no features then don't filter.
		  if ((maxLength == 0) || (track == null)) return;

		  track.filterOnLength(minLength, maxLength);
	 }

	 /**
	  * This will remove all Features from "track" that are not within
	  * the 'minSeqPos'/'maxSeqPos' boundaries.  If 'max' is -1, then
	  * goes to maximum Sequence length.
	  */
	 public void filterOnSeqPos() {
		  // if maxSeqPos is 0 or no features then don't filter
		  if ((maxSeqPos == 0) || (track == null)) return;

		  track.filterOnSeqPos(minSeqPos, maxSeqPos);
	 }

	 /**
	  * This will remove all Features from "track" that are not within
	  * the 'minRepeat/maxRepeat' and 'minWithin/maxWithin' boundaries.
	  */
	 public void filterOnRepeat() {
		  // if minRepeat/maxRepeat are less than 2 or no features then
		  // nothing to filter
		  if ((minRepeat < 2) || (maxRepeat < 2) || (track == null)) return;

		  track.filterOnRepeat(minRepeat, maxRepeat, minWithin, maxWithin);
	 }
   
	 /** Returns true if this Operation is a group. */
	 public boolean isGroup() { 
		  if (group == null) return false;
		  else return true;
	 }

	 /**
	  * Returns the number of Features contained in Operation.
	  */
	 public int numFeatures() { 
		  if (track == null) {
				return 0;
		  } else {
				return track.numFeatures();
		  }
	 }

	 /** Restart iterator. */
	 public void resetTrack() { 
		  if (track != null) iterator = track.featureIterator();
	 }

	 /** Return true if there are more Features in 'iterator'. */
	 public boolean hasNextFeature() { 
		  if (iterator != null) {
				return iterator.hasNext(); 
		  } else {
				return false;
		  }
	 }

	 /** Restart the Tracks iterator and return the first Feature. */
	 public Feature firstFeature() { 
		  if (track != null) {
				resetTrack();
				return nextFeature(); 
		  } else {
				return null;
		  }
	 }

	 /** Return the next Feature in iterator. */
	 public Feature nextFeature() {
		  if (hasNextFeature()) { 
				return (Feature) iterator.next(); 
		  } else { 
				return null; 
		  }
	 }

 	 /** Returns an iterator over the group list, null if empty group. */
	 public Iterator groupIterator() { 
		  if (group == null) { return null; }
		  else { return group.iterator(); }
	 }

	 /**
	  * Create a shallow clone (just clone the structure, not the
	  * Objects) of the existing object.
	  */
	 public Object clone() {
		  Operation operation = new Operation();
		  operation.group = this.group;
		  operation.track = this.track;
		  operation.type = this.type;
		  operation.negate = this.negate;
		  operation.minPos = this.minPos;
		  operation.maxPos = this.maxPos;
		  operation.sequence = this.sequence;
		  operation.minLength = this.minLength;
		  operation.maxLength = this.maxLength;
		  operation.ignore = this.ignore;
		  operation.minSeqPos = this.minSeqPos;
		  operation.maxSeqPos = this.maxSeqPos;
		  operation.minRepeat = this.minRepeat;
		  operation.maxRepeat = this.maxRepeat;
		  operation.minWithin = this.minWithin;
		  operation.maxWithin = this.maxWithin;
		  operation.matched = this.matched;
		  operation.iterator = this.iterator;

		  return operation;
	 }

	 /** Returns Operation information for debugging purposes. */
	 public String toString() {
		  String out = "\n";

		  if (isGroup()) {
				out += "group:          " + group.size() + "\n";
		  } else {
				out += "group:          null\n";
		  }
		  if (track == null) {
				out += "Track:          null\n";
		  } else if (track.numFeatures() == 0) {
				out += "Track (0 features):\n";
		  } else {
				out += "Track (" + track.numFeatures() + " features):" + track.toStringMore();
		  }
		  out += "Type:           " + Operator.getType(type) + "\n";
		  out += "Not:            " + negate + "\n";
		  out += "Min spacing:    " + minPos + "\n";
		  out += "Max spacing:    " + maxPos + "\n";
		  if (sequence == null) {
				out += "Sequence:       null\n";
		  } else {
				out += "Sequence:       " + sequence.getID() + "\n";
		  }
		  out += "Min length:     " + minLength + "\n";
		  out += "Max length:     " + maxLength + "\n";
		  out += "Min seq pos:    " + minSeqPos + "\n";
		  out += "Max seq pos:    " + maxSeqPos + "\n";
		  out += "Min repeat:     " + minRepeat + "\n";
		  out += "Max repeat:     " + maxRepeat + "\n";
		  out += "Min within:     " + minWithin + "\n";
		  out += "Max within:     " + maxWithin + "\n";
		  out += "Ignore:         " + ignore + "\n";
		  out += "Matched:        " + matched + "\n";

		  return out;
	 }
} // Operation.java
