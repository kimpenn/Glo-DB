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
 * @(#)TrackBrowser.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Browse existing Tracks.
 *
 * @author  Stephen Fisher
 * @version $Id: TrackBrowser.java,v 1.1.2.20 2007/03/01 21:17:33 fisher Exp $
 */

public class TrackBrowser {
	 private static BrowserFrame browserFrame = null;

	 public static JFrame show() {
		  return show("");
	 }

    public static JFrame show(String id) {
		  // only allow one instance of BrowserFrame
		  if (browserFrame == null) browserFrame = new BrowserFrame();

		  browserFrame.selectTrack(id);
		  browserFrame.show();

		  return browserFrame;
	 }
		  
	 private static class BrowserFrame extends JFrame {
		  /** 
			* This is the total number of features that will be displayed in
			* the feature list.
			*/
		  private final long MAX_FEATURES = 40000;

		  private JComboBox trackCB;
		  private JList attributeL;
		  private JButton addAttributeB;
		  private JButton editAttributeB;
		  private JButton delAttributeB;
		  private JList sequenceL;
		  private JList featureL;
		  private JLabel statusBar;
		  
		  private JLabel numFeaturesL = new JLabel("");
		  private JLabel contiguousL = new JLabel("");
		  private JLabel minL = new JLabel("");
		  private JLabel maxL = new JLabel("");
		  private JLabel lengthL = new JLabel("");
		  
		  private Track track;
		  private Object[] sequences;
		  private Object[] features;
		  
		  BrowserFrame thisFrame;
		  
		  //		  public BrowserFrame(String id) {
		  public BrowserFrame() {
				super("Track Browser");
				// keep pointer to self so can 'dispose' Frame below
				thisFrame = this;
				
				setDefaultCloseOperation(HIDE_ON_CLOSE);
				
				// ***** SETUP TRACK INFO *****
				JToolBar trackP = new JToolBar();
				trackP.setFloatable(false);
				trackP.setBorder(BorderFactory.createEtchedBorder());
				// add Track ComboBox
				trackCB = new JComboBox(ObjectHandles.getTrackList());
				trackCB.setEditable(false);
				//				selectTrack("");
				trackCB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								String selection = (String) ((JComboBox) e.getSource()).getSelectedItem();
								trackSelected(selection);
						  }
					 });
				// add Buttons
				JButton loadB = new JButton(new ImageIcon("icons/load.png"));
				loadB.setToolTipText("Load");
				loadB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Load track"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				loadB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
								setCursor(hourglassCursor);
								Track track = GUITrackIO.loadTrack();
								Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
								setCursor(normalCursor);
								if (track != null) {
									 trackCB.setSelectedIndex(ObjectHandles.getTrackList().getIndexOf(track.getID()));
								}
						  }
					 });
				JButton saveB = new JButton(new ImageIcon("icons/save.png"));
				saveB.setToolTipText("Save");
				saveB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Save track"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				saveB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track != null) GUITrackIO.saveTrack(track.getID());
						  }
					 });
				JButton viewB = new JButton(new ImageIcon("icons/view.png"));
				viewB.setToolTipText("View Data");
				viewB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("View track data"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				viewB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track != null) new ViewSequencePanel();
						  }
					 });
				// add info "rename" button
				JButton renameB= new JButton(new ImageIcon("icons/rename.png"));
				renameB.setToolTipText("Rename");
				renameB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Rename track"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				renameB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track != null) {
									 String id = GUIUtils.renameTrack(track.getID());
									 trackCB.setSelectedIndex(ObjectHandles.getTrackList().getIndexOf(id));
								}
						  }
					 });
				// add info "remove" button
				JButton deleteB= new JButton(new ImageIcon("icons/delete.png"));
				deleteB.setToolTipText("Delete");
				deleteB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("THIS WILL PERMANENTLY DELETE THE TRACK!"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				deleteB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track == null) return;  
								
								String msg = "Are you sure you want to remove this track?\n";
								msg += "Track: " + track.getID();
								Object[] options = {"Delete Track", "Cancel"};
								int flag = JOptionPane.showOptionDialog(null, msg,
																					 "Delete Confirmation",
																					 JOptionPane.YES_NO_OPTION,
																					 JOptionPane.QUESTION_MESSAGE,
																					 null,
																					 options,
																					 options[1]);
								if (flag == JOptionPane.YES_OPTION) { // "Delete"
									 Root.runCommand("removeTrack(\"" + track.getID() + "\")", true);
									 //									 ObjectHandles.removeTrack(track);
									 if (trackCB.getItemCount() > 0) {
										  trackCB.setSelectedIndex(0);
									 } else {
										  trackSelected(null);
									 }
								}
						  }
					 });
				JButton closeB = new JButton(new ImageIcon("icons/close.png"));
				closeB.setToolTipText("Close");
				closeB.addMouseListener(new MouseAdapter() {
						  // add status bar text when mouse moves over button
						  public void mouseEntered(MouseEvent e) { statusBar.setText("Close the track browser"); }
						  // clear status bar when mouse moves off button
						  public void mouseExited(MouseEvent e) { statusBar.setText(""); }
					 });
				closeB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								thisFrame.hide();
						  }
					 });
				// add Track Label
				trackP.addSeparator(new Dimension(15, 0));
				trackP.add(loadB);
				trackP.addSeparator(new Dimension(30, 25));
				trackP.add(saveB);
				trackP.addSeparator(new Dimension(30, 25));
				trackP.add(new JLabel("Track: "));
				trackP.add(trackCB);
				trackP.addSeparator(new Dimension(15, 0));
				trackP.add(viewB);
				trackP.addSeparator(new Dimension(30, 25));
				trackP.add(renameB);
				trackP.addSeparator(new Dimension(30, 25));
				trackP.add(deleteB);
				trackP.addSeparator(new Dimension(30, 25));
				trackP.add(closeB);
				trackP.addSeparator(new Dimension(15, 0));
				
				// ***** SETUP ATTRIBUTE INFO *****
				JPanel attributeP = new JPanel(new BorderLayout());
				// add attributes List
				attributeL = new JList();
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
				addAttributeB.setToolTipText("Add New Attribute");
				addAttributeB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track == null) return;
								
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
									 String msg = "getTrack(\"" + track.getID() + "\")";
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
								value = st.nextToken();  // this is the actual value
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
									 String msg = "getTrack(\"" + track.getID() + "\")";
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
								String msg = "Are you sure you want to delete the following attribute?\n";
								msg += "   " + attributeL.getSelectedValue();
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
									 msg = "getTrack(\"" + track.getID() + "\")";
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
				
				// ***** SETUP TRACK SPECs *****
				JPanel infoP = new JPanel(new BorderLayout());
				// add info sub-panel
				JPanel infoSP = new JPanel(new GridLayout(5,2,5,5));
				infoSP.add(new JLabel(" Min:"));
				infoSP.add(minL);
				infoSP.add(new JLabel(" Max:"));
				infoSP.add(maxL);
				infoSP.add(new JLabel(" Length:"));
				infoSP.add(lengthL);
				infoSP.add(new JLabel(" Num Features:"));
				infoSP.add(numFeaturesL);
				infoSP.add(new JLabel(" Contiguous:"));
				infoSP.add(contiguousL);
				infoP.add(infoSP, BorderLayout.CENTER);
				// add button sub-panel
				JPanel infoBP = new JPanel(new GridLayout(0,1,5,5));
				// add info "contiguous" button
				JButton contiguousB = new JButton("Compute Contiguity");
				contiguousB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track != null) {
									 contiguousL.setText(Boolean.toString(track.isContiguous()));
								}
						  }
					 });
				infoBP.add(contiguousB);
				// add info "mergeContiguous" button
				JButton mergeContiguousB = new JButton("Merge Contiguous");
				mergeContiguousB.addActionListener(new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
								if (track == null) return;  
								
								String msg = "Are you sure you want to merge all contiguous features?\n";
								msg += "This can not be undone.";
								Object[] options = {"Merge", "Cancel"};
								int flag = JOptionPane.showOptionDialog(null, msg,
																					 "Merge Confirmation",
																					 JOptionPane.YES_NO_OPTION,
																					 JOptionPane.QUESTION_MESSAGE,
																					 null,
																					 options,
																					 options[1]);
								if (flag == JOptionPane.YES_OPTION) { // "Merge"
									 // don't know why but the track order changes so
									 // need to reselect this track.  thus save the
									 // ID and contiguous info.
									 String id = track.getID();
									 String contiguous = contiguousL.getText();
									 
									 // XXX this should have a popup dialog that
									 // confirms this action
									 track.mergeContiguous();
									 
									 // reset the track
									 trackCB.setSelectedIndex(ObjectHandles.getTrackList().getIndexOf(id));
									 contiguousL.setText(contiguous);
								}
						  }
					 });
				infoBP.add(mergeContiguousB);
				// add info "flip" button
				/*
				  JButton flipB= new JButton("Flip Features");
				  flipB.addActionListener(new ActionListener() {
				  public void actionPerformed(ActionEvent e) {
				  if (track == null) return;  
				  GloDBUtils.printMsg("button not yet implemented");
				  }
				  });
				  infoBP.add(flipB);
				*/
				infoP.add(infoBP, BorderLayout.SOUTH);
				
				// ***** SETUP SEQUENCE INFO *****
				JPanel sequenceP = new JPanel(new BorderLayout());
				// setup list of Sequences for selected Track
				sequenceL = new JList();
				sequenceL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				sequenceL.setToolTipText("Double click on a Sequence name to edit the item in a Sequence Browser");
				/*
				  sequenceL.addListSelectionListener(new ListSelectionListener() {
				  public void valueChanged(ListSelectionEvent e) {
				  if (e.getValueIsAdjusting()) return;
				  
				  JList list = (JList) e.getSource();
				  if (list.isSelectionEmpty()) {
				  statusBar.setText("");
				  } else {
				  Sequence sequence = ObjectHandles.getSequence((String) sequences[list.getSelectedIndex()]);
				  String msg = "Sequence ID: " + sequence.getID();
				  msg += " :: Length: " + sequence.length();
				  msg += " :: Attributes: " + sequence.getAttributes();
				  statusBar.setText(msg);
				  }
				  }
				  });
				*/
				sequenceL.addMouseMotionListener(new MouseMotionListener() {
						  public void mouseMoved(MouseEvent e) {
								int index = sequenceL.locationToIndex(e.getPoint());
								if (index > -1) {  // make sure there is a valid item
									 Sequence sequence = ObjectHandles.getSequence((String) sequences[index]);
									 String msg = "Sequence ID: " + sequence.getID();
									 if (sequence.isDataLoaded()) {
										  // only include length if data already
										  // loaded, else this will cause the
										  // Sequence data to be loaded which can
										  // take a long time
										  msg += " :: Length: " + sequence.length();
									 }
									 msg += " :: Attributes: " + sequence.getAttributes();
									 statusBar.setText(msg);
								}
						  }
						  public void mouseDragged(MouseEvent e) { ; }
					 });
				sequenceL.addMouseListener(new MouseAdapter() {
						  // clear status bar when mouse moves out of sequenceL
						  public void mouseExited(MouseEvent e) {
								statusBar.setText("");
						  }
						  
						  public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 2) {
									 int index = sequenceL.locationToIndex(e.getPoint());
									 
									 if (index > -1) {  // make sure there is a valid item
										  // launch the Sequence Browser through Root so
										  // that the command gets added to the history.
										  String id = (String) sequences[index];
										  String cmd = "sequenceBrowser(\"" + id + "\")";
										  Root.runCommand(cmd, true);
										  /*
											 String msg = (ObjectHandles.getSequence((String) sequences[index])).toString();
											 JOptionPane.showMessageDialog(null, msg,
											 "Sequence Viewer",
											 JOptionPane.INFORMATION_MESSAGE);
										  */
									 }
								}
						  }
					 });
				JScrollPane sequenceSP = new JScrollPane(sequenceL);
				sequenceP.add(sequenceSP, BorderLayout.CENTER);
				// add Sequence Label
				sequenceP.add(new JLabel(" Sequences:"), BorderLayout.NORTH);
				sequenceP.add(infoP, BorderLayout.SOUTH);
				
				// ***** SETUP FEATURE INFO *****
				JPanel featureP = new JPanel(new BorderLayout());
				// setup list of Features for selected Track
				featureL = new JList();
				featureL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				featureL.setToolTipText("Double click on a Feature name to view the item's information");
				/*
				  featureL.addListSelectionListener(new ListSelectionListener() {
				  public void valueChanged(ListSelectionEvent e) {
				  if (e.getValueIsAdjusting()) return;
				  
				  JList list = (JList) e.getSource();
				  if (list.isSelectionEmpty()) {
				  GloDBUtils.printMsg("empty");
				  } else {
				  GloDBUtils.printMsg(list.getSelectedIndex());
				  }
				  }
				  });
				*/
				featureL.addMouseListener(new MouseAdapter() {
						  public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 2) {
									 int index = featureL.locationToIndex(e.getPoint());
									 if (index > -1) {  // make sure there is a valid item
										  String msg = ((Feature) features[index]).toStringFull();
										  JOptionPane.showMessageDialog(null, msg,
																				  "Feature Viewer",
																				  JOptionPane.INFORMATION_MESSAGE);
									 }
								}
						  }
					 });
				JScrollPane featureSP = new JScrollPane(featureL);
				featureP.add(featureSP, BorderLayout.CENTER);
				// add Features Label
				featureP.add(new JLabel(" Features:"), BorderLayout.NORTH);
				
				// setup split pane for Sequence/Feature Panels
				JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
																  sequenceP, featureP);
				splitPane.setOneTouchExpandable(true);
				splitPane.setDividerLocation(200);
				// provide minimum sizes for the two components in the split pane
				Dimension minimumSize = new Dimension(150, 200);
				sequenceP.setMinimumSize(minimumSize);
				featureP.setMinimumSize(minimumSize);
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
				
				getContentPane().add(trackP, BorderLayout.NORTH);
				getContentPane().add(attributeP, BorderLayout.WEST);
				getContentPane().add(splitPane, BorderLayout.CENTER);
				getContentPane().add(statusBarP, BorderLayout.SOUTH);
				pack();
				
				// set the default window size
				setSize(getSize().width + 100, getSize().height + 150);
				
				// initialize the browser to the current Track
				trackSelected((String) trackCB.getSelectedItem());
				
				// display the window
				show();
		  }
		  
		  /** 
			* This will update the TrackBrowser for the currently selected
			* Track.
			*/
		  private void trackSelected(String id) {
				if ((id == null) || (trackCB.getItemCount() == 0)) {
					 // if null item or empty list, then clear display
					 sequences = new Object[0];
					 sequenceL.setListData(sequences);
					 features = new Object[0];
					 featureL.setListData(features);
					 attributeL.setListData(new String[0]);
					 
					 minL.setText("");
					 maxL.setText("");
					 lengthL.setText("");
					 numFeaturesL.setText("");
					 contiguousL.setText("uncomputed");
					 
					 // make sure this is reset if no items in list
					 track = null;
					 
					 return;
				}
				
				track = ObjectHandles.getTrack(id);
				
				updateAttributes();
				Set tmpSeq = track.getSourceSet();
				if (tmpSeq != null) { sequences = tmpSeq.toArray(); }
				else { sequences = new Object[0]; }
				sequenceL.setListData(sequences);
				
				if (track.numFeatures() < MAX_FEATURES) {
					 TreeSet tmpFeat = track.getFeatures();
					 if (tmpFeat != null) { features = tmpFeat.toArray(); }
					 else { features = new Object[0]; }
				} else {
					 features = new Object[0];

					 String msg = "Too many features to display in the feature selection list."; 
					 GloDBUtils.printMsg(msg, GloDBUtils.WARNING);
				}
				featureL.setListData(features);
				
				minL.setText(Integer.toString(track.getMin()));
				maxL.setText(Integer.toString(track.getMax()));
				lengthL.setText(Integer.toString(track.length()));
				numFeaturesL.setText(Integer.toString(track.numFeatures()));
				contiguousL.setText("uncomputed");
		  }
		  
		  /**
			* This is separate from trackSelected() so it can be called
			* separately for when the attributes are changed in the attribute
			* panel.
			*/
		  private void updateAttributes() {
				// don't do anything if track not set
				if (track == null) return;
				
				HashMap attributes = track.getAttributes();
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
		  
		  /** Displays the Sequence data. */
		  class ViewSequencePanel extends JFrame {
				ViewSequencePanel thisFrame;
				
				public ViewSequencePanel() {
					 super("Sequence Data");
					 
					 // keep pointer to self so can 'dispose' Frame below
					 thisFrame = this;
					 
					 setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					 
					 // setup text area to display data
					 JTextArea textArea = new JTextArea(track.getDataFASTA());
					 if (textArea.getText().length() == 0) {
						  textArea.setText("No data, check sequence data source");
					 }
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
					 show();
				}
		  }

		  public void selectTrack(String id) {
				if ((id.length() == 0) || (trackCB.getItemCount() == 0)) return;
				int index = ObjectHandles.getTrackList().getIndexOf(id);
				// only change selection if valid ID
				if (index > -1) {
					 trackCB.setSelectedIndex(index);
				} else {
					 GloDBUtils.printMsg("Invalid track id (\"" + id + "\").", GloDBUtils.WARNING);
				}
		  }
	 }
} // TrackBrowser.java
