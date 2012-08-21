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
 * @(#)Root.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Base window for GUI.
 *
 * @author  Stephen Fisher
 * @version $Id: Root.java,v 1.35.2.28 2007/03/01 21:17:33 fisher Exp $
 */

public class Root  {
	 private static RootFrame rootFrame = null;

	 /**
	  * This will contain all error and warning messages.  This output
	  * panel can be disabled by setting the 'showMessages' flag to
	  * false.  When disabled, all messages printed will be displayed
	  * in the console.
	  */
	 public static JTextArea messages = new JTextArea(20, 90);

	 /** 
	  * Flag whether to display messages in the 'messages' panel.  If
	  * false, then print the messages in the console.  If true, then
	  * messages will only go to the messages panel and not the console.
	  */
	 public static boolean showMessages = true;

	 /**
	  * This will contain all commands that the GUI generates and sends
	  * to the console to be run.  The text in this panel can be copied
	  * and pasted into a python file to run directly in the console.
	  */
	 public static JTextArea history = new JTextArea(10, 90);

	 public static JLabel statusBar;

	 public static JFrame show() {
		  // only allow one instance of rootFrame
		  if (rootFrame == null) rootFrame = new RootFrame();
		  rootFrame.show();
		  return rootFrame;
	 }
		  
	 /** 
	  * This will add the String to the history and if 'toConsole',
	  * then run the command through the console.
	  */
	 public static void runCommand(String msg, boolean toConsole) {
		  history.append(msg + "\n");
		  if (toConsole) GloDBMain.console.exec("print; " + msg);
	 }

	 private static class RootFrame extends JFrame implements ActionListener {
		  private JMenuItem loadTrack;
		  private JMenuItem saveTrack;
		  private JMenuItem newSequence;
		  private JMenuItem loadSequence;
		  private JMenuItem saveSequence;
		  private JMenuItem quit;
		  
		  private JMenuItem copyHistory;
		  private JMenuItem clearHistory;
		  private JMenuItem selectHistory;
		  private JCheckBoxMenuItem toggleShowMessages;
		  private JMenuItem clearMessages;
		  private JMenuItem queryTracks;
		  //	 private JMenuItem updateTracks;
		  
		  private JMenuItem browseTracks;
		  private JMenuItem browseSequences;
		  private JMenuItem displayTrack;
		  private JMenuItem displaySequence;
		  
		  private JMenuItem helpTopics;
		  private JMenuItem viewAPI;
		  private JMenuItem parserDefs;
		  private JMenuItem about;
		  
		  private JTextArea messages = Root.messages;
		  private JTextArea history = Root.history;
		  private JLabel statusBar = Root.statusBar;
		  
		  public RootFrame() {
				super("Glo-DB");
				
				// this will cause all warnings to be sent here instead of to
				// the console
				GloDBUtils.guiMessages = messages;
				
				//		  setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setDefaultCloseOperation(EXIT_ON_CLOSE);
				
				//		  addWindowListener(new WindowAdapter() {
				//					 public void windowClosing(WindowEvent e) { System.exit(0); }
				//				});
				
				/**** FILE MENU ****/
				JMenu fileMenu = new JMenu("File");
				fileMenu.setMnemonic(KeyEvent.VK_F);
				
				loadTrack = addMenuItem(loadTrack, fileMenu, "Load Track", KeyEvent.VK_L, new ImageIcon("icons/load_m.png"));
				saveTrack = addMenuItem(saveTrack, fileMenu, "Save Track", -1, new ImageIcon("icons/save_m.png"));
				saveTrack.setMnemonic(KeyEvent.VK_S);
				fileMenu.addSeparator();
				newSequence = addMenuItem(newSequence, fileMenu, "New Sequence", -1, new ImageIcon("icons/new_m.png"));
				newSequence.setMnemonic(KeyEvent.VK_N);
				loadSequence = addMenuItem(loadSequence, fileMenu, "Load Sequence", -1, new ImageIcon("icons/load_m.png"));
				//				saveSequence = addMenuItem(saveSequence, fileMenu, "Save Sequence", -1, new ImageIcon("icons/save_m.png"));
				fileMenu.addSeparator();
				quit = addMenuItem(quit, fileMenu, "Quit", KeyEvent.VK_Q, new ImageIcon("icons/quit_m.png"));
				
				/**** EDIT MENU ****/
				JMenu editMenu = new JMenu("Edit");
				editMenu.setMnemonic(KeyEvent.VK_E);
				copyHistory = addMenuItem(copyHistory, editMenu, "Copy History", -1, new ImageIcon("icons/copy_m.png"));
				copyHistory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
				clearHistory = addMenuItem(clearHistory, editMenu, "Clear History", -1, new ImageIcon("icons/clear_m.png"));
				clearHistory.setMnemonic(KeyEvent.VK_C);
				selectHistory = addMenuItem(selectHistory, editMenu, "Select All History", -1, new ImageIcon(""));
				selectHistory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
				editMenu.addSeparator();
				toggleShowMessages = new JCheckBoxMenuItem("Enable Message Window");
				toggleShowMessages.addActionListener(this);
				toggleShowMessages.setSelected(Root.showMessages);
				toggleShowMessages.setToolTipText("Disabling the Message Window will cause messagaes to be sent to the command line.");
				editMenu.add(toggleShowMessages);
				clearMessages = addMenuItem(clearMessages, editMenu, "Clear Messages", -1, new ImageIcon("icons/clear_m.png"));
				editMenu.addSeparator();
				queryTracks = addMenuItem(queryTracks, editMenu, "Query Tracks", KeyEvent.VK_G, new ImageIcon("icons/search_m.png"));
				//		  updateTracks = addMenuItem(updateTracks, editMenu, "Update Tracks", -1, null);
				
				/**** VIEW MENU ****/
				JMenu viewMenu = new JMenu("View");
				viewMenu.setMnemonic(KeyEvent.VK_V);
				browseTracks = addMenuItem(browseTracks, viewMenu, "Browse Tracks", -1, new ImageIcon("icons/browse_m.png"));
				browseSequences = addMenuItem(browseSequences, viewMenu, "Browse Sequences", -1, new ImageIcon("icons/browse_m.png"));
				viewMenu.addSeparator();
				displayTrack = addMenuItem(displayTrack, viewMenu, "Display Track", -1, new ImageIcon("icons/graph_m.png"));
				displaySequence = addMenuItem(displaySequence, viewMenu, "Display Sequence", -1, new ImageIcon("icons/graph_m.png"));
				
				/**** HELP MENU ****/
				JMenu helpMenu = new JMenu("Help");
				helpMenu.setMnemonic(KeyEvent.VK_H);
				helpTopics = addMenuItem(helpTopics, helpMenu, "Help Topics", -1, new ImageIcon("icons/help_m.png"));
				viewAPI = addMenuItem(viewAPI, helpMenu, "API Documentation", -1, null);
				parserDefs = addMenuItem(parserDefs, helpMenu, "Parser Definitions", -1, null);
				helpMenu.addSeparator();
				about = addMenuItem(about, helpMenu, "About", -1, new ImageIcon("icons/info_m.png"));
				
				// put menu together and add to root pane
				JMenuBar menuBar = new JMenuBar();
				menuBar.add(fileMenu);
				menuBar.add(editMenu);
				menuBar.add(viewMenu);
				// push help menu to the right side
				menuBar.add(Box.createHorizontalGlue());
				menuBar.add(helpMenu);
				getRootPane().setJMenuBar(menuBar);
				
				// add messages as a scrolled text area.
				messages.setEditable(false);
				JScrollPane messagesSP = new JScrollPane(messages);
				messagesSP.setBorder(BorderFactory.createLoweredBevelBorder());
				JPanel messagesP = new JPanel(false);
				messagesP.setLayout(new BorderLayout());
				messagesP.add(new JLabel(" Messages:"), BorderLayout.NORTH);
				messagesP.add(messagesSP, BorderLayout.CENTER);
				
				// add history as a scrolled text area.
				history.setEditable(false);
				JScrollPane historySP = new JScrollPane(history);
				historySP.setBorder(BorderFactory.createLoweredBevelBorder());
				JPanel historyP = new JPanel(false);
				historyP.setLayout(new BorderLayout());
				historyP.add(new JLabel(" History:"), BorderLayout.NORTH);
				historyP.add(historySP, BorderLayout.CENTER);
				
				// setup main JPanel for window
				JPanel mainPane = new JPanel(false);
				mainPane.setLayout(new BorderLayout());
				JSplitPane mainSplitP = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
																	messagesP, historyP);
				mainSplitP.setOneTouchExpandable(true);
				// provide minimum sizes for the two components in the split pane
				messagesP.setMinimumSize(new Dimension(700, 400));
				historyP.setMinimumSize(new Dimension(700, 200));
				// provide a preferred size for the split pane
				mainSplitP.setPreferredSize(new Dimension(700, 600));
				// resize upper panel by 50% more that lower when the window
				// is expended
				mainSplitP.setResizeWeight(0.75);
				mainPane.add(mainSplitP, BorderLayout.CENTER);
				
				// add status bar at bottom of window.  Use a 'spacer' so that
				// anything added to statusBar later won't be crammed into the
				// left margin.
				statusBar = new JLabel(" ");
				JPanel statusBarP = new JPanel(false);
				statusBarP.setBorder(BorderFactory.createLoweredBevelBorder());
				statusBarP.setLayout(new BorderLayout());
				statusBarP.add(new JLabel(" "), BorderLayout.WEST);
				statusBarP.add(statusBar, BorderLayout.CENTER);
				mainPane.add(statusBarP, BorderLayout.SOUTH);
				
				getContentPane().add(mainPane);
				pack();
				
				// set the default window size
				//		  setSize(getSize().width + 300, getSize().height + 200);
				
				// display the window
				//		  setVisible(true);
				show();
		  }
		  
		  //--------------------------------------------------------------------------
		  // Miscellaneous Methods
		  
		  /** Handle menu items. */
		  public void actionPerformed(ActionEvent e) {
				//		  history.append("Event source: " + ((JMenuItem) e.getSource()).getText() + "\n");
				
				// erase status bar whenever there is an ActionEvent.
				statusBar.setText("");
				
				if (e.getSource() instanceof JMenuItem) {
					 JMenuItem source = (JMenuItem) e.getSource();
					 
					 if (source == loadTrack) {
						  GUITrackIO.loadTrack();
					 } else if (source == saveTrack) {
						  String track = GUIUtils.trackSelector();
						  if (track.length() == 0) return;
						  GUITrackIO.saveTrack(track);
					 } else if (source == loadSequence) {
						  GUISequenceIO.loadSequence();
					 } else if (source == newSequence) {
						  GUISequenceIO.newSequence();
					 } else if (source == quit) {
						  System.exit(0);
					 } else if (source == copyHistory) {
						  history.copy();
						  // exit here so the 'history' routines below don't undo this
						  return;
					 } else if (source == clearHistory) {
						  history.selectAll();
						  history.replaceSelection("");
						  return;
					 } else if (source == selectHistory) {
						  history.selectAll();
						  // exit here so the 'history' routines below don't undo this
						  return;
					 } else if (source == toggleShowMessages) {
						  Root.showMessages = toggleShowMessages.isSelected();
						  
						  if (Root.showMessages) GloDBUtils.guiMessages = messages;
						  else GloDBUtils.guiMessages = null;
						  //					 GloDBUtils.printMsg("Value of showMessages: " + Boolean.toString(Root.showMessages));
						  return;
					 } else if (source == clearMessages) {
						  messages.selectAll();
						  messages.replaceSelection("");
						  return;
					 } else if (source == queryTracks) {
						  new QueryBuilder();
						  Root.runCommand("QueryBuilder()", false);
						  /*
					 } else if (source == displayTrack) {
						  // the following doesn't work because Root() doesn't
						  // get redrawn while waiting on GenomeBrowser()

						  // run command here so can change cursor to "wait"
						  // while getting communicating with Genome Browser
						  //String track = GUIUtils.trackSelector();
						  //if (track.length() > 0) {
						  //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						  //this.repaint();
						  //new ViewHTML(GenomeBrowser.viewTrack(track));
						  //Root.runCommand("genomeBrowserTrack(\"" + track + "\")", false); 
						  //this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						  //}

						 Root.runCommand("viewTrackGenomeBrowser()", true); 
						 */
					 } else if (source == browseSequences) {
						  Root.runCommand("sequenceBrowser()", true);
					 } else if (source == browseTracks) {
						  Root.runCommand("trackBrowser()", true);
					 } else if (source == helpTopics) {
						  Root.runCommand("help()", true);
					 } else if (source == viewAPI) {
						  try {
								URL url = new URL("file:documentation/index.html");
								new ViewHTML(url);
								Root.runCommand("viewAPI()", false);
						  } catch (MalformedURLException urlException) {
								GloDBUtils.printError("API documentation not found.");
						  }
					 } else if (source == parserDefs) {
						  new ViewParserDefs();
						  Root.runCommand("parserDefs()", false);
					 } else if (source == about) {
						  Root.runCommand("about()", true);
					 } else {
						  statusBar.setText("Menu item not yet available.");
						  GloDBUtils.printMsg("Menu item not yet available.");
					 }
					 
					 history.setCaretPosition(history.getDocument().getLength());
				}
		  }
		  
		  /** Add specified item to specified menu. */
		  private JMenuItem addMenuItem(JMenuItem item, JMenu menu, String label, int mnemonic, ImageIcon image) {
				JMenuItem out;
				if (image == null) {
					 out = new JMenuItem(label);
				} else {
					 out = new JMenuItem(label, image);
				}
				if (mnemonic != -1) { 
					 out.setMnemonic(mnemonic);
					 out.setAccelerator(KeyStroke.getKeyStroke(mnemonic, ActionEvent.ALT_MASK));
				}
				out.addActionListener(this);
				menu.add(out);
				return out;
		  }
		  
	 } // Root.java
}
