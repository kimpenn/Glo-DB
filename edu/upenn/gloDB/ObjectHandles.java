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
 * @(#)ObjectHandles.java
 */

package edu.upenn.gloDB;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import javax.swing.*;

/**
 * ObjectHandles.
 *
 * @author  Stephen Fisher
 * @version $Id: ObjectHandles.java,v 1.15.2.13 2007/03/01 21:17:33 fisher Exp $
 */

public class ObjectHandles { 

	 /** 
	  * The HashMap of all existing Tracks.  It is made public so that
	  * the user can directly access the Tracks to test if a specific
	  * Track is in the set and to remove Tracks from the HashMap in
	  * the case of deleting the Track.  When deleting Tracks, it is
	  * necessary to remove the Track from the trackPool.  While new
	  * Tracks are added to the trackPool by default, it is possible to
	  * create a Track and not have it added to the trackPool.  This
	  * should be avoided as certain functions require the Track to
	  * exist in the trackPool.
	  */
	 public static HashMap trackPool = new HashMap();

	 /**
	  * The set of all existing Feature objects (Fuzzy and Exact).  It
	  * is made public so that the user can directly access the
	  * Features to test if a specific Feature is in the set and to
	  * remove Features from the set in the case of deleting the
	  * Feature. When removing Features, they must be removed from the
	  * featurePool.  While new Features are added to the featurePool
	  * by default, it is possible to create a Feature and not have it
	  * added to the featurePool. This should be avoided as certain
	  * functions require the Feature to exist in the featurePool.
	  */
	 //	 private static HashSet featurePool = new HashSet();

	 /** 
	  * The set of all existing Sequences.  It is made public so that
	  * the user can directly access the tracks to test if a specific
	  * Sequence is in the set and to remove Sequences from the set in
	  * the case of deleting the Sequence.  When removing Sequences,
	  * they must be removed from the sequencePool.  While new
	  * Sequences are added to the sequencePool by default, it is
	  * possible to create a Sequence and not have it added to the
	  * sequencePool. This should be avoided as certain functions
	  * require the Sequence to exist in the sequencePool.
	  */
	 public static HashMap sequencePool = new HashMap();

	 /**
	  * This is a duplicate list of Tracks in the trackPool.  It is
	  * used by the GUI.
	  */
	 private static DefaultComboBoxModel trackList = new DefaultComboBoxModel();

	 /**
	  * This is a duplicate list of Features in the featurePool.  It is
	  * used by the GUI.
	  */
	 //	 private static DefaultListModel featureList = new DefaultListModel();

	 /**
	  * This is a duplicate list of Sequences in the sequencePool.  It is
	  * used by the GUI.
	  */
	 private static DefaultComboBoxModel sequenceList = new DefaultComboBoxModel();

	 /**
	  * This is a list of all Tracks in the trackPool, that contain
	  * each Sequence.  It is used by the GUI.
	  */
	 public static HashMap trackBySequenceList = new HashMap();

    //--------------------------------------------------------------------------
    // Setters and Getters
   
    /** Get the trackPool. */
    public static HashMap getTrackPool() { return trackPool; }

    /** Get the trackList. */
    public static DefaultComboBoxModel getTrackList() { return trackList; }

    /** Get the featurePool. */
	 //    public static HashSet getFeaturePool() { return featurePool; }

    /** Get the featureList. */
	 //    public static DefaultListModel getFeatureList() { return featureList; }

    /** Get the sequencePool. */
    public static HashMap getSequencePool() { return sequencePool; }

    /** Get the sequenceList. */
    public static DefaultComboBoxModel getSequenceList() { return sequenceList; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /** Add a Tracks to the trackPool. */
	 public static void addTrack(Track track) throws InvalidIDException { 
		  String id = track.getID();
		  if (trackPool.containsKey(id)) {
				String msg = "ID \"" + id + "\" already exists in trackPool.";
				throw new InvalidIDException(msg);
		  }

		  // add Track to trackPool
		  trackPool.put(id, track); 

		  // add Track to trackList, for GUI
		  trackList.addElement(id);

		  // add Track to appropriate trackLists in
		  // trackBySequenceList
		  Set sources = track.getSourceSet();
		  if (sources != null) {
				TreeSet trackSet;
				for (Iterator i = sources.iterator(); i.hasNext();) {
					 String sequence = (String) i.next(); 
					 if (trackBySequenceList.containsKey(sequence)) {
						  trackSet = (TreeSet) trackBySequenceList.get(sequence);
					 } else {
						  // we should never reach this point -- empty DLMs
						  // should be created when Sequences are added to
						  // sequencePool.
						  trackSet = new TreeSet();
					 }
					 trackSet.add(id);
					 trackBySequenceList.put(sequence, trackSet);
				}
		  }
	 }

	 /** Removes the Track object for trackPool and other relevant lists. */
	 public static void removeTrack(String id) {
		  Track track = getTrack(id);
		  if (track != null) removeTrack(track);
	 }

	 /** Removes the Track object for trackPool and other relevant lists. */
	 public static void removeTrack(Track track) {
		  String id = track.getID();

		  // if not in trackPool, then not in any lists
		  if (trackPool.containsKey(id)) {
				// remove from trackPool
				trackPool.remove(id);

				// remove from trackList
				trackList.removeElement(id);

				// remove from trackBySequenceList
				Set sources = track.getSourceSet();
				if (sources != null) {
					 TreeSet trackSet;
					 for (Iterator i = sources.iterator(); i.hasNext();) {
						  String sequence = (String) i.next(); 
						  // this test should be unnecessary, as must be in
						  // trackList if in trackPool
						  if (trackBySequenceList.containsKey(sequence)) {
								trackSet = (TreeSet) trackBySequenceList.get(sequence);
								trackSet.remove(id);
								trackBySequenceList.put(sequence, trackSet);
						  }
					 }
				}
		  }
	 }

	 /** Rebuilds the Track relevant lists. */
	 public static void rebuildTrack(Track track) {
		  removeTrack(track);
		  addTrack(track);
	 }

	 /** 
	  * Returns true if the Track object exists in the trackPool.
	  */
	 public static boolean containsTrack(String id) { 
		  if (id == "") return false;
		  else return trackPool.containsKey(id); 
	 }

	 /** 
	  * Returns the Track object for the given ID. 
	  * @XXX Should throw an exception if 'id' is not found.
	  */
	 public static Track getTrack(String id) { 
		  if (id == "") return null;
		  else return (Track) trackPool.get(id); 
	 }

	 /** Returns a iterator over all Tracks in trackPool. */
	 public static Iterator trackIterator() { return trackPool.values().iterator(); }

	 /** 
	  * Changes the Track's ID in trackPool, trackList, and
	  * trackBySequenceList.
	  */
	 public static void renameTrack(String oldID, String newID) { 
		  Track track = getTrack(oldID);

		  // make sure track exists
		  if (track == null) {
				GloDBUtils.printError("Track \"" + oldID + "\" not found.");
				return;
		  }

		  renameTrack(track, newID);
	 }

	 /** 
	  * Changes the Track's ID in trackPool, trackList, and
	  * trackBySequenceList.
	  */
	 public static void renameTrack(Track track, String newID) { 
		  // make sure track exists
		  if (track == null) {
				GloDBUtils.printError("Null track, can not be renamed.");
				return;
		  }

		  // check to make sure ID doesn't already exist in trackPool
		  if (trackPool.containsKey(newID)) {
				String msg = "ID \"" + newID + "\" already exists in trackPool.";
				throw new InvalidIDException(msg);
		  }

		  // remove the Track from trackPool and other relevant lists.
		  removeTrack(track);

		  // change ID and add to trackPool
		  track.id = newID;
		  addTrack(track);
	 }

    /** 
	  * Get the trackList that contains all Tracks with the given
	  * Sequence.
	  */
    public static TreeSet getTrackBySequenceList(String id) { 
		  return (TreeSet) trackBySequenceList.get(id); 
	 }


	 /** Add a Feature to the featurePool. */
	 public static void addFeature(Feature feature) { 
		  //		  featurePool.add(feature); 
		  //		  featureList.addElement(feature);
	 }

	 /** Returns a iterator over all Features in featurePool. */
	 //	 public static Iterator featureIterator() { return featurePool.iterator(); }


	 /** Add a Sequence to the sequencePool. */
	 public static void addSequence(Sequence sequence) throws InvalidIDException {
		  String id = sequence.getID();
		  if (sequencePool.containsKey(id)) {
				String msg = "ID \"" + id + "\" already exists in sequencePool.";
				throw new InvalidIDException(msg);
		  }
			  
		  // add Sequence to sequencePool
		  sequencePool.put(id, sequence);

		  // add Sequence to sequenceList, for GUI
		  sequenceList.addElement(id);

		  // make sure the Sequence is represented in trackBySequenceList
		  if (! trackBySequenceList.containsKey(id)) {
				trackBySequenceList.put(id, new TreeSet());
		  }
	 }

	 /** Removes the Sequence object for sequencePool and other relevant lists. */
	 public static void removeSequence(Sequence sequence) {
		  String id = sequence.getID();

		  // if not in sequencePool, then not in any lists
		  if (sequencePool.containsKey(id)) {
				// remove from sequencePool
				sequencePool.remove(id);

				// remove from sequenceList
				sequenceList.removeElement(id);
		  }
	 }

	 /** 
	  * Returns true if the Sequence object exists in the sequencePool.
	  */
	 public static boolean containsSequence(String id) { 
		  if (id == "") return false;
		  else return sequencePool.containsKey(id); 
	 }

	 /** Returns a iterator over all Sequences in sequencePool. */
	 public static Iterator sequenceIterator() { return sequencePool.values().iterator(); }

	 /** 
	  * Returns the Sequence object for the given ID. 
	  * @XXX Should throw an exception if 'id' is not found.
	  */
	 public static Sequence getSequence(String id) { 
		  if (id == "") return null;
		  else return (Sequence) sequencePool.get(id); 
	 }

	 /*
	  * Changes the Sequence's ID in sequencePool, sequenceList, and
	  * trackBySequenceList.
	  * @XXX WARNING, this needs to change the relevant Feature.source
	  * values when the sequence ID changes.
	  * @XXX could only allow this if trackSet is empty but that might
	  * be confusing
	  */
	 /*
	 public static void renameSequence(Sequence sequence, String newID) { 
		  // check to make sure ID doesn't already exist in sequencePool
		  if (sequencePool.containsKey(newID)) {
				String msg = "ID \"" + newID + "\" already exists in sequencePool.";
				throw new InvalidIDException(msg);
		  }

		  // remove the Sequence from sequencePool and other relevant lists
		  removeSequence(sequence);

		  // update the sequence reference in trackBySequenceList
		  String oldID = sequence.getID();
		  if (trackBySequenceList.containsKey(oldID)) {
				TreeSet trackSet = (TreeSet) trackBySequenceList.get(oldID);
				trackBySequenceList.remove(oldID);
				trackBySequenceList.put(newID, trackSet);
		  }
				
		  // change ID and add to sequencePool
		  sequence.id = newID;
		  addSequence(sequence);
	 }
	 */
} // ObjectHandles.java
