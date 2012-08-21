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
 * @(#)GUISequenceIO.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.io.*;
import java.util.HashMap;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * Static methods used throughout the GUI.
 *
 * @author  Stephen Fisher
 * @version $Id: GUISequenceIO.java,v 1.6.2.11 2007/03/01 21:17:33 fisher Exp $
 */

public class GUISequenceIO {

	 /** 
	  * Define 'filter' as a static global variable so that it persists
	  * across instances of the fileChoosers and thus will 'remember'
	  * what the user last used.  This isn't in GUIUtils because the
	  * user might have a different 'default' here than in
	  * GUIFeatureIO.
	  */
	 static FileFilter filter = null;

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 public static Sequence newSequence() {
		  String msg = "Create a sequence with ID:";
		  String id = JOptionPane.showInputDialog(null, msg,
																"New Sequence",
																JOptionPane.YES_NO_OPTION);
		  if ((id != null) && (id.length() > 0)) {
				msg = "newSequence(\"" + id + "\")";
				
				// add command to history but don't run via console.
				// instead we're running it here so we can return a
				// reference to the Set of Sequences loaded
				Root.runCommand(msg, false);
				
				// create new sequence here
				return new Sequence(id);
		  } else {
				return null;
		  }
	 }

	 /** 
	  * Use a FileChooser to select a Sequence file to load.  The file
	  * can have a FASTA, GenBank, or GloDB (binary) file format.
	  */
	 public static Set loadSequence() {
		  HashMap file = GUIUtils.openFileChooser(GloDBUtils.SEQUENCE, filter);
		  if (file == null) return null;

		  // save the current filter for the next time we open a file
		  filter = (FileFilter) file.get("filter");

		  String filename = (String) file.get("name");
		  String desc = filter.getDescription();

		  SequenceFile sequenceFile = FileIO.getSequenceFileType(desc);
		  if (sequenceFile == null) return null;

		  Set sequences = sequenceFile.loadAll(filename);
		  if (sequences == null) return null;
		  String msg = "loadSequence(\"" + filename + "\", " + sequenceFile.getID() + ")";

		  // add command to history but don't run via console.
		  // instead we're running it here so we can return a
		  // reference to the Set of Sequences loaded
		  Root.runCommand(msg, false);

		  return sequences;
	 }

	 /** 
	  * Use a FileChooser to save the given Sequence, returning the
	  * filename.  The output file can have a FASTA, GenBank, or GloDB
	  * (binary) file format.
	  */
	 public static String saveSequence(String sequence) {
		  HashMap file = GUIUtils.saveFileChooser(GloDBUtils.SEQUENCE, filter);
		  if ((file == null) || (sequence == "")) return "";

		  // save the current filter for the next time we open a file
		  filter = (FileFilter) file.get("filter");

		  String filename = (String) file.get("name");
		  String desc = filter.getDescription();

		  SequenceFile sequenceFile = FileIO.getSequenceFileType(desc);
		  if (sequenceFile == null) return "";

		  String msg = "saveSequence(\"" + sequence + "\", " + sequenceFile.getID() 
				+ ", \"" + filename + "\", 1)";

		  // run command in console
		  Root.runCommand(msg, true);
	 
		  return filename;
	 }

} // GUISequenceIO.java
