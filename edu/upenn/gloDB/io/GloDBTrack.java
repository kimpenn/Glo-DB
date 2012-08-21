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
 * @(#)GloDBTrack.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.gui.GUIUtils;
import java.io.*;
import javax.swing.filechooser.FileFilter;

/**
 * Import/Export Track data from/to GloDB files.
 *
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 * THIS FILE IS A PLACE HOLDER
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *
 * @author  Stephen Fisher
 * @version $Id: GloDBTrack.java,v 1.1.2.6 2007/02/16 17:14:47 fisher Exp $
 */

public class GloDBTrack implements TrackFile {

	 private final int ID = FileIO.GLODB;
	 private final String DESC = "Glo-DB files (*.glo)";
	 private final String[] EXT = {".glo"};
	 private final FileFilter fileFilter = new GloDBFilter();

    //--------------------------------------------------------------------------
    // Setters and Getters

	 public int getID() { return ID; }

	 public String getDesc() { return DESC; }

	 public String[] getExt() { return EXT; }

	 public FileFilter getFileFilter() { return fileFilter; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * Load all Features in the GloDB file into a single Track and
	  * return the resulting Track object.
	  */
	 public Track load(String filename) {
		  return load(filename, "");
	 }

	 /**
	  * Load all Features in the GloDB file into a single Track and
	  * return the resulting Track object.  If a source isn't
	  * provided, then if appropriate, this will attempt to load source
	  * data from the file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public Track load(String filename, String sourceID) {
		  File file = new File(filename);
		  // return null if the file doesn't exist
		  if (! file.exists()) {
				GloDBUtils.printMsg("ERROR: file \"" + filename + "\" doesn't exist.");
				return null;
		  }

		  Track track = null;
		  try {
				// load the Track
				FileInputStream fStream = new FileInputStream(file);
				ObjectInputStream oStream = new ObjectInputStream(fStream);
				track = (Track) oStream.readObject();
				oStream.close();

				// add track to trackPool
				try {
					 ObjectHandles.addTrack(track);
				} catch (InvalidIDException e) {
					 String id_new = Track.randomID("_T");
					 String msg = "ID \"" + track.getID() + "\" already exists, using ID \"" + id_new + "\" instead.";
					 GloDBUtils.printWarning(msg);
					 
					 // add self to set of all Tracks, using new ID
					 track.setID(id_new, false);
					 ObjectHandles.addTrack(track);
				}

				// add the Track to trackPool and return a reference
				// to the Track
				ObjectHandles.addTrack(track);
				return track;
		  } catch (FileNotFoundException e) {
				// problem with FileInputStream
				GloDBUtils.printMsg("ERROR: file \"" + filename + "\" can not be opened.");
		  } catch (ClassNotFoundException e) {
				// problem with ObjectInputStream.readObject().  XXX do we
				// need to close 'oStream'?
				GloDBUtils.printMsg("ERROR: input file \"" + filename + "\" does not"
										 + " contain a valid Track.");
		  } catch (IOException e) {
				// problem with ObjectInputStream.  XXX do we need to
				// close 'oStream'?
				GloDBUtils.printMsg("ERROR: reading input file \"" + filename + "\".");
		  } catch (InvalidIDException e) {
				GloDBUtils.printMsg("ERROR: ID \"" + track.getID() + "\" already exists.");
		  }

		  return null;
	 }

	 /** 
	  * Save the Track to a file based on it's ID.  This will
	  * overwrite any existing file and append ".glo" to the filename,
	  * if necessary.
	  */
	 public void save(String id) {
		  // add ".glo" filename extension, if necessary
		  String filename = id;
		  if (! filename.endsWith(".glo")) filename += ".glo";

		  save(id, filename, true);
	 }

	 /**
	  * Save all Features in a GloDB file.  If the file already exists,
	  * then overwrite it if 'overwrite' is true.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  * @XXX Should probably throw an exception if the file exists and
	  * not supposed to overwrite the file.
	  * @XXX Should offer option to include Sequence data.
	  */
	 public void save(String id, String filename, boolean overwrite) {
		  File file = new File(filename);
		  // if the file already exists and not supposed to overwrite
		  // it, then return on error.
		  if (file.exists() && (! overwrite)) {
				GloDBUtils.printMsg("ERROR: file \"" + filename + "\" already exists.");
				return;
		  }

		  try {
				FileOutputStream fStream = new FileOutputStream(file);
				ObjectOutputStream oStream = new ObjectOutputStream(fStream);
				Track f = ObjectHandles.getTrack(id);
				oStream.writeObject(f);
				oStream.flush();
				oStream.close();
		  } catch (FileNotFoundException e) {
				// problem with FileOutputStream
				GloDBUtils.printMsg("ERROR: file \"" + filename + "\" can not be opened.");
		  } catch (IOException e) {
				// problem with ObjectOutputStream.  XXX do we need to
				// close 'oStream'?
				GloDBUtils.printMsg("ERROR: writting output file \"" + filename + "\".");
		  }
	 }

	 /** 
	  * GloDB specific FileFilter. 
	  * @XXX This should use EXT.
	  */
	 private class GloDBFilter extends FileFilter {
		  public boolean accept(File f) {
				// accept directories
				if (f.isDirectory()) return true;
				
				// if true, then don't filter by file extensions.
				if (GUIUtils.showAllFiles()) return true;

				// accept files ending in '.glo'
				if ((f.getName()).endsWith(".glo")) return true;

				return false;
		  }
		  
		  // set the filter's description
		  public String getDescription() { return DESC; }
	 }

} // GloDBTrack.java

	 
