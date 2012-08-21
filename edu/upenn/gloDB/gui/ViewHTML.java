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
 * @(#)ViewHTML.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.GloDBUtils;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.html.*;
import javax.swing.event.*;
import java.net.URL;
import java.io.*;
import javax.swing.filechooser.FileFilter;

/**
 * Browse HTML file or text.
 *
 * @author  Stephen Fisher
 * @version $Id: ViewHTML.java,v 1.1.2.11 2007/03/01 21:17:33 fisher Exp $
 */

public class ViewHTML extends JFrame {
	 private final static int URL = 1;
	 private final static int TEXT = 2;
	 private final static int INPUTSTREAM = 3;

	 ViewHTML thisFrame;
	 HTMLEditorPane htmlText = new HTMLEditorPane();
	 
	 public ViewHTML(URL source) {
		  super("Glo-DB: HTML Viewer");
		  setup(source, URL);
	 }

	 public ViewHTML(String source) {
		  super("Glo-DB: HTML Viewer");
		  setup(source, TEXT);
	 }

	 public ViewHTML(InputStream source) {
		  super("Glo-DB: HTML Viewer");
		  setup(source, INPUTSTREAM);
	 }

	 private void setup(Object source, int type) {
		  // keep pointer to self so can 'dispose' Frame below
		  thisFrame = this;
		  
		  setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		  
		  // setup text area to display data
		  htmlText.setEditable(false);
		  htmlText.addHyperlinkListener(new Hyperactive());
		  switch (type) {
		  case URL: 
				try { 
					 htmlText.setPage((URL) source); 
				} catch (IOException e) { 
					 GloDBUtils.printError("Invalid URL to be displayed"); 
					 return;
				}
				break;
		  case TEXT: 
				htmlText.setContentType("text/html");
				htmlText.setText((String) source);
				break;
		  case INPUTSTREAM: 
				htmlText.setContentType("text/html");
				try {
					 htmlText.read((InputStream) source, null);
				} catch (IOException e) { 
					 GloDBUtils.printError("Invalid InputStream to be displayed"); 
					 return;
				}
				break;
		  }
        JScrollPane htmlTextSP = new JScrollPane(htmlText);
		  htmlTextSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		  
		  // button panel
		  JPanel buttonP = new JPanel(new GridLayout(1,0));
		  buttonP.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		  // save button
		  JButton saveB = new JButton("Save HTML");
		  saveB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  String filename = saveFileChooser();
						  if (filename.length() > 0) {
								// set overwrite to 'true' because the user
								// agreed to overwrite the file in the
								// FileChooser
								saveText(filename, true);
						  }
					 }
				});
		  buttonP.add(saveB);
		  // close button
		  JButton closeB = new JButton("Close Viewer");
		  closeB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  thisFrame.dispose();
					 }
				});
		  buttonP.add(closeB);
		  
		  getContentPane().setLayout(new BorderLayout());
		  getContentPane().add(htmlTextSP, BorderLayout.CENTER);
		  getContentPane().add(buttonP, BorderLayout.SOUTH);
		  pack();
		  
		  // set the default window size
		  setSize(800, 900);
		  
		  // display the window
		  //		  setVisible(true);
		  show();
	 }

	 private class Hyperactive implements HyperlinkListener {
         public void hyperlinkUpdate(HyperlinkEvent e) {
 	          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
 		      HTMLEditorPane pane = (HTMLEditorPane) e.getSource();
 		      if (e instanceof HTMLFrameHyperlinkEvent) {
 		          HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
 		          HTMLDocument doc = (HTMLDocument)pane.getDocument();
 		          doc.processHTMLFrameHyperlinkEvent(evt);
 		      } else {
 		          try {
 			      pane.setPage(e.getURL());
 		          } catch (Throwable t) {
 			      t.printStackTrace();
 		          }
 		      }
 	          }
 	      }
     }

	 /** 
	  * Use a JFileChooser the get the file info for saving HTML to a file.
	  */
	 public String saveFileChooser() {
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

	 /**
	  * Save the html text to a file.
	  *
	  * @XXX need to throw FileIO exceptions, rather than just print
	  * errors.
	  */
	 public void saveText(String filename, boolean overwrite) {
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

				//				bWriter.write(htmlText.getText());
				bWriter.write(htmlText.getHTML());
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
	  * HTML FileFilter. 
	  */
	 private class HTMLFilter extends FileFilter {
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

	 /**
	  * This class is meant to wrap the JEditorPane because the
	  * JEditorPane munges up the HTML source when it stores the text.
	  * When it stores HTML it looses tags that it doesn't know about
	  * and thus when we use getText() routine to get the original HTML
	  * text back it isn't correct.  So here we keep a copy of the
	  * original HTML.  
	  * @XXX This should also handle URLs and InputStreams.
	  */
	 private class HTMLEditorPane extends JEditorPane {
		  private String origHTML = "";
		  
		  public void setText(String source) {
				super.setText(source);
				origHTML = source;
		  }

		  public String getHTML() {
				if (origHTML.length() > 0) return origHTML;
				else return getText();
		  }
	 }

} // ViewHTML.java
