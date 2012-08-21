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
 * @(#)GUIUtils.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import edu.upenn.gloDB.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Static methods used throughout the GUI.
 *
 * @XXX The global file type descriptions and extensions be user
 * modifiable.
 *
 * @author  Stephen Fisher
 * @version $Id: GUIUtils.java,v 1.17.2.21 2007/03/01 21:17:33 fisher Exp $
 */

public class GUIUtils {
	 //	 private static JOptionPane dialog = new JOptionPane();
	 private static JFileChooser fileChooser;

	 /** 
	  * By using global static variables, these values will persist
	  * across instances of the open/save fileChoosers.
	  */
	 private static boolean SHOW_ALL_FILES = GloDBMain.userDefaults.getBoolean("SHOW_ALL_FILES", 
																									 false);
	 private static boolean USE_FILE_EXTENSIONS = GloDBMain.userDefaults.getBoolean("USE_FILE_EXTENSIONS", 
																											false);

    //--------------------------------------------------------------------------
    // Getters and Setters
	 
	 /** Set the SHOW_ALL_FILES flag. */
    public static void setShowAllFiles(boolean showAllFiles) { 
		  SHOW_ALL_FILES = showAllFiles; 
		  GloDBMain.userDefaults.putBoolean("SHOW_ALL_FILES", showAllFiles);
	 }

	 public static boolean showAllFiles() { return SHOW_ALL_FILES; }

	 /** Set the USE_FILE_EXTENSIONS flag. */
    public static void setUseFileExtensions(boolean useFileExtensions) { 
		  USE_FILE_EXTENSIONS = useFileExtensions; 
		  GloDBMain.userDefaults.putBoolean("USE_FILE_EXTENSIONS", useFileExtensions);
	 }

	 public static boolean useFileExtensions() { return USE_FILE_EXTENSIONS; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /** RE-set GUI user defaults.  */
    public static void resetGUIDefaults() { 
		  setShowAllFiles(false);
		  setUseFileExtensions(false);
	 }

	 /**
	  * On Windows machines the path contains "\\", however, when
	  * converted to a String, this becomes "\".  So we are doubling
	  * them here, to preserver the "\\".
	  */
	 public static String getFilename(File file) {
		  return file.getAbsolutePath().replaceAll("\\\\","\\\\\\\\");
	 }

	 /** Present a dialog box for renaming a particular Track. */
	 public static String renameTrack(String id) {
		  // don't do anything if not a valid Track id
		  if (ObjectHandles.getTrack(id) == null) return "";

		  String[] labels = {"Current Name", "New Name"};
		  // use this to get the return value from FieldEditDialog
		  ArrayList textFields = new ArrayList();
		  JTextField tf = new JTextField(20);
		  tf.setText(id);
		  tf.setEditable(false);
		  textFields.add(tf);
		  tf = new JTextField(20);
		  tf.setText(id);
		  tf.setEditable(true);
		  textFields.add(tf);
		  new FieldEditDialog("Rename Track", labels, textFields);

		  // check "exit code" for FieldEditDialog, if false then exit
		  Boolean exitCode = (Boolean) textFields.get(textFields.size()-1);
		  if (! exitCode.booleanValue()) return "";

		  tf = (JTextField) textFields.get(1);
		  String newID = tf.getText();
		  if ((newID.length() > 0) && (id.compareTo(newID) != 0)) {
				ObjectHandles.renameTrack(id, newID);
				Root.runCommand("renameTrack(\"" + id + "\", \"" + newID + "\")", false);
				return newID;
		  }

		  return id;
	 }

	 /** 
	  * Present a dialog box for changing sequence loader. 
	  * @XXX This is currently hard coded for the FASTASequence loader.
	  */
	 public static void newSequenceLoader(String id) {
		  // don't do anything if not a valid Sequence id
		  Sequence sequence = ObjectHandles.getSequence(id);
		  if (sequence == null) return;

		  String[] labels = {"Loader Type", "Source File"};
		  // use this to get the return value from FieldEditDialog
		  ArrayList textFields = new ArrayList();
		  JTextField tf = new JTextField(20);
		  // XXX don't allow user to change loader type
		  tf.setText("FASTA File Loader");
		  /*
		  SequenceLoader loader = sequence.getDataLoader();
		  if (loader == null) {
				// default to FASTA File
				tf.setText("FASTA File Loader");
		  } else {
				tf.setText(loader.toString());
		  }
		  */
		  tf.setEditable(false);
		  textFields.add(tf);
		  tf = new JTextField(20);
		  // XXX this assumes FASTASequence as loader
		  Object arg = sequence.getLoaderArg("filename");
		  if (arg == null) arg = "";
		  tf.setText(arg.toString());
		  tf.setEditable(true);
		  textFields.add(tf);
		  new FieldEditDialog("Change Sequence Loader", labels, textFields);

		  // check "exit code" for FieldEditDialog, if false then exit
		  Boolean exitCode = (Boolean) textFields.get(textFields.size()-1);
		  if (! exitCode.booleanValue()) return;

		  tf = (JTextField) textFields.get(1);
		  String newSource = tf.getText();
		  if (! GloDBUtils.isEmpty(newSource)) {
				// XXX this only allows for loading of FASTA files
				Root.runCommand("setSequenceSourceFile(\"" + id + "\", \"" + newSource + "\", FASTA)", true);
		  }
	 }

	 /** Present a dialog box for selecting a particular Track. */
	 public static String trackSelector() {
		  // use this to get the return value from ObjectSelectorDialog
		  ArrayList out = new ArrayList();
		  Object[] objects = ObjectHandles.trackPool.keySet().toArray();
		  new ObjectSelectorDialog("Track Selector", objects, out);

		  if (out.size() > 0) {
				return (String) out.get(0);
		  } else {
				return "";
		  }
	 }

	 /** Present a dialog box for selecting a particular Sequence. */
	 public static String sequenceSelector() {
		  // use this to get the return value from ObjectSelectorDialog
		  ArrayList out = new ArrayList();
		  Object[] objects = ObjectHandles.sequencePool.keySet().toArray();
		  new ObjectSelectorDialog("Sequence Selector", objects, out);

		  if (out.size() > 0) {
				return (String) out.get(0);
		  } else {
				return "";
		  }
	 }

	 /** Create a file chooser for opening files. */
	 public static HashMap openFileChooser(int type, FileFilter filter) {
		  // use the current working directory
		  fileChooser = new JFileChooser(".");

		  // set the title
		  String title = "Load";
		  if (type == GloDBUtils.TRACK) title += " Track";
		  else if (type == GloDBUtils.SEQUENCE) title += " Sequence";
		  title += " File";
		  fileChooser.setDialogTitle(title);

		  // set the filter, if present
		  fileChooser.setAcceptAllFileFilterUsed(false);
		  HashSet dataTypes = FileIO.getDataTypes(type);
		  if (dataTypes != null) {
				for (Iterator i = dataTypes.iterator(); i.hasNext();) {
					 DataFile dataFile = (DataFile) i.next();
					 fileChooser.addChoosableFileFilter(dataFile.getFileFilter());
				}

				// XXX not sure if we need to test to make sure 'filter'
				// is a FileFilter in the FileIO set
				if (filter != null) fileChooser.setFileFilter(filter);
		  }

		  // XXX these options need to be enabled and coded.
		  JPanel accessoryP = new  JPanel(new GridLayout(2,0));
		  JCheckBox allFilesCB = new JCheckBox("Show all files");
		  allFilesCB.setSelected(showAllFiles());
		  allFilesCB.setEnabled(true);
		  allFilesCB.setToolTipText("When checked, all files will be displayed, regardless of their file extensions.");
		  allFilesCB.addItemListener(new ItemListener() {
					 public void itemStateChanged(ItemEvent e) {
						  setShowAllFiles(((e.getStateChange() == ItemEvent.SELECTED) ? true : false));
						  fileChooser.rescanCurrentDirectory();
					 }
				});
		  accessoryP.add(allFilesCB);		  
		  JCheckBox useExtensionsCB = new JCheckBox("Use file extensions");
		  useExtensionsCB.setSelected(useFileExtensions());
		  useExtensionsCB.setEnabled(true);
		  useExtensionsCB.setToolTipText("When checked, this will force the use of file extensions.  For example: \"data.gff\" instead of \"data\"");
		  useExtensionsCB.addItemListener(new ItemListener() {
					 public void itemStateChanged(ItemEvent e) {
						  setUseFileExtensions(((e.getStateChange() == ItemEvent.SELECTED) ? true : false));
					 }
				});
		  accessoryP.add(useExtensionsCB);
		  fileChooser.setAccessory(accessoryP);

		  // open the file chooser
		  int status = fileChooser.showOpenDialog(null);
		  if (status == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
				String filename = getFilename(file);

				filter = fileChooser.getFileFilter(); 
				String[] ext = {""};

				DataFile dataFile = FileIO.getDataType(type, filter.getDescription());
				if (dataFile != null) ext = dataFile.getExt();

				if (useFileExtensions()) {
					 // add filename extension, if necessary
					 if (ext.length > 0) {
						  boolean notFound = true;
						  int i = 0;
						  while (notFound && (i < ext.length)) {
								if (filename.endsWith(ext[i])) notFound = false;
								i++;
						  }

						  // the filename doesn't end with a valid
						  // extension, so loop through the extensions to
						  // see if we can find a file with one of the valid
						  // extensions.
						  if (notFound) {
								for (i = 0; i < ext.length; i++) {
									 file = new File(filename + ext[i]);
									 if (file.exists()) {
										  filename += ext[i];
										  break;
									 }
								}
						  }
					 }
				}

				if (file.exists()) {
					 HashMap out = new HashMap();
					 out.put("name", filename);
					 out.put("ext", ext);
					 out.put("filter", filter);
					 return out;
				}

				// the file doesn't exist, so rerun the file chooser
				JOptionPane.showMessageDialog(new Frame(), 
														"The file \"" + filename
														+ "\" does not exist.", title,
														JOptionPane.ERROR_MESSAGE);
				return openFileChooser(type, filter);
		  }

		  return null;
    }

	 /** 
	  * Use a JFileChooser the get the file info for saving a Sequence.
	  */
	 public static HashMap saveFileChooser(int type, FileFilter filter) {
		  // use the current working directory
		  fileChooser = new JFileChooser(".");

		  // set the title
		  String title = "Save";
		  if (type == GloDBUtils.TRACK) title += " Track";
		  else if (type == GloDBUtils.SEQUENCE) title += " Sequence";
		  title += " File";
		  fileChooser.setDialogTitle(title);

		  // set the filter, if present
		  fileChooser.setAcceptAllFileFilterUsed(false);
		  HashSet dataTypes = FileIO.getDataTypes(type);
		  if (dataTypes != null) {
				for (Iterator i = dataTypes.iterator(); i.hasNext();) {
					 DataFile dataFile = (DataFile) i.next();
					 fileChooser.addChoosableFileFilter(dataFile.getFileFilter());
				}

				// XXX not sure if we need to test to make sure 'filter'
				// is a FileFilter in FileIO
				if (filter != null) fileChooser.setFileFilter(filter);
		  }

		  // XXX these options need to be enabled and coded.
		  JPanel accessoryP = new  JPanel(new GridLayout(2,0));
		  JCheckBox allFilesCB = new JCheckBox("Show all files");
		  allFilesCB.setSelected(showAllFiles());
		  allFilesCB.setEnabled(true);
		  allFilesCB.setToolTipText("When checked, all files will be displayed, regardless of their file extensions.");
		  allFilesCB.addItemListener(new ItemListener() {
					 public void itemStateChanged(ItemEvent e) {
						  setShowAllFiles(((e.getStateChange() == ItemEvent.SELECTED) ? true : false));
						  fileChooser.rescanCurrentDirectory();
					 }
				});
		  accessoryP.add(allFilesCB);		  
		  JCheckBox useExtensionsCB = new JCheckBox("Use file extensions");
		  useExtensionsCB.setSelected(useFileExtensions());
		  useExtensionsCB.setEnabled(true);
		  useExtensionsCB.setToolTipText("When checked, this will force the use of file extensions.  For example: \"data.gff\" instead of \"data\"");
		  useExtensionsCB.addItemListener(new ItemListener() {
					 public void itemStateChanged(ItemEvent e) {
						  setUseFileExtensions(((e.getStateChange() == ItemEvent.SELECTED) ? true : false));
						  fileChooser.rescanCurrentDirectory();
					 }
				});
		  accessoryP.add(useExtensionsCB);
		  fileChooser.setAccessory(accessoryP);

		  // launch the file chooser
		  int status = fileChooser.showSaveDialog(null);
		  if (status == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
				String filename = getFilename(file);

				filter = fileChooser.getFileFilter(); 
				String[] ext = {""};

				DataFile dataFile = FileIO.getDataType(type, filter.getDescription());
				if (dataFile != null) ext = dataFile.getExt();

				if (useFileExtensions()) {
					 // add filename extension, if necessary
					 if (ext.length > 0) {
						  boolean notFound = true;
						  int i = 0;
						  while (notFound && (i < ext.length)) {
								if (filename.endsWith(ext[i])) notFound = false;
								i++;
						  }

						  // the filename doesn't end with a valid ext, so
						  // loop through the extensions to see if we can
						  // find a file with one of the valid extensions.
						  // we could just take the first ext that isn't a
						  // valid file but it's assumed that users will be
						  // consistent in their use of extensions and thus
						  // if ".fasta" matches but ".fas" doesn't, we
						  // assume that ".fasta" is actually what the user
						  // wants to use.
						  if (notFound) {
								i = 0; 
								// we're overloading 'notFound' by using it
								// here as well as above.
								while (notFound && (i < ext.length)) {
									 file = new File(filename + ext[i]);
									 if (file.exists()) notFound = false;
									 else i++;   // don't increment if found
								}
								// if no file extensions match a file, then
								// just use the first one in the list.
								if (notFound) filename += ext[0];
								else filename += ext[i];
						  }
					 }
				}

				if (! file.exists()) {
					 HashMap out = new HashMap();
					 out.put("name", filename);
					 //					 out.put("desc", filter.getDescription());
					 out.put("ext", ext);
					 out.put("filter", filter);
					 return out;
				}
					
				// the file exist, so check if want to overwrite the file
				String msg = "The file \"" + file.getPath() + "\" already exists.\n";
				msg += "Do you want to overwrite the file?";
				Object[] options = {"Overwrite", "Cancel"};
				int flag = JOptionPane.showOptionDialog(null, msg,
																	 "Overwrite Confirmation",
																	 JOptionPane.YES_NO_OPTION,
																	 JOptionPane.QUESTION_MESSAGE,
																	 null,
																	 options,
																	 options[1]);
				if (flag == JOptionPane.YES_OPTION) { // "Overwrite"
					 HashMap out = new HashMap();
					 out.put("name", filename);
					 out.put("ext", ext);
					 out.put("filter", filter);
					 return out;
				} else {
					 return saveFileChooser(type, filter);
				}
		  }

		  return null;
    }

} // GUIUtils.java
