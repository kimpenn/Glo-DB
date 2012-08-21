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
 * @(#)GenBankTrack.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.gui.GUIUtils;
import java.io.*;
import javax.swing.filechooser.FileFilter;

/**
 * Import/Export Track data from/to GenBank files.
 *
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 * THIS FILE IS A PLACE HOLDER
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *
 * @author  Stephen Fisher
 * @version $Id: GenBankTrack.java,v 1.1.2.13 2007/03/01 21:17:33 fisher Exp $
 */

public class GenBankTrack implements TrackFile {

	 private final int ID = FileIO.GENBANK;
	 private final String DESC = "GenBank files (*.gb; *.genbank)";
	 private final String[] EXT = {".gb", ".genbank"};
	 private final FileFilter fileFilter = new GenBankFilter();

    //--------------------------------------------------------------------------
    // Setters and Getters

	 public int getID() { return ID; }

	 public String getDesc() { return DESC; }

	 public String[] getExt() { return EXT; }

	 public FileFilter getFileFilter() { return fileFilter; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * Load all Features in the GenBank file into a single Track and
	  * return the resulting Track object.
	  */
	 public Track load(String filename) {
		  return load(filename, "");
	 }

	 /**
	  * Load all Features in the GenBank file into a single Track and
	  * return the resulting Track object.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public Track load(String filename, String seqID) {
		  // when creating Track's ID, if necessary, remove ".gb"
		  // filename extension
		  String id = filename;
		  if (id.endsWith(".gb")) id = id.substring(1, id.length()-5);
		  if (id.endsWith(".genbank")) id = id.substring(1, id.length()-10);
		  Track track = new Track(false, id);

		  Sequence srcSequence = null;
		  if (seqID.length() > 0) srcSequence = ObjectHandles.getSequence(seqID);

		  try {
				BufferedReader bReader = new BufferedReader(new FileReader(filename));

				String line;
				boolean firstFeat = true;

				Feature feature  = null;  // current Feature

				while ((line = bReader.readLine()).trim() != null) {
					 // skip all comment lines
					 if (! line.startsWith("#")) {
						  if (! firstFeat) {
								// not first Feature so append existing Feature
								// before reseting the variables for the next
								// Feature.
								track.addFeature(feature);
						  } else {
								firstFeat = false;
						  }
						  
						  // split line at every space
						  String[] fields = line.split(" ");
						  
						  // get a reference to the Sequence for this
						  // Feature.  If no Sequence is found, then don't
						  // add this Feature.  If srcSequence already
						  // exists, then just use that instead.
						  Sequence seqRef;
						  if (srcSequence == null) {
								seqRef = ObjectHandles.getSequence(fields[0]);
								if (seqRef == null) continue;
						  } else {
								seqRef = srcSequence;
						  }

						  // create a new Feature object
						  feature = new ExactFeature(Integer.parseInt(fields[3]), 
																 Integer.parseInt(fields[4]), seqRef); 

						  // get Feature attributes
						  String attributes = "";
						  attributes += "source=" + fields[1];    // add source
						  attributes += ";track=" + fields[2];   // add track
						  attributes += ";score=" + fields[5];     // add score
						  attributes += ";strand=" + fields[6];    // add strand
						  attributes += ";frame=" + fields[7];     // add frame
						  if (fields.length > 8) {
								attributes += ";attribs=" + fields[8];      // get attributes
								if (fields.length > 9) {
									 attributes += ";comments=" + fields[9]; // get comments
								}
						  }
						  feature.setAttributes(attributes);

						  // add the Feature object to the Track
						  track.addFeature(feature);
					 }

					 // add last Features info
					 if (feature != null) track.addFeature(feature);
				}

				bReader.close();
		  } catch (FileNotFoundException e) {
				GloDBUtils.printError("File not found: " + e.getMessage());
				return null;
		  } catch (IOException e) {
				GloDBUtils.printError("Error reading file: " + filename);
				return null;
		  }

		  if (track.numFeatures() == 0) {
				// this assumes an empty Track is a mistake, so return null
				GloDBUtils.printError("Unable to load any features from the file: " + filename);
				return null;
		  }

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

		  GloDBUtils.printMsg("Loaded GenBank file: " + filename);
		  return track;
	 }

	 /** 
	  * Save the Track to a file based on it's ID.  This will
	  * overwrite any existing file and append ".gb" to the filename,
	  * if necessary.
	  */
	 public void save(String id) {
		  // add ".gb" filename extension, if necessary
		  String filename = id;
		  if ((! filename.endsWith(".gb")) || (! filename.endsWith(".genbank"))) {
				filename += ".gb";
		  }

		  save(id, filename, true);
	 }

	 /**
	  * Save all Features in a GloDB file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  * @XXX Should offer option to include Sequence data.
	  */
	 public void save(String id, String filename, boolean overwrite) {
		  // add ".gb" filename extension, if necessary
		  if ((! filename.endsWith(".gb")) && (! filename.endsWith(".genbank"))) {
				filename += ".gb";
		  }

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
	  * GenBank specific FileFilter. 
	  * @XXX This should use EXT.
	  */
	 private class GenBankFilter extends FileFilter {
		  public boolean accept(File f) {
				// accept directories
				if (f.isDirectory()) return true;

				// if true, then don't filter by file extensions.
				if (GUIUtils.showAllFiles()) return true;

				// accept files ending in '.genbank' or '.gb'
				if ((f.getName()).endsWith(".genbank")) return true;
				if ((f.getName()).endsWith(".gb")) return true;

				return false;
		  }
		  
		  // set the filter's description
		  public String getDescription() { return DESC; }
	 }

} // GenBankTrack.java

	 
