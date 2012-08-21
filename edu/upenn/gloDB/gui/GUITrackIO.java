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
 * @(#)GUITrackIO.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.io.*;
import java.util.HashMap;
import javax.swing.filechooser.FileFilter;

/**
 * Static methods used to handle Track IO.
 *
 * @XXX Need to make it easy for users to set the initial default
 * values for SHOW_ALL_FILES and USE_FILE_EXTENSIONS.
 * @XXX Need to allow for user setting of valid file extensions.
 *
 * @author  Stephen Fisher
 * @version $Id: GUITrackIO.java,v 1.1.2.9 2007/03/01 21:17:33 fisher Exp $
 */

public class GUITrackIO {

	 /** 
	  * Define 'filter' as a static global variable so that it persists
	  * across instances of the fileChoosers and thus will 'remember'
	  * what the user last used.  This isn't in GUIUtils because the
	  * user might have a different 'default' here than in
	  * GUISequenceIO.
	  */
	 static FileFilter filter = null;

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /** 
	  * Use a FileChooser to select a Track file to load.  The file
	  * can have a FASTA, GenBank, GFF, or GloDB (binary) file format.
	  */
	 public static Track loadTrack() {
		  HashMap file = GUIUtils.openFileChooser(GloDBUtils.TRACK, filter);
		  if (file == null) return null;

		  // save the current filter for the next time we open a file
		  filter = (FileFilter) file.get("filter");

		  String filename = (String) file.get("name");
		  String desc = filter.getDescription();

		  TrackFile trackFile = FileIO.getTrackFileType(desc);
		  if (trackFile == null) return null;

		  Track track = trackFile.load(filename);
		  if (track == null) return null;
		  String msg = "loadTrack(\"" + filename + "\", " + trackFile.getID() + ")";

		  // add command to history but don't run via console.
		  // instead we're running it here so we can return a
		  // reference to the Track loaded
		  Root.runCommand(msg, false);
		  return track;
	 }

	 /** 
	  * Use a FileChooser to save the given Track, returning the
	  * filename.  The file can have a FASTA, GenBank, GFF, or GloDB
	  * (binary) file format.
	  */
	 public static String saveTrack(String track) {
		  HashMap file = GUIUtils.saveFileChooser(GloDBUtils.TRACK, filter);
		  if ((file == null) || (track == "")) return "";

		  // save the current filter for the next time we open a file
		  filter = (FileFilter) file.get("filter");

		  String filename = (String) file.get("name");
		  String desc = filter.getDescription();

		  TrackFile trackFile = FileIO.getTrackFileType(desc);
		  if (trackFile == null) return "";

		  String msg = "saveTrack(\"" + track + "\", " + trackFile.getID() 
				+ ", \"" + filename + "\", 1)";

		  // run command in console
		  Root.runCommand(msg, true);

		  return filename;
	 }

} // GUITrackIO.java
