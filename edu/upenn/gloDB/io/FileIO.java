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
 * @(#)FileIO.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.GloDBUtils;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Import and export utilities.
 *
 * @author  Stephen Fisher
 * @version $Id: FileIO.java,v 1.16.2.8 2007/03/01 21:17:33 fisher Exp $
 */

public class FileIO {

	 public final static int GLODB = 1;
	 public final static int FASTA = 2;
	 public final static int GFF = 3;
	 public final static int GENBANK = 4;
	 
	 /**
	  * This HashSet is used to store references to TrackFile types.
	  */
	 private static HashSet trackFileTypes = new HashSet();

	 /**
	  * This HashSet is used to store references to SequenceFile types.
	  */
	 private static HashSet sequenceFileTypes = new HashSet();


    //--------------------------------------------------------------------------
    // Setters and Getters

	 /** Returns the set of Track file types. */
	 public static HashSet getTrackFileTypes() { 
		  return trackFileTypes;
	 }

	 /** Returns the set of Sequence file types. */
	 public static HashSet getSequenceFileTypes() { 
		  return sequenceFileTypes;
	 }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * Convert from the integer constant value to a string equivalent.
	  */
	 public static String convertConstant(int val) {
		  switch (val) {
		  case 1: return "GLODB";
		  case 2: return "GFF";
		  case 3: return "FASTA";
		  case 4: return "GenBank";
		  }
		  return "";
	 }

	 /** Returns the DataFiles based on the file type. */
	 public static HashSet getDataTypes(int type) { 
		  if (type == GloDBUtils.TRACK) return getTrackFileTypes();
		  else if (type == GloDBUtils.SEQUENCE) return getSequenceFileTypes();
		  else return null;
	 }

	 /** Returns the DataFile based on the file type and type description. */
	 public static DataFile getDataType(int type, String desc) { 
		  if (type == GloDBUtils.TRACK) return getTrackFileType(desc);
		  else if (type == GloDBUtils.SEQUENCE) return getSequenceFileType(desc);
		  else return null;
	 }

	 /** Adds the Track file type to the list of possible types. */
	 public static void addTrackFileType(TrackFile trackFileType) {
		  trackFileTypes.add(trackFileType);
	 }

	 /** Returns the TrackFile based on the file type description. */
	 public static TrackFile getTrackFileType(String desc) { 
		  if (trackFileTypes != null) {
				for (Iterator i = trackFileTypes.iterator(); i.hasNext();) {
					 TrackFile trackFile = (TrackFile) i.next();
					 if (desc == trackFile.getDesc()) return trackFile;
				}
		  }

		  return null;
	 }

	 /** Returns the TrackFile based on the file type ID. */
	 public static TrackFile getTrackFileType(int id) { 
		  if (trackFileTypes != null) {
				for (Iterator i = trackFileTypes.iterator(); i.hasNext();) {
					 TrackFile trackFile = (TrackFile) i.next();
					 if (id == trackFile.getID()) return trackFile;
				}
		  }

		  return null;
	 }

	 /** Adds the Seqeunce file type to the list of possible types. */
	 public static void addSequenceFileType(SequenceFile sequenceFileType) {
		  sequenceFileTypes.add(sequenceFileType);
	 }

	 /** Returns the SequenceFile based on the file type description. */
	 public static SequenceFile getSequenceFileType(String desc) { 
		  if (sequenceFileTypes != null) {
				for (Iterator i = sequenceFileTypes.iterator(); i.hasNext();) {
					 SequenceFile sequenceFile = (SequenceFile) i.next();
					 if (desc == sequenceFile.getDesc()) return sequenceFile;
				}
		  }

		  return null;
	 }

	 /** Returns the SequenceFile based on the file type ID. */
	 public static SequenceFile getSequenceFileType(int id) { 
		  if (sequenceFileTypes != null) {
				for (Iterator i = sequenceFileTypes.iterator(); i.hasNext();) {
					 SequenceFile sequenceFile = (SequenceFile) i.next();
					 if (id == sequenceFile.getID()) return sequenceFile;
				}
		  }

		  return null;
	 }

} // FileIO.java
