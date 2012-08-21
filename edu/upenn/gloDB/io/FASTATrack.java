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
 * @(#)FASTATrack.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.gui.GUIUtils;
import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.text.NumberFormat;
import javax.swing.filechooser.FileFilter;

/**
 * Import Track data from a FASTA file.  The basic file format
 * dictates a header line at the beginning of each Feature.  There
 * are no standards as to what the header line should contain or how
 * it should be formatted, other than to stipulate that it begins with
 * a ">".  Thus this format sufficient for coding Sequence objects but
 * not ideal for sequence annotations (Tracks).  Since some sites,
 * such as www.fruitfly.org, release annotations as FASTA files, some
 * attempt has been made to parse the headers from specific sites.
 * Users can use the FASTAParser interface to create their own header
 * parsers as well.  Here the default is FASTAParserFly.
 *
 * @XXX Can we assume that the header starts with a Sequence ID?
 *
 * @XXX SaveTrack() looks for 'ID', 'descriptors', 'dbxref',
 * 'strand', 'source', and 'boundaries' in the Feature attributes and
 * processes these uniquely.  In particular, 'boundaries' is discarded
 * because it's assumed to be the same as Feature.start and
 * Feature.stop.  If 'source' is also discarded if it's the same as
 * Feature.getSource().getID().  'strand' is used in creating
 * 'gene_boundaries' and similarly discarded.  the 'descriptors' and
 * 'dbxref' labels are not included in the output, but their HashMap
 * values are included.
 *
 * @author  Stephen Fisher
 * @version $Id: FASTATrack.java,v 1.1.2.21 2007/03/01 21:17:33 fisher Exp $
 */

public class FASTATrack implements TrackFile {

	 private final int ID = FileIO.FASTA;
	 private final String DESC = "FASTA files (*.fa; *.fas; *.fasta)";
	 private final String[] EXT = {".fa", ".fas", ".fasta"};
	 private final FileFilter fileFilter = new FASTAFilter();

    //--------------------------------------------------------------------------
    // Setters and Getters

	 public int getID() { return ID; }

	 public String getDesc() { return DESC; }

	 public String[] getExt() { return EXT; }

	 public FileFilter getFileFilter() { return fileFilter; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * Load all Features in the FASTA file into a single Track and
	  * return the resulting Track object.  If possible, a Sequence
	  * object will be loaded/created for each Feature from the FASTA
	  * file.
	  */
	 public Track load(String filename) {
		  return load(filename, "");
	 }

	 /**
	  * Load all Features in the FASTA file into a single Track and
	  * return the resulting Track object.  If a Sequence is given,
	  * then that will be used as the source file all Features in the
	  * file, otherwise a Sequence object will be loaded/created for
	  * each Feature from the FASTA file.  
	  *
	  * The header is parsed using {@link FASTAParserFly
	  * FASTAParserFly}.  An {@link ExactFeature ExactFeature} object
	  * is created with the start and stop positions taken from the
	  * "boundaries" key:value pair.  The parsed header is stored in
	  * the {@link AbstractFeature#attributes
	  * AbstractFeature.attributes} field of the {@link ExactFeature
	  * ExactFeature} object.
	  *
	  * If the file is empty then returns 'null'.
	  *
	  * If this can't get a valid Sequence ID from the user or the
	  * Feature's header, then can't be associated with any existing
	  * Sequence and so this will create a Sequence with it's best
	  * guess at the Sequence ID.  However, this isn't very useful
	  * because it's not likely that other Features will share this
	  * Sequence.  There's also no capacity to load this Sequence data
	  * later, so the Sequence data is load here as well, which is very
	  * inefficient.
	  *
	  * @XXX When skipping a Feature because the Sequence data loaded
	  * doesn't contain the correct range, should we discard the loaded
	  * Sequence or leave it in the sequencePool?
	  * @XXX I'm not sure how the position information is formatted.
	  * @XXX Need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public Track load(String filename, String sourceID) {
		  // when creating Track's ID, remove ".fasta" or ".fas" or
		  // ".fa" filename extension, if present.  Use File() to remove
		  // any path info from the filename so that the ID is just the
		  // name.
		  File file = new File(filename);
		  String id = file.getName();
		  if (id.endsWith(".fasta")) id = id.substring(0, id.length()-6);
		  else if (id.endsWith(".fas")) id = id.substring(0, id.length()-4);
		  else if (id.endsWith(".fa")) id = id.substring(0, id.length()-3);

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

				boolean validFeature = false;
				boolean loadSequenceData = false;

				Feature cFeature = null;  // current Feature
				Sequence seq = null;
				String data = "";  // store sequence data as read from file

				while ((line = bReader.readLine()) != null) {
					 // skip empty lines
					 if (GloDBUtils.isEmpty(line)) continue;

					 // test for Sequence header
					 if (line.startsWith(">")) {
						  if (validFeature) {
								if (loadSequenceData) { 
									 // we need to save the Sequence/contig data
									 seq.setData(data);

									 // reset the Sequence flag because starting to
									 // read in a new Feature
									 loadSequenceData = false;
						  
									 // make sure that the Feature is valid; that is,
									 // if the sequence loaded isn't large enough to
									 // encompass the Feature we won't include the
									 // Feature
									 if (seq.contains(cFeature)) {
										  // valid Feature, so add to Track
										  track.addFeature(cFeature);
									 } else {
										  String msg = "Skipping record because the sequence loaded (" + seq.getID() + ") doesn't encompass the entire feature: \n";
										  msg += cFeature.getAttributes();
										  GloDBUtils.printError(msg);
									 }
								} else {
									 // not first Feature so add existing
									 // Feature before reseting the variables
									 // for the next Feature.  We can assume
									 // this is a valid Feature.
									 track.addFeature(cFeature);
								}
						  }

						  // we assume this will be a valid Feature
						  validFeature = true;
								
						  // assume all features are "exact", thus just have start and
						  // stop positions.
						  int start = 0;
						  int stop = 0;
						  HashMap attributes;

						  // reset Sequence info
						  seq = source;
						  data = "";

						  // process source/boundaries (was call
						  // "gene_boundaries" in the FASTA file) here,
						  // creating a Feature.

						  // XXX: what is the format for the position
						  // information?  can a track have more than one
						  // position pair?  for example one pair looks like
						  // this: (X:1,488..3,280[-]) and can there be
						  // cases with more than one pair such as something
						  // like this: (X:1,488..3,280;4,453..6,654[-])
						  FASTAParser parser = new FASTAParserFly();
						  attributes = parser.parseHeader(line);

						  if (! attributes.containsKey("boundaries")) {
								String msg = "Skipping record because no feature information in header: \n";
								msg += line;
								GloDBUtils.printError(msg);
								validFeature = false; // not valid Feature
								continue;
						  }
						  String boundaries = (String) attributes.get("boundaries");

						  // get the Sequence ID and start/stop positions
						  //						  String pos[] = boundaries.split(":", 2);
						  //						  sourceID = pos[0];
						  // XXX: This assumes that these Features do NOT
						  // have more than one position pair.
						  //						  pos = pos[1].split("\\.\\.");
						  String pos[] = boundaries.split("\\.\\.");

						  // add position information to the Feature object.  the
						  // positions have commas and thus need to be 'parsed' and not
						  // just converted from String to Integer.
						  NumberFormat nf = NumberFormat.getNumberInstance();
						  try {
								start = (nf.parse(pos[0])).intValue();
								stop = (nf.parse(pos[1])).intValue();
						  } catch (Exception e) {
								String msg = "Skipping record because unable to parse feature position information: \n";
								msg += line;
								GloDBUtils.printError(msg);
								validFeature = false; // not valid Feature
								continue;
						  }

						  if (seq == null) {
								// we don't have user-specified source info,
								// so check the "gene_boundaries" Sequence ID
								// to see if it's valid
								if (attributes.containsKey("source")) {
									 sourceID = (String) attributes.get("source");
									 seq = ObjectHandles.getSequence(sourceID); 
								}
									 
								if ((seq == null) || 
									 (! seq.contains(start)) || (! seq.contains(stop))) {
									 // still haven't found a valid Sequence
									 // for this Feature, so check the ID at
									 // the beginning of the header line.  The
									 // ID was parsed by FASTAParserFly and
									 // added as an attribute.
									 if (attributes.containsKey("ID")) {
										  sourceID = (String) attributes.get("ID");
										  
										  // we need to test if new source ID is valid
										  seq = ObjectHandles.getSequence(sourceID);
									 }
									 
									 if ((seq == null) || 
										  (! seq.contains(start)) || (! seq.contains(stop))) {
										  // XXX If still no source info, then need
										  // to load the source info from the file,
										  // creating a new Sequence object.  If
										  // sourceID is empty, then a random ID
										  // will be created in Sequence().

										  // If we've gotten this far then the
										  // Feature can't be associated with
										  // any existing Sequence and so we're
										  // creating a Sequence here.  However,
										  // this isn't very useful because it's
										  // not likely that other Features will
										  // share this Sequence.  There's also
										  // no capacity to load this Sequence
										  // data later, so we are going to have
										  // to load it now as well, which is
										  // very inefficient.
										  try {
												GloDBUtils.printMsg("Source \"" + sourceID + "\" doesn't exist, loading sequence data.");
												seq = new Sequence(sourceID);
												seq.setAttributes(attributes);
										  } catch (InvalidIDException e) {
												// This shoud never be reached but
												// is here just in case something
												// goes wrong above.
												String newID = Sequence.randomID("_S");
												String msg = "Source \"" + sourceID + "\" already exists, using ID \"" 
													 + newID + "\" instead.";
												GloDBUtils.printMsg(msg, GloDBUtils.WARNING);
												seq = new Sequence(newID);
												seq.setAttributes(attributes);
										  }
										  
										  // use the Feature's start position
										  // as the offset position for the
										  // Sequence data

										  // XXX we should shift the feature
										  // start/stop postions to go from 0 to
										  // (length-offset).
										  seq.setOffset(start);

										  // at this point we need to load the
										  // Sequence data from the input file.
										  loadSequenceData = true;
									 }
								}
						  }

						  // starting a new Feature.  'Seq' is either set
						  // to 'sourceID', as provided when this method was
						  // called, or to the Sequence contig loaded from
						  // this FASTA file.
						  cFeature = new ExactFeature(start, stop, seq); 
						  // add attributes to Feature object
						  cFeature.setAttributes(attributes);
					 } else {
						  // if necessary, load Sequence/contig data
						  if (loadSequenceData) data += line;
					 }
				}

				// add last Feature's info
				if (validFeature && (cFeature != null)) {
					 if (loadSequenceData) { 
						  // we need to save the Sequence/contig data
						  seq.setData(data);

						  // make sure that the Feature is valid; that is,
						  // if the sequence loaded isn't large enough to
						  // encompass the Feature we won't include the
						  // Feature
						  if (seq.contains(cFeature)) {
								// valid Feature, so add to Track
								track.addFeature(cFeature);
						  } else {
								String msg = "Skipping record because the sequence loaded (" + seq.getID() + ") doesn't encompass the entire feature: \n";
								msg += cFeature.getAttributes();
								GloDBUtils.printError(msg);
						  }
					 } else {
						  // valid Feature, so add to Track
						  track.addFeature(cFeature);
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

		  GloDBUtils.printMsg("Loaded FASTA file: " + filename);
		  return track;
	 }

	 /** 
	  * Save the Track to a file based on it's ID.  This will overwrite
	  * any existing file.  This will append ".fasta" to the filename.
	  */
	 public void save(String id) {
		  // add ".fasta" filename extension, if necessary
		  String filename = id;
		  if ((! filename.endsWith(".fa")) && (! filename.endsWith(".fas")) 
				&& (! filename.endsWith(".fasta"))) {
				filename += ".fasta";
		  }

		  save(id, filename, true);
	 }

	 /**
	  * Save all Features in a FASTA file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  * @XXX How should the attributes be formatted?  Should we remove
	  * 'ID', 'descriptors', 'dbxref', 'strand', 'source', and
	  * 'boundaries' from the header since these were most likely added
	  * when we created the header?
	  */
	 public void save(String id, String filename, boolean overwrite) {
		  // add ".fasta" filename extension, if necessary
		  if ((! filename.endsWith(".fa")) && (! filename.endsWith(".fas")) 
				&& (! filename.endsWith(".fasta"))) {
				filename += ".fasta";
		  }

		  File file = new File(filename);
		  // if the file already exists and not supposed to overwrite
		  // it, then return on error.
		  if (file.exists() && (! overwrite)) {
				GloDBUtils.printError("File \"" + filename + "\" already exists.");
				return;
		  }

		  try {
				Track track = ObjectHandles.getTrack(id);
				if (track == null) {
					 GloDBUtils.printError("Track \"" + id + "\" doesn't exist.");
					 return;
				}

				FileWriter fWriter = new FileWriter(file);
				BufferedWriter bWriter = new BufferedWriter(fWriter);

				for (Iterator s = track.getSourceSet().iterator(); s.hasNext();) {
					 String sequenceID = (String) s.next();
					 Sequence sequence = (Sequence) ObjectHandles.sequencePool.get(sequenceID);

					 // get sequence data for this source
					 String seqData = sequence.getData();  
					 int offset = sequence.getOffset();

					 for (Iterator i = track.featuresBySource(sequenceID).iterator(); i.hasNext();) {
						  Feature feature = (Feature) i.next();

						  // XXX should include more formatting
						  String header = ">";
						  
						  // create a copy of the attributes so we can remove
						  // objects from the HashMap as we process them below
						  HashMap attribs = feature.getAttributesMap();
						  
						  // start with the ID attribute, if not present, then
						  // use the Track's ID.
						  if (attribs.containsKey("ID")) {
								header += attribs.get("ID");
								attribs.remove("ID");
						  } else {
								header += id;
						  }
						  
						  // if contains 'descriptors' then remove label
						  if (attribs.containsKey("descriptors")) {
								header += " " + attribs.get("descriptors");
								attribs.remove("descriptors");
						  }
						  
						  // if "gene_boundaries" already exists, then we
						  // probably didn't process this header and so we
						  // should just leave it alone
						  if (! attribs.containsKey("gene_boundaries")) {
								String gb = "gene_boundaries:(" + feature.getSource().getID() + ":";
								gb += feature.getStart() + ".." + feature.getStop();
								
								// if 'source' already handled, then remove from
								// attribs map
								if (attribs.containsKey("source")) {
									 String value = (String) attribs.get("source");
									 //								if (value.equalsIgnoreCase(feature.getSource().getID())) {
									 if (value.equals(feature.getSource().getID())) {
										  attribs.remove("source");
									 }
								}
								
								// if 'boundaries' exists then remove from attribs
								// map, because this should be equivalent to the
								// Feature's start/stop
								attribs.remove("boundaries");
								
								if (attribs.containsKey("strand")) {
									 gb += "[" + (String) attribs.get("strand") + "]";
									 attribs.remove("strand");  // don't need anymore
								}
								gb += ")";
								header += " " + gb;
						  }
						  
						  // if contains 'dbxref' then remove label and enclose
						  // in '()'
						  if (attribs.containsKey("dbxref")) {
								header += " (";
								Set dbxref = (Set) attribs.get("dbxref");
								for (Iterator dI = dbxref.iterator(); dI.hasNext();) {
									 header += dI.next();
								}
								header += ")";
								attribs.remove("dbxref");
						  }
						  
						  // add remaining attributes to the header
						  for (Iterator l = (attribs.keySet()).iterator(); l.hasNext();) {
								String key = (String) l.next();
								header += " " + key + ":" + attribs.get(key);
						  }
						  
						  bWriter.write(header);
						  bWriter.newLine();
						  
						  // if offset = 0, then not set so need to adjust for
						  // sequence starting at 1 and String starting at 0.  If
						  // offset is set, then 
						  int start = feature.getStart();
						  int stop = feature.getStop();
						  if (offset == 0) {
								start -= 1;
						  } else {
								start = start - offset;
								stop = (stop - offset) + 1;
						  }

						  // output to file with Sequence.FORMAT_WIDTH
						  // characters per line
						  String boundedData = seqData.substring(start, stop);
						  int dataLen = boundedData.length();
						  int idx = Sequence.FORMAT_WIDTH;
						  while (idx < dataLen) {
								bWriter.write(boundedData.substring(idx - Sequence.FORMAT_WIDTH, idx) + "\n");
								idx += Sequence.FORMAT_WIDTH;
						  }
						  if (idx >= dataLen) bWriter.write(boundedData.substring(idx - Sequence.FORMAT_WIDTH, 
																									 dataLen));
						  bWriter.newLine();
					 }
				}

				bWriter.newLine();
				bWriter.flush();
				bWriter.close();
		  } catch (FileNotFoundException e) {
				// problem with FileOutputStream
				GloDBUtils.printError("File \"" + filename + "\" can not be opened.");
		  } catch (IOException e) {
				// problem with ObjectOutputStream.  XXX do we need to
				// close 'oStream'?
				GloDBUtils.printError("Error writting output file \"" + filename + "\".");
		  }
	 }

	 /** 
	  * FASTA specific FileFilter. 
	  * @XXX This should use EXT.
	  */
	 private class FASTAFilter extends FileFilter {
		  public boolean accept(File f) {
				// accept directories
				if (f.isDirectory()) return true;
				
				// if true, then don't filter by file extensions.
				if (GUIUtils.showAllFiles()) return true;

				// accept files ending in '.fasta' or '.fas' or '.fa'
				if ((f.getName()).endsWith(".fasta")) return true;
				if ((f.getName()).endsWith(".fas")) return true;
				if ((f.getName()).endsWith(".fa")) return true;

				return false;
		  }
		  
		  // set the filter's description
		  public String getDescription() { return DESC; }
	 }

} // FASTATrack.java

	 
