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
 * @(#)HTMLFile.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.GloDBUtils;
import edu.upenn.gloDB.gui.GUIUtils;
import javax.swing.*;
import java.net.URL;
import java.io.*;
import javax.swing.filechooser.FileFilter;

/**
 * Save URL, HTML text, or InputStream as HTML file.
 *
 * @XXX This may not work for URL or InputStreams
 *
 * @author  Stephen Fisher
 * @version $Id: HTMLFile.java,v 1.1.2.5 2007/01/26 20:13:59 fisher Exp $
 */

public class HTMLFile {
	 public static void saveURL(URL source) {
		  saveURL(source, "", true);
	 }

	 public static void saveURL(URL source, String filename, boolean overwrite) {
		  // if no filename, then get filename
		  if (filename.length() == 0) filename = saveFileChooser();

		  // check again because saveFileChooser() might return empty
		  // filename
		  if (filename.length() == 0) return;

		  // add ".htm" filename extension, if necessary
		  if ((! filename.endsWith(".html")) && (! filename.endsWith(".htm"))) {
				filename += ".htm";
		  }

		  /*
		  try { 
				htmlText.setPage((URL) source); 
		  } catch (IOException e) { 
				GloDBUtils.printError("Invalid URL to be saved"); 
				return;
		  }
		  */
	 }

	 public static void saveInputStream(InputStream source) {
		  saveInputStream(source, "", true);
	 }

	 public static void saveInputStream(InputStream source, String filename, boolean overwrite) {
		  // if no filename, then get filename
		  if (filename.length() == 0) filename = saveFileChooser();

		  // check again because saveFileChooser() might return empty
		  // filename
		  if (filename.length() == 0) return;

		  // add ".htm" filename extension, if necessary
		  if ((! filename.endsWith(".html")) && (! filename.endsWith(".htm"))) {
				filename += ".htm";
		  }

		  /*
		  try {
				htmlText.read((InputStream) source, null);
		  } catch (IOException e) { 
				GloDBUtils.printError("Invalid InputStream to be saved"); 
				return;
		  }
		  */
	 }

	 /**
	  * Save the html text to a file.  This will overwrite any existing
	  * file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public static void saveText(String source) {
		  saveText(source, "", true);
	 }

	 /**
	  * Save the html text to a file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public static void saveText(String source, String filename, boolean overwrite) {
		  // if no filename, then get filename
		  if (filename.length() == 0) filename = saveFileChooser();

		  // check again because saveFileChooser() might return empty
		  // filename
		  if (filename.length() == 0) return;

		  // add ".htm" filename extension, if necessary
		  if ((! filename.endsWith(".html")) && (! filename.endsWith(".htm"))) {
				filename += ".htm";
		  }

		  File file = new File(filename);
		  // if the file already exists and not supposed to overwrite
		  // it, then return on error.
		  if (file.exists() && (! overwrite)) {
				GloDBUtils.printError("File \"" + filename + "\" already exists.");
				return;
		  }

		  try {
				FileWriter fWriter = new FileWriter(file);
				BufferedWriter bWriter = new BufferedWriter(fWriter);

				bWriter.write(source);
				//				bWriter.newLine();
				bWriter.flush();
				bWriter.close();
		  } catch (FileNotFoundException e) {
				// problem with FileOutputStream
				GloDBUtils.printError("File \"" + filename + "\" can not be opened.");
		  } catch (IOException e) {
				// problem with ObjectOutputStream.  XXX do we need to
				// close bWriter()?
				GloDBUtils.printError("Error writting html file \"" + filename + "\".");
		  }
	 }

	 /** 
	  * Use a JFileChooser the get the file info for saving HTML to a file.
	  */
	 private static String saveFileChooser() {
		  // use the current working directory
		  JFileChooser fileChooser = new JFileChooser();

		  // set the title
		  fileChooser.setDialogTitle("Save HTML File");
		  // set the filter, if present
		  fileChooser.setAcceptAllFileFilterUsed(true);
		  FileFilter filter = new HTMLFilter();
		  fileChooser.addChoosableFileFilter(filter);
		  fileChooser.setFileFilter(filter);

		  // launch the file chooser
		  int status = fileChooser.showSaveDialog(null);
		  if (status == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
				String filename = GUIUtils.getFilename(file);

				String[] ext = {".htm", ".html"};
				boolean notFound = true;
				int i = 0;
				while (notFound && (i < ext.length)) {
					 if (filename.endsWith(ext[i])) notFound = false;
					 i++;
				}

				// the filename doesn't end with a valid ext, so loop
				// through the extensions to see if we can find a file
				// with one of the valid extensions.  we could just take
				// the first ext that isn't a valid file but it's assumed
				// that users will be consistent in their use of
				// extensions and thus if ".html" matches but ".htm"
				// doesn't, we assume that ".html" is actually what the
				// user wants to use.
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

				if (! file.exists()) return filename;
					
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
					 return filename;
				} else {
					 return saveFileChooser();
				}
		  }

		  return "";
    }


	 /** HTML FileFilter. */
	 private static class HTMLFilter extends FileFilter {
		  public boolean accept(File f) {
				// accept directories
				if (f.isDirectory()) return true;
				
				// accept all files
				if ((f.getName()).endsWith(".html")) return true;
				if ((f.getName()).endsWith(".htm")) return true;

				return false;
		  }
		  
		  // set the filter's description
		  public String getDescription() { return "HTML files (*.htm; *.html)"; }
	 }

} // HTMLFile.java
