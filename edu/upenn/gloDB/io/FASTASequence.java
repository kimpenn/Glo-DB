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
 * @(#)FASTASequence.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.gui.GUIUtils;
import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import javax.swing.filechooser.FileFilter;

/**
 * Import Sequence data from a FASTA file.  The basic file format
 * dictates a header line at the beginning of each sequence.  There
 * are no standards as to what the header line should contain or how
 * it should be formatted, other than to stipulate that it begins with
 * a ">".  Thus this format sufficient for coding Sequence objects but
 * not ideal for sequence annotations (Features).  Since some sites,
 * such as www.fruitfly.org, release annotations as FASTA files, some
 * attempt has been made to parse the headers from specific sites.
 * Users can use the FASTAParser interface to create their own header
 * parsers as well.
 *
 * @XXX can we assume that the header starts with a Sequence ID?
 *
 * @author  Stephen Fisher
 * @version $Id: FASTASequence.java,v 1.31.2.16 2007/03/01 21:17:33 fisher Exp $
 */

public class FASTASequence implements SequenceFile, SequenceLoader {

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
	  * Return the Sequence data as a String.  This assumes a single
	  * Sequence per file.
	  *
	  * @XXX This should allow for FASTA files with more than one
	  * Sequence -- can use headers to find correct data.
	  */
	 public String getData(HashMap loaderArgs) {
		  String filename = (String) loaderArgs.get("filename");
		  if (GloDBUtils.isEmpty(filename)) {
				GloDBUtils.printError("Empty file name, can't load source data.");
				return "";
		  }

		  try {
				BufferedReader bReader = new BufferedReader(new FileReader(filename));

				String line;

				// load sequence header
				if ((line = bReader.readLine()) != null) {
					 // make sure header exists
					 if (line.startsWith(">")) {
						  GloDBUtils.printMsg("Loading: " + filename);
					 } else {
						  GloDBUtils.printError("File does not contain a header: " + filename);
						  bReader.close();
						  return null;
					 }
				} else {
					 GloDBUtils.printError("Empty file: " + filename);
					 bReader.close();
					 return null;
				}


				int fileLength = (int) (new File(filename)).length();

				// Send output to the command line because the GUI hangs
				// during the loading.
				System.out.print("Working:...");
				int cnt = 0;
				int interval = (fileLength) / 500;
				if (interval == 0) interval = 1;

				// load sequence data. We use a StringBuffer for the
				// loading because this entails a lot of concatinations
				// which are very slow to perform on String objects but
				// very fast for StringBuffers.
				StringBuffer sb = new StringBuffer(fileLength);
				while ((line = bReader.readLine()) != null) {
					 // stop if reach another sequence
					 if (line.startsWith(">")) break;
					 sb.append(line);
					 if ((cnt++ % interval) == 0) System.out.print(".");
				}
				System.out.println("");

				bReader.close();
				return sb.toString();
		  } catch (FileNotFoundException e) {
				GloDBUtils.printError("File not found: " + e.getMessage());
				return null;
		  } catch (IOException e) {
				GloDBUtils.printError("Error reading file: " + filename);
				return null;
		  }
	 }

	 /**
	  * Load the first sequence in the FASTA file and return the
	  * resulting Sequence object.
	  */
	 public Sequence load(String filename) {
		  return load(filename, "", new FASTAParserMinimal());
	 }

	 /**
	  * Load the first sequence in the FASTA file and return the
	  * resulting Sequence object.
	  */
	 public Sequence load(String filename, String id) {
		  return load(filename, id, new FASTAParserMinimal());
	 }

	 /**
	  * Load the first sequence in the FASTA file and return the
	  * resulting Sequence object.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public Sequence load(String filename, String id, FASTAParser parser) {
		  File file = new File(filename);

		  if (id.length() == 0) {
				// Need to create Sequence's ID. Remove ".fasta" or ".fas"
				// or ".fa" filename extension, if present.  Use File() to
				// remove any path info from the filename so that the ID
				// is just the name.
				id = file.getName();
				if (id.endsWith(".fasta")) id = id.substring(0, id.length()-6);
				else if (id.endsWith(".fas")) id = id.substring(0, id.length()-4);
				else if (id.endsWith(".fa")) id = id.substring(0, id.length()-3);
		  }

		  Sequence sequence = new Sequence(id); 
		  //		  String data = "";  // store sequence data as read from file.

		  try {
				BufferedReader bReader = new BufferedReader(new FileReader(file));

				String line;

				// load sequence header
				if ((line = bReader.readLine()) != null) {
					 // make sure header exists
					 if (line.startsWith(">")) {
						  GloDBUtils.printMsg("Loading: " + filename);
						  sequence.setAttributes(parser.parseHeader(line));
						  // setup parameters to load data later, if necessary
						  sequence.setDataLoader(this);
						  HashMap loaderArgs = new HashMap();
						  loaderArgs.put("filename", filename);
						  sequence.setLoaderArgs(loaderArgs);
					 } else {
						  // since didn't correctly load file, remove the
						  // Sequence we just created
						  ObjectHandles.removeSequence(sequence);  

						  GloDBUtils.printError("File doesn't contain a header: " + filename);
						  bReader.close();
						  return null;
					 }
				} else {
					 // since didn't correctly load file, remove the
					 // Sequence we just created
					 ObjectHandles.removeSequence(sequence);  

					 GloDBUtils.printError("Empty file: " + filename);
					 bReader.close();
					 return null;
				}

				/*
				// load sequence data
				while ((line = bReader.readLine()) != null) {
					 // stop if reach another sequence
					 if (line.startsWith(">")) break;
					 data += line;
				}
				sequence.setData(data);
				*/

				bReader.close();
		  } catch (FileNotFoundException e) {
				GloDBUtils.printError("File not found: " + e.getMessage());
				return null;
		  } catch (IOException e) {
				GloDBUtils.printError("Error reading file: " + filename);
				return null;
		  }

		  return sequence;
	 }

	 /**
	  * Load all Sequences in the FASTA file and return a Set
	  * containing the resulting Sequence objects.  
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public HashSet loadAll(String filename) {
		  return loadAll(filename, new FASTAParserMinimal());
	 }

	 /**
	  * Load all Sequences in the FASTA file and return a set
	  * containing the resulting Sequence objects.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public HashSet loadAll(String filename, FASTAParser parser) {
		  HashSet sequences = new HashSet();  // set of loaded sequences

		  // make sure we have at least a minimal parser
		  if (parser == null) parser = new FASTAParserMinimal();

		  try {
				BufferedReader bReader = new BufferedReader(new FileReader(filename));

				String line;
				boolean firstSequence = true;

				Sequence seq = null;
				//				String data = "";  // store sequence data as read from file.

				while ((line = bReader.readLine()) != null) {
					 // test for a sequence header
					 if (line.startsWith(">")) {
						  if (! firstSequence) {
								// not first sequence so append existing sequence before
								// reseting the variables for the next sequence.
								//								seq.setData(data);
								sequences.add(seq);
						  } else {
								firstSequence = false;
						  }
								
						  HashMap attributes = parser.parseHeader(line);
						  String id = (String) attributes.get("ID");
						  if (id == null) id = "";
						  seq = new Sequence(id); 
						  seq.setAttributes(attributes);
						  // setup parameters to load data later, if necessary
						  seq.setDataLoader(this);
						  HashMap loaderArgs = new HashMap();
						  loaderArgs.put("filename", filename);
						  seq.setLoaderArgs(loaderArgs);

						  /*
							 // THIS CODE REMOVED BECAUSE IT SETS ID AFTER CREATING SEQUENCE
							 // CURRENTLY NOT ALLOWING RENAMING OF SEQUENCES
						  // starting a new sequence
						  seq = new Sequence(); 
						  seq.setAttributes(parser.parseHeader(line));
						  // setup parameters to load data later, if necessary
						  seq.setDataLoader(this);
						  HashMap loaderArgs = new HashMap();
						  loaderArgs.put("filename", filename);
						  seq.setLoaderArgs(loaderArgs);
						  try {
								String id = (String) seq.getAttribute("ID");

								if (id != null) seq.setID(id);
								else seq.setID(Sequence.randomID("_S"));
						  } catch (InvalidIDException e) {
								String id = Sequence.randomID("_S");
								String msg = "WARNING: ID \"" + seq.getAttribute("ID")
									 + "\" already exists, using ID \"" + id + "\" instead.";
								GloDBUtils.printMsg(msg);
								seq.setID(id);
						  }
						  */

						  //						  data = "";  // store sequence data as read from file.
					 } else {
						  // load sequence data
						  //						  data += line;
					 }
				}
				
				if (seq != null) {
					 // add last sequences info
					 //					 seq.setData(data);
					 sequences.add(seq);
				}

				bReader.close();
		  } catch (FileNotFoundException e) {
				GloDBUtils.printError("File not found: " + e.getMessage());
				return null;
		  } catch (IOException e) {
				// XXX we should probably remove the sequences from the
				// sequence pool here
				GloDBUtils.printError("Error reading file: " + filename);
				return null;
		  }

		  return sequences;
	 }

	 /** 
	  * Save the Seqeuence to a file based on it's ID.  This will
	  * overwrite any existing file.  This will append ".fasta" to the
	  * filename.
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
	  * Save the Sequence data.  This will make sure the data is loaded
	  * prior to saving the Sequence.
	  */
	 public void save(String id, String filename, boolean overwrite) {
		  GloDBUtils.printMsg("Saving 'FASTA' sequence files not yet supported.");
	 }

	 public String toString() { return "FASTA File Loader"; }

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

} // FASTASequence.java

	 
