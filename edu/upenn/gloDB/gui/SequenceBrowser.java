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
 * @(#)SequenceBrowser.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Browse existing Sequences.
 *
 * @author  Stephen Fisher
 * @version $Id: SequenceBrowser.java,v 1.25.2.28 2007/03/01 21:17:33 fisher Exp $
 */

public class SequenceBrowser {
	 private static BrowserFrame browserFrame = null;

	 public static JFrame show() {
		  return show("");
	 }

    public static JFrame show(String id) {
		  // only allow one instance of BrowserFrame
		  if (browserFrame == null) browserFrame = new BrowserFrame();

		  browserFrame.selectSequence(id);
		  browserFrame.show();

		  return browserFrame;
	 }
		  
	 private static class BrowserFrame extends JFrame {
		  private JComboBox sequenceCB;
		  private JList attributeL;
		  private JButton addAttributeB;
		  private JButton editAttributeB;
		  private JButton delAttributeB;
		  private JList trackL;
		  private JLabel statusBar;
		  
		  private JLabel offsetL = new JLabel("");
		  private JLabel minL = new JLabel("");
		  private JLabel maxL = new JLabel("");
		  private JLabel lengthL = new JLabel("");
		  private JLabel loadDataL = new JLabel("");
		  
		  private Sequence sequence;
		  private Object[] tracks;
		  
		  BrowserFrame thisFrame;
		  
		  public BrowserFrame() {
				super("Sequence Browser");
				
				// keep pointer to self so can 'dispose' Frame below
				thisFrame = this;
				
				setDefaultCloseOperation(HIDE_ON_CLOSE);
				
				// ***** SETUP SEQUENCE INFO *****
				JToolBar sequenceP = new JToolBar();
				sequenceP.setFloatable(false);
				sequenceP.setBorder(BorderFactory.createEtchedBorder());
				// add Sequence ComboBox
				sequenceCB = new JComboBox(ObjectHandles.getSequenceList());
				sequenceCB.setEditable(false);
				//				selectSequence(id);
				sequenceCB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								String selection = (String) ((JComboBox) e.getSource()).getSelectedItem();
								sequenceSelected(selection);
						  }
					 });
				// add Buttons
				JButton newB = new JButton(new ImageIcon("icons/new.png"));
				newB.setToolTipText("New");
				newB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("New sequence"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				newB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								Sequence tmp = GUISequenceIO.newSequence();
								// if user 'cancelled' then don't do anything
								if (tmp != null) {	 
									 sequence = tmp;
									 
									 // don't use 'id' here because the actual ID
									 // might have changed when creating the
									 // Sequence
									 sequenceCB.setSelectedItem(sequence.getID());
								}
						  }
					 });
				JButton loadB = new JButton(new ImageIcon("icons/load.png"));
				loadB.setToolTipText("Load");
				loadB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Load sequence"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				loadB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								Set sequences = GUISequenceIO.loadSequence();
								if (sequences != null) {
									 Iterator i = sequences.iterator();
									 Sequence sequence = (Sequence) i.next();
									 sequenceCB.setSelectedIndex(ObjectHandles.getSequenceList().getIndexOf(sequence.getID()));
								}
						  }
					 });
				JButton saveB = new JButton(new ImageIcon("icons/save.png"));
				saveB.setToolTipText("Save");
				saveB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Save sequence"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				saveB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (sequence == null) return;
								GloDBUtils.printMsg("'Save' not yet implemented.");
						  }
					 });
				JButton sourceB = new JButton(new ImageIcon("icons/web.png"));
				sourceB.setToolTipText("View/edit source location");
				sourceB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Edit and/or view the source location (file, URL, etc)"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				sourceB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (sequence == null) return;
								new EditSourcePanel();
						  }
					 });
				JButton viewB = new JButton(new ImageIcon("icons/view.png"));
				viewB.setToolTipText("View Data");
				viewB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("View sequence data"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				viewB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (sequence == null) return;
								
								// if user set data directly, then getDataLoader
								// may not be set but there would still be data to
								// view
								if ((! sequence.isDataLoaded()) && (sequence.getDataLoader() == null)) {
									 GloDBUtils.printError("No source data associated with this sequence.");
									 return;
								} 
								
								// warn user for very long sequences
								if (sequence.length() > 100000) {
									 String msg = "The sequence is very long (" + Integer.toString(sequence.length()) + " bp).\n";
									 msg += "It may take 60sec or longer to create this display.\n";
									 msg += "Are you sure you want to continue?\n";
									 Object[] options = {"Display", "Cancel"};
									 int flag = JOptionPane.showOptionDialog(null, msg,
																						  "Display Confirmation",
																						  JOptionPane.YES_NO_OPTION,
																						  JOptionPane.QUESTION_MESSAGE,
																						  null,
																						  options,
																						  options[1]);
									 if (flag == JOptionPane.YES_OPTION) { // "Display"
										  new ViewSequencePanel();
									 }
								} else {
									 // short (< 100K bp) sequence so just display
									 new ViewSequencePanel();
								}
								
								// if data wasn't previously loaded, it should be
								// now so try again to set these values.
								if (sequence.isDataLoaded()) {
									 maxL.setText(Integer.toString(sequence.getMax()));
									 lengthL.setText(Integer.toString(sequence.length()));
								}
								loadDataL.setText(Boolean.toString(sequence.isDataLoaded()));
						  }
					 });
				JButton deleteB= new JButton(new ImageIcon("icons/delete.png"));
				deleteB.setToolTipText("Delete Sequence");
				deleteB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("THIS WILL PERMANENTLY DELETE THE SEQUENCE!"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				deleteB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (sequence == null) return;	
								
								String msg = "Are you sure you want to remove this sequence?\n";
								msg += "Sequence: " + sequence.getID();
								Object[] options = {"Delete Sequence", "Cancel"};
								int flag = JOptionPane.showOptionDialog(null, msg,
																					 "Delete Confirmation",
																					 JOptionPane.YES_NO_OPTION,
																					 JOptionPane.QUESTION_MESSAGE,
																					 null,
																					 options,
																					 options[1]);
								if (flag == JOptionPane.YES_OPTION) { // "Delete"
									 ObjectHandles.removeSequence(sequence);
									 if (sequenceCB.getItemCount() > 0) {
										  sequenceCB.setSelectedIndex(0);
									 } else {
										  sequenceSelected(null);
									 }
								}
						  }
					 });
				JButton closeB = new JButton(new ImageIcon("icons/close.png"));
				closeB.setToolTipText("Close");
				closeB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Close the sequence browser"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				closeB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								thisFrame.hide();
						  }
					 });
				// add ToolBar
				sequenceP.addSeparator(new Dimension(15, 0));
				sequenceP.add(newB);
				sequenceP.addSeparator(new Dimension(30, 25));
				sequenceP.add(loadB);
				sequenceP.addSeparator(new Dimension(30, 25));
				/*
				sequenceP.add(saveB);
				sequenceP.addSeparator(new Dimension(30, 25));
				*/
				sequenceP.add(new JLabel("Sequence: "));
				sequenceP.add(sequenceCB);
				sequenceP.addSeparator(new Dimension(15, 0));
				sequenceP.add(sourceB);
				sequenceP.addSeparator(new Dimension(30, 25));
				sequenceP.add(viewB);
				sequenceP.addSeparator(new Dimension(30, 25));
				sequenceP.add(deleteB);
				sequenceP.addSeparator(new Dimension(30, 25));
				sequenceP.add(closeB);
				sequenceP.addSeparator(new Dimension(15, 0));
				
				// ***** SETUP ATTRIBUTE INFO *****
				JPanel attributeP = new JPanel(new BorderLayout());
				// add attributes List
				attributeL = new JList();
				//		  attributeL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				attributeL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				attributeL.addListSelectionListener(new ListSelectionListener() {
						  public void valueChanged(ListSelectionEvent e) {
								if (e.getValueIsAdjusting()) return;
								
								JList list = (JList) e.getSource();
								if (list.isSelectionEmpty()) {
									 // disable the "Delete" button if no selection
									 editAttributeB.setEnabled(false);
									 delAttributeB.setEnabled(false);
								} else {
									 // enable the "Delete" button if something selected
									 editAttributeB.setEnabled(true);
									 delAttributeB.setEnabled(true);
								}
						  }
					 });
				JScrollPane attributeSP = new JScrollPane(attributeL);
				attributeP.add(attributeSP, BorderLayout.CENTER);
				// add button sub-panel
				JPanel attributeBP = new JPanel(new GridLayout(1,0,5,5));
				// add attributes "add" Button
				addAttributeB = new JButton(new ImageIcon("icons/new.png"));
				addAttributeB.setToolTipText("Add Attribute");
				addAttributeB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (sequence == null) return;
								
								String[] labels = {" Key:", " Value:"};
								// use this to get the return value from FieldEditDialog
								ArrayList textFields = new ArrayList();
								textFields.add(new JTextField(20));
								textFields.add(new JTextField(20));
								new FieldEditDialog("Add Attribute", labels, textFields);
								
								// check "exit code" for FieldEditDialog, if false then exit
								Boolean exitCode = (Boolean) textFields.get(textFields.size()-1);
								if (! exitCode.booleanValue()) return;

								JTextField tf = (JTextField) textFields.get(0);
								String key = tf.getText();
								if (key.length() > 0) { // make sure a key is valid
									 tf = (JTextField) textFields.get(1);  // get value
									 String value = tf.getText();
									 String msg = "getSequence(\"" + sequence.getID() + "\")";
									 msg += ".addAttribute(\"" + key + "\", \"" + value + "\")";
									 Root.runCommand(msg, true);
									 updateAttributes();
								}
						  }
					 });
				attributeBP.add(addAttributeB);
				// add attributes "edit" Button
				editAttributeB = new JButton(new ImageIcon("icons/edit.png"));
				editAttributeB.setToolTipText("Edit Attribute");
				editAttributeB.setEnabled(false);
				editAttributeB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								StringTokenizer st = new StringTokenizer((String) 
																					  attributeL.getSelectedValue(),
																					  ":", true);
								String key = st.nextToken();
								String value = st.nextToken(); // this is the ":"
								value = st.nextToken();	// this is the actual value
								value = value.substring(1, value.length());  // remove leading " "
								// add any remaining tokens
								while (st.hasMoreTokens()) { 
									 value += st.nextToken(); 
								}
								
								String[] labels = {" Key:", " Value:"};
								// use this to get the return value from FieldEditDialog
								ArrayList textFields = new ArrayList();
								JTextField tf = new JTextField(20);
								tf.setText(key);
								tf.setEditable(false);
								textFields.add(tf);
								tf = new JTextField(20);
								tf.setText(value);
								textFields.add(tf);
								new FieldEditDialog("Edit Attribute", labels, textFields);
								
								// check "exit code" for FieldEditDialog, if false then exit
								Boolean exitCode = (Boolean) textFields.get(textFields.size()-1);
								if (! exitCode.booleanValue()) return;

								tf = (JTextField) textFields.get(1);
								String newValue = tf.getText();
								if (value.compareTo(newValue) != 0) { // make sure value changed
									 String msg = "getSequence(\"" + sequence.getID() + "\")";
									 msg += ".addAttribute(\"" + key + "\", \"" + newValue + "\")";
									 Root.runCommand(msg, true);
									 updateAttributes();
								}
						  }
					 });
				attributeBP.add(editAttributeB);
				// add attributes "delete" Button
				delAttributeB = new JButton(new ImageIcon("icons/delete.png"));
				delAttributeB.setToolTipText("Delete Attribute");
				delAttributeB.setEnabled(false);
				delAttributeB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								String msg = "Are you sure you want to delete the following attribute?\n\t";
								msg += attributeL.getSelectedValue();
								Object[] options = {"Delete Attribute", "Cancel"};
								int flag = JOptionPane.showOptionDialog(null, msg,
																					 "Delete Confirmation",
																					 JOptionPane.YES_NO_OPTION,
																					 JOptionPane.QUESTION_MESSAGE,
																					 null,
																					 options,
																					 options[1]);
								if (flag == JOptionPane.YES_OPTION) { // "Delete"
									 StringTokenizer st = new StringTokenizer((String) 
																							attributeL.getSelectedValue(),
																							":");
									 String key = st.nextToken();
									 msg = "getSequence(\"" + sequence.getID() + "\")";
									 msg += ".delAttribute(\"" + key + "\")";
									 Root.runCommand(msg, true);
									 
									 updateAttributes();
								}
						  }
					 });
				attributeBP.add(delAttributeB);
				attributeP.add(attributeBP, BorderLayout.SOUTH);
				// add attributes Label
				attributeP.add(new JLabel(" Attributes:"), BorderLayout.NORTH);
				// provide a preferred size for attributesP
				attributeP.setPreferredSize(new Dimension(300, 200));
				
				// ***** SETUP SEQUENCE SPECs *****
				JPanel infoP = new JPanel(new BorderLayout());
				// add info sub-panel
				JPanel infoSP = new JPanel(new GridLayout(5,2,5,5));
				infoSP.add(new JLabel(" Offset:"));
				infoSP.add(offsetL);
				infoSP.add(new JLabel(" Min:"));
				infoSP.add(minL);
				infoSP.add(new JLabel(" Max:"));
				infoSP.add(maxL);
				infoSP.add(new JLabel(" Length:"));
				infoSP.add(lengthL);
				infoSP.add(new JLabel(" Data loaded:"));
				infoSP.add(loadDataL);
				infoP.add(infoSP, BorderLayout.CENTER);
				// add button sub-panel
				JPanel infoBP = new JPanel(new GridLayout(0,1,5,5));
				// add info "loadData" button
				JButton loadDataB = new JButton("(Re)Load Data");
				loadDataB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								String msg = "Are you sure you want to (re)load the sequence data?\n";
								msg += "This may take a while for large data files.";
								Object[] options = {"Load", "Cancel"};
								int flag = JOptionPane.showOptionDialog(null, msg,
																					 "Load Data Confirmation",
																					 JOptionPane.YES_NO_OPTION,
																					 JOptionPane.QUESTION_MESSAGE,
																					 null,
																					 options,
																					 options[1]);
								if (flag == JOptionPane.YES_OPTION) { // "Load"
									 if (sequence != null) {
										  sequence.reloadData();
										  loadDataL.setText(Boolean.toString(sequence.isDataLoaded()));
										  if (sequence.isDataLoaded()) {
												maxL.setText(Integer.toString(sequence.getMax()));
												lengthL.setText(Integer.toString(sequence.length()));
										  }
									 }
								}
						  }
					 });
				infoBP.add(loadDataB);
				infoP.add(infoBP, BorderLayout.SOUTH);
				
				// ***** SETUP TRACK INFO *****
				JPanel trackP = new JPanel(new BorderLayout());
				// setup list of Tracks for selected sequence
				trackL = new JList();
				trackL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				trackL.setToolTipText("Double click on a Track name to edit the item in a Track Browser");
				/*
				  trackL.addListSelectionListener(new ListSelectionListener() {
				  public void valueChanged(ListSelectionEvent e) {
				  if (e.getValueIsAdjusting()) return;
				  
				  JList list = (JList) e.getSource();
				  if (list.isSelectionEmpty()) {
				  statusBar.setText("");
				  } else {
				  Track track = ObjectHandles.getTrack((String) tracks[index]);
				  String msg = "Track ID: " + track.getID();
				  msg += " :: Num Features: " + track.numFeatures();
				  msg += " :: Length: " + track.length();
				  msg += " :: Attributes: " + track.getAttributes();
				  statusBar.setText(msg);
				  }
				  }
				  });
				*/
				trackL.addMouseMotionListener(new MouseMotionListener() {
						  public void mouseMoved(MouseEvent e) {
								if (tracks.length > 0) {
									 int index = trackL.locationToIndex(e.getPoint());
									 if (index > -1) {
										  Track track = ObjectHandles.getTrack((String) tracks[index]);
										  String msg = "Track ID: " + track.getID();
										  msg += " :: Num Features: " + track.numFeatures();
										  msg += " :: Length: " + track.length();
										  msg += " :: Attributes: " + track.getAttributes();
										  statusBar.setText(msg);
									 }
								} else {
									 statusBar.setText("No tracks with features on this sequence.");
								}								
						  }
						  public void mouseDragged(MouseEvent e) { ; }
					 });
				trackL.addMouseListener(new MouseAdapter() {
						  // clear status bar when mouse moves out of trackL
						  public void mouseExited(MouseEvent e) {
								statusBar.setText("");
						  }
						  
						  public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 2) {
									 int index = trackL.locationToIndex(e.getPoint());
									 
									 if (index > -1) {	 // make sure there is a valid item
										  // launch the Track Browser through Root so
										  // that the command gets added to the history.
										  String id = (String) tracks[index];
										  String cmd = "trackBrowser(\"" + id + "\")";
										  Root.runCommand(cmd, true);
										  
										  /*
											 String msg = (ObjectHandles.getTrack((String) tracks[index])).toString();
											 JOptionPane.showMessageDialog(null, msg,
											 "Track Viewer",
											 JOptionPane.INFORMATION_MESSAGE);
										  */
									 }
								}
						  }
					 });
				JScrollPane trackSP = new JScrollPane(trackL);
				trackP.add(trackSP, BorderLayout.CENTER);
				// add Track Label
				trackP.add(new JLabel(" Tracks:"), BorderLayout.NORTH);
				trackP.add(infoP, BorderLayout.SOUTH);
				
				// setup split pane for Track/Feature Panels
				JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
																  attributeP, trackP);
				splitPane.setOneTouchExpandable(true);
				splitPane.setDividerLocation(300);
				// provide minimum sizes for the two components in the split pane
				Dimension minimumSize = new Dimension(150, 200);
				attributeP.setMinimumSize(minimumSize);
				trackP.setMinimumSize(minimumSize);
				// provide a preferred size for the split pane
				splitPane.setPreferredSize(new Dimension(400, 200));
				
				// add status bar at bottom of window
				JLabel statusBarSpacer = new JLabel(" ");
				statusBar = new JLabel(" ");
				JPanel statusBarP = new JPanel(false);
				statusBarP.setBorder(BorderFactory.createLoweredBevelBorder());
				statusBarP.setLayout(new BorderLayout());
				statusBarP.add(statusBarSpacer, BorderLayout.WEST);
				statusBarP.add(statusBar, BorderLayout.CENTER);
				
				getContentPane().add(sequenceP, BorderLayout.NORTH);
				getContentPane().add(splitPane, BorderLayout.CENTER);
				getContentPane().add(statusBarP, BorderLayout.SOUTH);
				pack();
				
				// set the default window size
				setSize(getSize().width + 50, getSize().height + 150);
				
				// initialize the browser to the current Sequence
				sequenceSelected((String) sequenceCB.getSelectedItem());
				
				// display the window
				show();
		  }
		  
		  /** 
			* This will update the SequenceBrowser for the currently selected
			* Sequence.
			*/
		  private void sequenceSelected(String id) {
				if ((id == null) || (sequenceCB.getItemCount() == 0)) {
					 // if null item or empty list, then clear display
					 tracks = new Object[0];
					 trackL.setListData(tracks);
					 attributeL.setListData(new String[0]);
					 
					 offsetL.setText("");
					 minL.setText("");
					 maxL.setText("");
					 lengthL.setText("");
					 loadDataL.setText("");
					 
					 // make sure this is reset if no items in list
					 sequence = null;
					 
					 return;
				}
				
				sequence = ObjectHandles.getSequence(id);
				
				updateAttributes();
				
				// this doesn't work because the panel won't register that
				// 'tracks' now points to a different list.
				//		  tracks = ObjectHandles.getTrackBySequenceList(sequence.getID());
				Set tmpTrack = ObjectHandles.getTrackBySequenceList(sequence.getID());
				if (tmpTrack != null) { tracks = tmpTrack.toArray(); }
				else { tracks = new Object[0]; }
				trackL.setListData(tracks);
				
				offsetL.setText(Integer.toString(sequence.getOffset()));
				minL.setText(Integer.toString(sequence.getMin()));
				if (sequence.isDataLoaded()) {
					 maxL.setText(Integer.toString(sequence.getMax()));
					 lengthL.setText(Integer.toString(sequence.length()));
				} else {
					 // don't force the loaded of data here
					 maxL.setText("n/a");
					 lengthL.setText("n/a");
				}
				loadDataL.setText(Boolean.toString(sequence.isDataLoaded()));
		  }
		  
		  /**
			* This is separate from sequenceSelected() so it can be called
			* separately for when the attributes are changed in the attribute
			* panel.
			*/
		  private void updateAttributes() {
				// don't do anything if sequence not set
				if (sequence == null) return;
				
				HashMap attributes = sequence.getAttributes();
				if ((attributes == null) || (attributes.size() == 0)) {
					 attributeL.setListData(new String[0]);
				} else {
					 String[] attributeArray = new String[attributes.size()];
					 Set keys = attributes.keySet();
					 int cnt = 0;
					 for (Iterator i = keys.iterator(); i.hasNext();) {
						  String key = (String) i.next();
						  attributeArray[cnt] = key + ": " + attributes.get(key);
						  cnt++;
					 }
					 attributeL.setListData(attributeArray);
				}
		  }
		  
		  /** 
			* Displays the Sequence data. 
			* @XXX Should see about using FieldEditDialog here.
			*/
		  class EditSourcePanel extends JFrame {
				EditSourcePanel thisFrame;

				JTextField loaderTF = new JTextField(20);
				JTextField argsTF = new JTextField(20);

				public EditSourcePanel() {
					 super("Sequence Data");
					 
					 // keep pointer to self so can 'dispose' Frame below
					 thisFrame = this;
					 
					 setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					 
					 // setup text area to display data
					 JPanel labelP = new JPanel(new GridLayout(0,1));
					 JPanel editP = new JPanel(new GridLayout(0,1));
					 labelP.add(new JLabel(" Source loader:"));
					 labelP.add(new JLabel(" Loader arguments:"));
					 updateValues();
					 editP.add(loaderTF);
					 editP.add(argsTF);
					 JPanel contentP = new JPanel(new BorderLayout());
					 contentP.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
					 contentP.add(labelP, BorderLayout.WEST);
					 contentP.add(editP, BorderLayout.CENTER);
					 
					 // button panel
					 JPanel buttonP = new JPanel(new BorderLayout());
					 buttonP.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
					 JButton editB = new JButton("Edit");
					 editB.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 if (sequence != null) {
										  GUIUtils.newSequenceLoader(sequence.getID());
										  updateValues();
									 }
								}
						  });
					 buttonP.add(editB, BorderLayout.WEST);
					 JButton closeB = new JButton("Close");
					 closeB.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 thisFrame.dispose();
								}
						  });
					 buttonP.add(closeB, BorderLayout.EAST);
					 
					 getContentPane().setLayout(new BorderLayout());
					 getContentPane().add(contentP, BorderLayout.NORTH);
					 getContentPane().add(buttonP, BorderLayout.SOUTH);
					 pack();
					 
					 // set the default window size
					 setSize(getSize().width + 100, getSize().height + 30);
					 
					 // display the window
					 //				setVisible(true);
					 show();
				}

				private void updateValues() {
					 SequenceLoader sl = sequence.getDataLoader();
					 if (sl == null) {
						  loaderTF.setText("not set");
					 } else {
						  loaderTF.setText(sl.toString());
					 }
					 loaderTF.setEditable(false);
					 argsTF.setText((sequence.getLoaderArgs()).toString());
					 argsTF.setEditable(false);
				}
		  }
		  
		  /** Displays the Sequence data. */
		  class ViewSequencePanel extends JFrame {
				ViewSequencePanel thisFrame;
				
				public ViewSequencePanel() {
					 super("Sequence Data");
					 
					 // keep pointer to self so can 'dispose' Frame below
					 thisFrame = this;
					 
					 setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					 
					 // setup text area to display data
					 JTextArea textArea = new JTextArea(sequence.getDataFormatted());
					 textArea.setLineWrap(false);
					 textArea.setWrapStyleWord(false);
					 textArea.setEditable(false);
					 textArea.setFont(new Font("Courier", Font.PLAIN, 14));
					 JScrollPane areaSP = new JScrollPane(textArea);
					 areaSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					 areaSP.setPreferredSize(new Dimension(580, 300));
					 
					 // setup close button
					 JButton closeB = new JButton("Close Viewer");
					 closeB.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 thisFrame.dispose();
								}
						  });
					 
					 getContentPane().setLayout(new BorderLayout());
					 getContentPane().add(areaSP, BorderLayout.CENTER);
					 getContentPane().add(closeB, BorderLayout.SOUTH);
					 pack();
					 
					 // set the default window size
					 setSize(getSize().width + 100, getSize().height + 30);
					 
					 // display the window
					 //				setVisible(true);
					 show();
				}
		  }
		  
		  /** Handle newSequence dialog. */
		  class newSequencePanel extends JFrame {
				JTextField idTF;
				newSequencePanel thisFrame;
				
				public newSequencePanel() {
					 super("New Sequence");
					 
					 // keep pointer to self so can 'dispose' Frame below
					 thisFrame = this;
					 
					 setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					 
					 // entry panel
					 JPanel labelP = new JPanel(new GridLayout(0,1));
					 JPanel editP = new JPanel(new GridLayout(0,1));
					 idTF = new JTextField(20);
					 labelP.add(new JLabel(" ID:"));
					 editP.add(idTF);
					 JPanel contentP = new JPanel(new BorderLayout());
					 contentP.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
					 contentP.add(labelP, BorderLayout.WEST);
					 contentP.add(editP, BorderLayout.CENTER);
					 
					 // button panel
					 JPanel buttonP = new JPanel(new BorderLayout());
					 buttonP.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
					 JButton addB = new JButton("Create");
					 addB.setActionCommand("Create");
					 addB.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 String id = idTF.getText().trim();
									 
									 String msg = "newSequence(\"" + id + "\")";
									 
									 // add command to history but don't run via console.
									 // instead we're running it here so we can return a
									 // reference to the Set of Sequences loaded
									 Root.runCommand(msg, false);
									 
									 // create new sequence here
									 sequence = new Sequence(id);
									 // don't use 'id' here because it will have
									 // been changed when creating the Sequence, if
									 // the ID already existed
									 sequenceCB.setSelectedItem(sequence.getID());
									 
									 thisFrame.dispose();									 
								}
						  });
					 buttonP.add(addB, BorderLayout.WEST);
					 JButton cancelB = new JButton("Cancel");
					 cancelB.setActionCommand("Cancel");
					 cancelB.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 thisFrame.dispose();
								}
						  });
					 buttonP.add(cancelB, BorderLayout.EAST);
					 
					 getContentPane().setLayout(new BorderLayout());
					 getContentPane().add(contentP, BorderLayout.NORTH);
					 getContentPane().add(buttonP, BorderLayout.SOUTH);
					 pack();
					 
					 // set the default window size
					 setSize(getSize().width + 100, getSize().height + 30);
					 
					 // display the window
					 //				setVisible(true);
					 show();
				}
		  }

		  public void selectSequence(String id) {
				if ((id.length() == 0) || (sequenceCB.getItemCount() == 0)) return;
				int index = ObjectHandles.getSequenceList().getIndexOf(id);
				// only change selection if valid ID
				if (index > -1) {
					 sequenceCB.setSelectedIndex(index);
				} else {
					 GloDBUtils.printMsg("Invalid sequence id (\"" + id + "\").", GloDBUtils.WARNING);
				}
		  }
	 }
} // SequenceBrowser.java
