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
 * @(#)GFFTrack.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.gui.GUIUtils;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;

/**
 * Import/Export Track data from/to GFF files. <br><br>
 *
 * File format (each column is separated by a tab character '\t'):
 * <table border="1">
 * <tr><td>seqname</td><td>source</td><td>feature</td><td>start</td><td>end</td><td>score</td><td>strand</td><td>frame</td><td>[attributes]</td></tr>
 * </table><br>
 *
 * Examples:
 * <table border="1">
 * <tr><td>SEQ1</td><td>EMBL</td><td>splice5</td><td>172</td><td>173</td><td>.</td><td>+</td><td>.</td><td>&nbsp;</td></tr> 
 * <tr><td>SEQ1</td><td>netgene</td><td>splice5</td><td>172</td><td>173</td><td>0.94</td><td>+</td><td>.</td><td>&nbsp;</td></tr>
 * <tr><td>SEQ1</td><td>genie</td><td>sp5-20</td><td>163</td><td>182</td><td>2.3</td><td>+</td><td>.</td><td>&nbsp;</td></tr> 
 * <tr><td>SEQ2</td><td>grail</td><td>ATG</td><td>17</td><td>19</td><td>2.1</td><td>-</td><td>0</td><td>&nbsp;</td></tr> 
 * <tr><td>seq1</td><td>BLASTX</td><td>similarity</td><td>101</td><td>235</td><td>87.1</td><td>+</td><td>0</td><td>Target "HBA_HUMAN" 11 55 ; E_value 0.0003</td></tr>
 * <tr><td>dJ102G20</td><td>GD_mRNA</td><td>coding_exon</td><td>7105</td><td>7201</td><td>.</td><td>-</td><td>2</td><td>Sequence "dJ102G20.C1.1"</td></tr>
 * <tr><td>X</td><td>gadfly</td><td>exon</td><td>3118</td><td>3280</td><td>.</td><td>-</td><td>.</td><td>genegrp=CG3038; transgrp=CG3038-RB; name=CG3038:1</td></tr>
 * <tr><td>X</td><td>gadfly</td><td>exon</td><td>2850</td><td>3016</td><td>.</td><td>-</td><td>.</td><td>genegrp=CG3038; transgrp=CG3038-RB; name=CG3038:2</td></tr>
 * </table>
 *
 * @author  Stephen Fisher
 * @version $Id: GFFTrack.java,v 1.1.2.23 2007/02/22 21:10:27 fisher Exp $
 */

public class GFFTrack implements TrackFile {

	 private final int ID = FileIO.GFF;
	 private final String DESC = "GFF files (*.gff)";
	 private final String[] EXT = {".gff"};
	 private final FileFilter fileFilter = new GFFFilter();

    //--------------------------------------------------------------------------
    // Setters and Getters

	 public int getID() { return ID; }

	 public String getDesc() { return DESC; }

	 public String[] getExt() { return EXT; }

	 public FileFilter getFileFilter() { return fileFilter; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * Load all Features in the GFF file into a single Track and
	  * return the resulting Track object.  
	  */
	 public Track load(String filename) {
		  return load(filename, "");
	 }

	 /**
	  * Load all Features in the GFF file into a single Track and
	  * return the resulting Track object.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public Track load(String filename, String sourceID) {
		  // when creating Track's ID, if necessary, remove ".gff"
		  // filename extension.  Use File() to remove any path info
		  // from the filename so that the ID is just the name.
		  File file = new File(filename);
		  String id = file.getName();
		  if (id.endsWith(".gff")) id = id.substring(0, id.length()-4);


		  Track track = new Track(false, id);
		  Sequence source = null;
		  if (! GloDBUtils.isEmpty(sourceID)) {
				source = ObjectHandles.getSequence(sourceID);
				if (source == null) {
					 String msg = "The source ID \"" + sourceID + "\" isn't valid.";
					 msg += "Source IDs will be set from the feature headers.";
					 GloDBUtils.printMsg(msg, GloDBUtils.WARNING);
				}
		  }

		  try {
				BufferedReader bReader = new BufferedReader(new FileReader(file));
				String line;

				StringBuffer attributes = new StringBuffer();
				while ((line = bReader.readLine()) != null) {
					 line = line.trim();

					 // skip all comment ('#') lines and lines that are
					 // empty. Don't use GloDBUtils.isEmpty() because it's
					 // redundent processing (trims and tests for null).
					 if ((! line.startsWith("#")) && (line.length() > 0)) {
						  // split line at every tab ('\t')
						  String[] fields = line.split("	");
						  
						  // get a reference to the Sequence for this
						  // Feature.  If source already exists then use
						  // that, else try the first field ('seqName').
						  Sequence seqRef;
						  if (source == null) {
								seqRef = ObjectHandles.getSequence(fields[0]);
								if (seqRef == null) {
									 if (true) {
										  seqRef = new Sequence(fields[0]);
										  GloDBUtils.printWarning("Sequence not found, so created empty Sequence with ID: " 
																		 + fields[0]);
									 } else {
										  GloDBUtils.printError("Skipping feature because sequence not found: " 
																	  + fields[0]);
										  continue;
									 }
								}
						  } else {
								seqRef = source;
						  }

						  // create a new Feature object
						  Feature feature = new ExactFeature(Integer.parseInt(fields[3]), 
																		 Integer.parseInt(fields[4]), seqRef); 

						  // get Feature attributes
						  attributes.setLength(0); // erase buffer
						  attributes.append("source=" + fields[1]);     // get source
						  attributes.append(";feature=" + fields[2]);   // get feature label
						  attributes.append(";score=" + fields[5]);     // get score
						  attributes.append(";strand=" + fields[6]);    // get strand
						  attributes.append(";frame=" + fields[7]);     // get frame
						  if (fields.length > 8) {                // get attributes
								// this will contain tag/value pairs. Since we
								// use ';' as key/value delimiter, we need to
								// make sure fields[8] doesn't also contain
								// this delimiter.
								attributes.append(";attributes=" + fields[8].replace(';', ','));
								/*
								StringTokenizer tokens = new StringTokenizer(fields[8], ";");

								while (tokens.hasMoreTokens()) {
									 String attrib = tokens.nextToken().trim();
									 String[] key_value = attrib.split(" ", 2);

									 if (key_value.length > 1) {
										  attributes.put(key_value[0], key_value[1]);
									 } else {
										  // test if uses '=' as delimiter
										  // instead of ' '
										  key_value = attrib.split("=", 2);
										  if (key_value.length > 1) {
												attributes.put(key_value[0], key_value[1]);
										  } else {
												// still can't parse the
												// attributes, so add all
												// attributes as one item
												attributes.put("attributes", fields[8]);
												break;
										  }
									 }
								}
								*/
						  }
						  feature.setAttributes(attributes.toString());

						  // add the Feature object to the Track
						  track.addFeature(feature);
					 }
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

		  GloDBUtils.printMsg("Loaded GFF file: " + filename);
		  return track;
	 }

	 /** 
	  * Save the Track to a file based on it's ID.  This will overwrite
	  * any existing file.  This will append ".gff" to the filename.
	  */
	 public void save(String id) {
		  // add ".gff" filename extension, if necessary
		  String filename = id;
		  if (! filename.endsWith(".gff")) filename += ".gff";

		  save(id, filename, true);
	 }

	 /**
	  * Save all Features in a GFF file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  * @XXX Should offer option to include Sequence data.
	  */
	 public void save(String id, String filename, boolean overwrite) {
		  // if empty filename, then exit
		  if (filename.length() == 0) return;

		  // add ".gff" filename extension, if necessary
		  if (! filename.endsWith(".gff")) filename += ".gff";

		  File file = new File(filename);
		  // if the file already exists and not supposed to overwrite
		  // it, then return on error.
		  if (file.exists() && (! overwrite)) {
				GloDBUtils.printError("File \"" + filename + "\" already exists.");
				return;
		  }

		  // counters for potential errors in output file
		  int featureLabelErrors = 0;
		  int strandLabelErrors = 0;

		  try {
				Track track = ObjectHandles.getTrack(id);
				if (track == null) {
					 GloDBUtils.printError("Track \"" + id + "\" doesn't exist.");
					 return;
				}

				FileWriter fWriter = new FileWriter(file);
				BufferedWriter bWriter = new BufferedWriter(fWriter);

				for (Iterator i = track.featureIterator(); i.hasNext();) {
					 Feature feature = (Feature) i.next();

					 // create a copy of the attributes so we can remove
					 // objects from the HashMap as we process them below
					 HashMap attribs = feature.getAttributesMap();

					 // add sequence ID
					 String line = feature.getSourceID();

					 // add source info
					 if (attribs.containsKey("source")) {
						  line += "\t" + attribs.get("source");
						  attribs.remove("source");
					 } else {
						  // if no source attribute then we are the source
						  line += "\tGloDB";
					 }						  

					 // add feature label
					 if (attribs.containsKey("feature")) {
						  line += "\t" + attribs.get("feature");
						  attribs.remove("feature");
					 } else {
						  // if no feature attribute then use the track ID.
						  // XXX this is probably not correct
						  line += "\t" + id;
						  featureLabelErrors++;
					 }						  

					 // add start/stop info
					 line += "\t" + feature.getStart();
					 line += "\t" + feature.getStop();

					 // add score info
					 if (attribs.containsKey("score")) {
						  line += "\t" + attribs.get("score");
						  attribs.remove("score");
					 } else {
						  // if no score attribute then use '.'
						  line += "\t.";
					 }						  

					 // add strand info
					 if (attribs.containsKey("strand")) {
						  line += "\t" + attribs.get("strand");
						  attribs.remove("strand");
					 } else {
						  // if no strand attribute then use '.'
						  // XXX this is probably not correct
						  line += "\t+";
						  strandLabelErrors++;
					 }						  

					 // add frame info
					 if (attribs.containsKey("frame")) {
						  line += "\t" + attribs.get("frame");
						  attribs.remove("frame");
					 } else {
						  // if no frame attribute then use '.'
						  line += "\t.";
					 }						  

					 // add attributes info
					 if (attribs.containsKey("attributes")) {
						  line += "\t" + attribs.get("attributes");
						  attribs.remove("attributes");
					 }						  

					 // add remaining attributes
					 for (Iterator l = (attribs.keySet()).iterator(); l.hasNext();) {
						  String key = (String) l.next();
						  line += "; " + key + " " + attribs.get(key);
					 }

					 bWriter.write(line);
					 bWriter.newLine();
				}

				bWriter.flush();
				bWriter.close();
		  } catch (FileNotFoundException e) {
				// problem with FileOutputStream
				GloDBUtils.printError("File \"" + filename + "\" can not be opened.");
		  } catch (IOException e) {
				// problem with ObjectOutputStream.  XXX do we need to
				// close bWriter()?
				GloDBUtils.printError("Error writting output file \"" + filename + "\".");
		  }

		  if (featureLabelErrors > 0) {
				String msg = "Number of feature labels not found: " + featureLabelErrors + "\n";
				msg += "     Used \"" + id + "\" instead.";
				GloDBUtils.printError(msg);
		  }

		  if (strandLabelErrors > 0) {
				String msg = "Strand attribute not found: " + strandLabelErrors + "\n";
				msg += "     Used \"+\" instead.";
				GloDBUtils.printError(msg);
		  }
	 }

	 /** Format all Features into a GFF like string. */
	 public String format(String id) {
		  // counters for potential errors in output file
		  int featureLabelErrors = 0;
		  int strandLabelErrors = 0;

		  Track track = ObjectHandles.getTrack(id);
		  if (track == null) {
				GloDBUtils.printError("Not a valid track");
				return "";
		  }

		  String out = "";

		  for (Iterator i = track.featureIterator(); i.hasNext();) {
				Feature feature = (Feature) i.next();

				// create a copy of the attributes so we can remove
				// objects from the HashMap as we process them below
				HashMap attribs = feature.getAttributesMap();
				
				// add sequence ID
				String line = feature.getSourceID();
				
				// add source info
				if (attribs.containsKey("source")) {
					 line += "\t" + attribs.get("source");
					 attribs.remove("source");
				} else {
					 // if no source attribute then we are the source
					 line += "\tGloDB";
				}						  
				
				// add feature label
				if (attribs.containsKey("feature")) {
					 line += "\t" + attribs.get("feature");
					 attribs.remove("feature");
				} else {
					 // if no feature attribute then use the track ID.
					 // XXX this is probably not correct
					 line += "\t" + id;
					 featureLabelErrors++;
				}						  
				
				// add start/stop info
				line += "\t" + feature.getStart();
				line += "\t" + feature.getStop();
				
				// add score info
				if (attribs.containsKey("score")) {
					 line += "\t" + attribs.get("score");
					 attribs.remove("score");
				} else {
					 // if no score attribute then use '.'
					 line += "\t.";
				}						  
				
				// add strand info
				if (attribs.containsKey("strand")) {
					 line += "\t" + attribs.get("strand");
					 attribs.remove("strand");
				} else {
					 // if no strand attribute then use '.'
					 // XXX this is probably not correct
					 line += "\t+";
					 strandLabelErrors++;
				}						  
				
				// add frame info
				if (attribs.containsKey("frame")) {
					 line += "\t" + attribs.get("frame");
					 attribs.remove("frame");
				} else {
					 // if no frame attribute then use '.'
					 line += "\t.";
				}						  
				
				// add attributes info
				if (attribs.containsKey("attributes")) {
					 line += "\t" + attribs.get("attributes");
					 attribs.remove("attributes");
				}						  
				
				// add remaining attributes
				for (Iterator l = (attribs.keySet()).iterator(); l.hasNext();) {
					 String key = (String) l.next();
					 line += "; " + key + " " + attribs.get(key);
				}
				
				out += line + "\n";
		  }
		  
		  if (featureLabelErrors > 0) {
				String msg = "Number of feature labels not found: " + featureLabelErrors + "\n";
				msg += "     Used \"" + id + "\" instead.";
				GloDBUtils.printError(msg);
		  }

		  if (strandLabelErrors > 0) {
				String msg = "Strand attribute not found: " + strandLabelErrors + "\n";
				msg += "     Used \"+\" instead.";
				GloDBUtils.printError(msg);
		  }

		  return out;
	 }

	 /** 
	  * GFF specific FileFilter. 
	  * @XXX This should use EXT.
	  */
	 private class GFFFilter extends FileFilter {
		  public boolean accept(File f) {
				// accept directories
				if (f.isDirectory()) return true;
				
				// if true, then don't filter by file extensions.
				if (GUIUtils.showAllFiles()) return true;

				// accept files ending in '.gff'
				if ((f.getName()).endsWith(".gff")) return true;

				return false;
		  }
		  
		  // set the filter's description
		  public String getDescription() { return DESC; }
	 }

} // GFFTrack.java

	 
