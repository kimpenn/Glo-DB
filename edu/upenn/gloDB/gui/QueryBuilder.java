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
 * @(#)QueryBuilder.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.util.Iterator;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * Browse existing Tracks.
 *
 * @author  Stephen Fisher
 * @version $Id: QueryBuilder.java,v 1.1.2.35 2007/03/01 21:17:33 fisher Exp $
 */

public class QueryBuilder extends JFrame {
	 //	 private final String[] OPERATORS = { "Intersection: AND", "Intersection (strict): sAND", "Intersection (bitwise): &&", "Union: OR", "Union (bitwise): ||", "Relative complement: MINUS", "Relative complement (strict): sMINUS", "Relative complement (bitwise): -", "Order: POS", "Order (bitwise): ." };
	 private final String[] OPERATORS = { "Intersection: AND", "Intersection (strict): sAND", "Intersection (bitwise): &&", "Union: OR", "Union (bitwise): ||", "Relative complement: MINUS", "Relative complement (strict): sMINUS", "Relative complement (bitwise): -", "Order: POS" };

	 private JTextField queryTF;
	 private JTable table;
	 private ListTableModel tableModel;
	 private String trackID = "";
	 
	 private JButton updateB;
	 private JButton groupB;
	 private JButton ungroupB;
	 private JButton deleteB;
	 private JButton deleteAllB;
	 private JButton copyB;
	 private JButton computeB;
	 private JButton newB;

	 private JList operatorL;
	 private JCheckBox negateChB;
	 private JList trackL;
	 private JCheckBox sequenceChB;
	 private JComboBox sequenceCB;

	 // these aren't forced to be integers here because then the user
	 // would need to know that '-1' is the default value and as
	 // strings we can just use the empty string.
	 private JTextField minWidthTF;
	 private JTextField maxWidthTF;
	 private JTextField minSeqPosTF;
	 private JTextField maxSeqPosTF;
	 private JTextField minRepeatTF;
	 private JTextField maxRepeatTF;
	 private JTextField minWithinTF;
	 private JTextField maxWithinTF;
	 private JTextField minPosTF;
	 private JTextField maxPosTF;

	 QueryBuilder thisFrame;

	 public QueryBuilder() {
		  this("");
	 }

    public QueryBuilder(String id) {
        super("Query Builder");

		  // keep pointer to self so can 'dispose' Frame below
		  thisFrame = this;

		  setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		  // create various borders
		  Border emptyBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		  Border emptyBorder5 = BorderFactory.createEmptyBorder(5,5,5,5);
		  Border lowBevelBorder = BorderFactory.createLoweredBevelBorder();
		  Border etchedBorder = BorderFactory.createEtchedBorder();
		  Border tmpBorder = BorderFactory.createCompoundBorder(emptyBorder, lowBevelBorder);
		  // create a lower bevel border with outside and inside padding
		  Border lowBevelPadBorder = BorderFactory.createCompoundBorder(tmpBorder, emptyBorder);
		  // create an etched border with inside padding
		  //		  Border etchedPadBorder = BorderFactory.createCompoundBorder(etchedBorder, emptyBorder);
		  // create an etched border with no top padding, for use with titles
		  Border emptyBorder0 = BorderFactory.createEmptyBorder(0,10,10,10);
		  Border etchedPadBorder0 = BorderFactory.createCompoundBorder(etchedBorder, emptyBorder0);


		  // *************************************************
		  // create query panel
		  JPanel queryP = new JPanel(new BorderLayout());
		  queryP.setBorder(lowBevelPadBorder);

		  // *************************************************
		  // create text composition panel
		  JPanel textP = new JPanel(new BorderLayout(10,0));
		  //		  textP.setBorder(BorderFactory.createLoweredBevelBorder());
		  textP.setBorder(emptyBorder5);
		  queryTF = new JTextField(50);
		  queryTF.setEditable(false);
		  JButton queryB = new JButton("Change Output Track ID");
		  queryB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  setID(getID());
					 }
				});
		  textP.add(new JLabel(" Query: "), BorderLayout.WEST);
		  textP.add(queryTF, BorderLayout.CENTER);
		  textP.add(queryB, BorderLayout.EAST);

		  // *************************************************
		  // TABLE SETUP
		  tableModel = new ListTableModel();
		  table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		  table.setColumnSelectionAllowed(false);
		  //		  table.setShowGrid(false);
		  //		  table.setShowHorizontalLines(false);
		  table.setShowVerticalLines(false);
		  // XXX this will hopefully disallow moving cols 
		  table.setDragEnabled(false);  
        //Create the scroll pane and add the table to it.
        JScrollPane tableSP = new JScrollPane(table);
		  /*
        //Set up tool tip for Track column
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        trackTC.setCellRenderer(renderer);
		  */
		  TableColumn column = null;
		  for (int i = 0; i < tableModel.getColumnCount(); i++) {
				column = table.getColumnModel().getColumn(i);
				// first 2 columns should be smaller
				if (i < 2) column.setPreferredWidth(10); 
				else column.setPreferredWidth(100); 
		  }
		  // END TABLE SETUP
		  // *************************************************

		  // *************************************************
		  // create panel with list
		  DefaultListSelectionModel dlsm = new DefaultListSelectionModel();
		  table.setSelectionModel(dlsm);
		  // don't allow discontinuous selection
		  dlsm.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		  dlsm.addListSelectionListener(new ListSelectionListener() {
					 public void valueChanged(ListSelectionEvent e) {
						  if (e.getValueIsAdjusting()) return;

						  if (table.getSelectedRowCount() == 0) {
								// no selection so disable buttons 
								updateB.setEnabled(false);
								groupB.setEnabled(false);
								ungroupB.setEnabled(false);
								deleteB.setEnabled(false);

								updateEditPanel(null);

								// don't allow operator if first item
								if (tableModel.getRowCount() == 0) { 
									 operatorL.setEnabled(false);
									 operatorL.clearSelection();
								} else {
									 operatorL.setEnabled(true);
								}
						  } else {
								// we know that at least 1 row is selected
								QueryElement qElement = tableModel.getRow(table.getSelectedRow());
								updateEditPanel(qElement);

								// enable buttons if selection
								groupB.setEnabled(true);
								deleteB.setEnabled(true);

								// only enable the follwoing buttons if 1 item
								// is selected.  For ungroupB, if only 1
								// selected item, then only enable if group.
								// If multiple selected items then just leave
								// enabled.
								if (table.getSelectedRowCount() == 1) {
									 // only enable the appropriate button
									 if (qElement.isGrouped()) {
										  ungroupB.setEnabled(true);
									 } else {
										  ungroupB.setEnabled(false);
									 }
									 updateB.setEnabled(true);
								} else {
									 ungroupB.setEnabled(true);
									 updateB.setEnabled(false);
									 trackL.clearSelection();
								}
						  }
					 }
				});

		  // *************************************************
		  // create panel with control button
		  JPanel controlP = new JPanel(new GridLayout(1,0,200,5));
		  controlP.setBorder(emptyBorder5);
		  // add delete button
		  deleteB = new JButton("Delete Selected Elements(s)");
		  deleteB.setEnabled(false);
		  deleteB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  tableModel.removeRows(table.getSelectedRows());
						  updateQuery();
					 }
				});
		  controlP.add(deleteB);
		  // add clear button
		  deleteAllB = new JButton("Delete All Elements");
		  deleteAllB.setEnabled(false);
		  deleteAllB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  String msg = "Are you sure you want to delete all items?";
						  Object[] options = {"Delete", "Cancel"};
						  int flag = JOptionPane.showOptionDialog(null, msg,
																				 "Delete Confirmation",
																				 JOptionPane.YES_NO_OPTION,
																				 JOptionPane.QUESTION_MESSAGE,
																				 null,
																				 options,
																				 options[1]);
						  if (flag == JOptionPane.YES_OPTION) { // "Delete"
								tableModel.removeAllRows();
								updateQuery();
						  }
					 }
				});
		  controlP.add(deleteAllB);

		  queryP.add(textP, BorderLayout.NORTH);
		  queryP.add(tableSP, BorderLayout.CENTER);
		  queryP.add(controlP, BorderLayout.SOUTH);

		  // *************************************************
		  // ************ EDIT PANEL *************
		  JPanel editP = new JPanel(new BorderLayout());
		  editP.setBorder(lowBevelPadBorder);

		  // ********* EDIT BUTTON PANEL ************
		  JPanel buttonP = new JPanel(new GridLayout(1,0,10,10));
		  buttonP.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		  // add 'new' button
		  newB = new JButton("Insert Track");
		  newB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  QueryElement qElement = new QueryElement((String) trackL.getSelectedValue());
						  updateQueryElement(qElement);

						  int[] selection = table.getSelectedRows();
						  if (selection.length == 0) { // nothing selected, append to end
								tableModel.addRow(qElement);
						  } else {  // insert track after current selection
								int index = selection[selection.length - 1];
								if (index == tableModel.getRowCount()) {
									 // last row selected so just append
									 tableModel.addRow(qElement);
								} else {
									 tableModel.addRowAt(qElement, index + 1);
								}
						  }
						  updateQuery();
						  updateEditPanel(null);  // reset the query panel
					 }
				});
		  buttonP.add(newB);
		  // add group button
		  groupB = new JButton("Group Selected Element(s)");
		  groupB.setEnabled(false);
		  groupB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  // get selected elements to put into group
						  int[] rows = table.getSelectedRows();

						  // store where the new group goes
						  int row = rows[0];

						  //make new group
						  QueryElement qElement = new QueryElement();
						  updateQueryElement(qElement);

						  // remove operator from first element in group
						  QueryElement firstQE = tableModel.getRow(row);
						  //						  firstQE.operator = "";
						  firstQE.operator = -1;

						  // build group element
						  for (int i = 0; i < rows.length; i++) {
								qElement.addToGroup(tableModel.getRow(rows[i]));
						  }

						  // remove grouped elements from table
						  tableModel.removeRows(rows);

						  // add group element to table
						  tableModel.addRowAt(qElement, row);

						  updateQuery();
						  updateEditPanel(null);  // reset the query panel
					 }
				});
		  buttonP.add(groupB);
		  // add ungroup button
		  ungroupB = new JButton("Ungroup Selected Element(s)");
		  ungroupB.setEnabled(false);
		  ungroupB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  // get selected elements to ungroup
						  int[] rows = table.getSelectedRows();
						  // store where the new elements go
						  int row = rows[0];

						  // build an array containing all the selected
						  // items, removing the items from the table. This
						  // array is in reverse order and will be inverted
						  // when the items are ungrouped and added back to
						  // the table.
						  ArrayList selected = new ArrayList();
						  int maxRow = rows[rows.length-1];
						  while (maxRow >= row) {
								selected.add(tableModel.getRow(maxRow));
								tableModel.removeRow(maxRow--);
						  }

						  // by adding all of the ungrouped items at the
						  // same index location 'row', we are effectively
						  // inverting 'selected'
						  for (Iterator i = selected.iterator(); i.hasNext();) {
								QueryElement qElement = (QueryElement) i.next();
								// only 'ungroup' if actually a group
								if (qElement.isGrouped()) {
									 // move operator from group to first element
									 QueryElement firstQE = (QueryElement) qElement.getGroup().get(0);
									 firstQE.operator = qElement.operator;

									 // add ungrouped items
									 int index = row;
									 for (Iterator it = qElement.groupIterator(); it.hasNext();) {
										  tableModel.addRowAt((QueryElement) it.next(), index++);
									 }
								} else {
									 // not a group so just add the item
									 tableModel.addRowAt(qElement, row);
								}
						  }

						  updateQuery();
					 }
				});
		  buttonP.add(ungroupB);
		  // add update button
		  updateB = new JButton("Update Selected Element");
		  updateB.setEnabled(false);
		  updateB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  QueryElement qElement = tableModel.getRow(table.getSelectedRow());
						  updateQueryElement(qElement);
						  if (! qElement.isGrouped()) {
								qElement.track = (String) trackL.getSelectedValue();
						  }
						  updateQuery();

						  // need to tell table to redraw
						  tableModel.fireTableDataChanged();
					 }
				});
		  buttonP.add(updateB);

		  // *************************************************
		  // ************* OPTION PANELS
		  JPanel optionsP = new JPanel(new GridLayout(1,0,5,0));

		  // create Operators panel with titled, etched, padded border
		  JPanel operatorP = new JPanel(new BorderLayout());
		  //		  JPanel operatorP = new JPanel(new GridLayout(0,1,0,5));
		  operatorP.setBorder(BorderFactory.createTitledBorder(etchedPadBorder0, "Operators"));
		  operatorL = new JList(OPERATORS);
		  operatorL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		  operatorL.setEnabled(false);
		  operatorL.clearSelection();
		  operatorL.addListSelectionListener(new ListSelectionListener() {
					 public void valueChanged(ListSelectionEvent e) {
						  if (e.getValueIsAdjusting()) return;
						  if (operatorL.getSelectedIndex() == -1) { // nothing selected
								minPosTF.setEnabled(false);
						  } else {
								// test if order operator ('POS'/'.')
								if (QueryElement.isOrderOperator(operatorL.getSelectedIndex())) {
									 minPosTF.setEnabled(true);
								} else { 
									 minPosTF.setEnabled(false);
									 maxPosTF.setEnabled(false);
								}
						  }
					 }
				});
        JScrollPane operatorSP = new JScrollPane(operatorL);

		  JPanel orderedP = new JPanel(new GridLayout(1,0));
		  orderedP.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
		  PlainDocument minPosPD = new PlainDocument();
		  minPosPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minPosTF.getText().trim());
								maxPosTF.setEnabled(minPosTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Order lower bound must be an integer.");
								maxPosTF.setEnabled(false);
						  }
					 }
					 public void removeUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minPosTF.getText().trim());
								maxPosTF.setEnabled(minPosTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								// don't need to again show error message,
								// just disable maxPosTF
								maxPosTF.setEnabled(false);
						  }
					 }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  minPosTF = new JTextField(minPosPD, "", 0);
		  minPosTF.setEnabled(false);
		  orderedP.add(new JLabel("Order (bp):"));
		  orderedP.add(minPosTF);
		  orderedP.add(new JLabel("to", JLabel.CENTER));
		  PlainDocument maxPosPD = new PlainDocument();
		  maxPosPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(maxPosTF.getText().trim());
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Order upper bound must be an integer.");
						  }
					 }
					 public void removeUpdate(DocumentEvent e) { ; }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  maxPosTF = new JTextField(maxPosPD, "", 0);
		  maxPosTF.setEnabled(false);
		  orderedP.add(maxPosTF);

		  operatorP.add(operatorSP, BorderLayout.CENTER);
		  operatorP.add(orderedP, BorderLayout.SOUTH);
	  /*
		  JPanel setOperatorP = new JPanel(new BorderLayout());
		  setOperatorL = new JList(SET_OPERATORS);
		  setOperatorL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		  setOperatorL.setEnabled(false);
		  setOperatorL.clearSelection();
        JScrollPane setOperatorSP = new JScrollPane(setOperatorL);
		  setOperatorP.add(new JLabel("Set:"), BorderLayout.NORTH);
		  setOperatorP.add(setOperatorSP, BorderLayout.CENTER);
		  JPanel bitOperatorP = new JPanel(new BorderLayout());
		  bitOperatorL = new JList(BIT_OPERATORS);
		  bitOperatorL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		  bitOperatorL.setEnabled(false);
		  bitOperatorL.clearSelection();
        JScrollPane bitOperatorSP = new JScrollPane(bitOperatorL);
		  bitOperatorP.add(new JLabel("Bitwise:"), BorderLayout.NORTH);
		  bitOperatorP.add(bitOperatorSP, BorderLayout.CENTER);
		  operatorP.add(setOperatorP);
		  operatorP.add(bitOperatorP);
	  */
		  optionsP.add(operatorP);

		  // create panel to stack unitOperatorP and trackP
		  JPanel middleP = new JPanel(new BorderLayout());

		  // create Unitary Operator panel with titled, etched, padded border
		  JPanel unitOperatorP = new JPanel(new BorderLayout());
		  unitOperatorP.setBorder(BorderFactory.createTitledBorder(etchedPadBorder0, "Unitary Operators"));
		  JPanel unitOperatorP0 = new JPanel(new BorderLayout());
		  negateChB = new JCheckBox();
		  negateChB.setSelected(false);
		  unitOperatorP0.add(negateChB, BorderLayout.WEST);
		  unitOperatorP0.add(new JLabel(" Negate"), BorderLayout.CENTER);
		  unitOperatorP.add(unitOperatorP0, BorderLayout.NORTH);
		  middleP.add(unitOperatorP, BorderLayout.NORTH);

		  // create Track panel with titled, etched, padded border
		  JPanel trackP = new JPanel(new BorderLayout());
		  trackP.setBorder(BorderFactory.createTitledBorder(etchedPadBorder0, "Track"));
		  JPanel trackP0 = new JPanel(new BorderLayout());
		  trackL = new JList(ObjectHandles.getTrackList());
		  trackL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		  trackL.addListSelectionListener(new ListSelectionListener() {
					 public void valueChanged(ListSelectionEvent e) {
						  if (e.getValueIsAdjusting()) return;
						  if (trackL.getSelectedIndex() == -1) { // nothing selected
								newB.setEnabled(false);
						  } else {
								newB.setEnabled(true);
						  }
					 }
				});
        JScrollPane trackSP = new JScrollPane(trackL);
		  trackSP.setBorder(emptyBorder5);
		  trackP0.add(trackSP, BorderLayout.CENTER);
		  trackP.add(trackP0, BorderLayout.NORTH);
		  middleP.add(trackP, BorderLayout.CENTER);
		  optionsP.add(middleP);

		  // create Qualifier panel with titled, etched, padded border
		  JPanel qualifiersP = new JPanel(new GridLayout(8,1,5,5));
		  qualifiersP.setBorder(BorderFactory.createTitledBorder(etchedPadBorder0, "Qualifiers"));
		  JPanel sequenceP = new JPanel(new BorderLayout());
		  sequenceChB = new JCheckBox();
		  sequenceChB.setSelected(false);
		  sequenceChB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  sequenceCB.setEnabled(sequenceChB.isSelected());
					 }
				});
		  JPanel sequenceP0 = new JPanel(new BorderLayout());
		  sequenceP0.add(sequenceChB, BorderLayout.WEST);
		  sequenceP0.add(new JLabel(" Sequence:"), BorderLayout.CENTER);
		  sequenceP.add(sequenceP0, BorderLayout.WEST);
		  sequenceCB = new JComboBox(ObjectHandles.getSequenceList());
		  sequenceCB.setEditable(false);
		  sequenceCB.setEnabled(false);
		  sequenceP.add(sequenceCB, BorderLayout.CENTER);
		  qualifiersP.add(sequenceP);
		  //		  qualifiersP.add(sequenceCB);
		  qualifiersP.add(new JLabel(""));

		  JPanel widthP = new JPanel(new GridLayout(1,0));
		  PlainDocument minWidthPD = new PlainDocument();
		  minWidthPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minWidthTF.getText().trim());
								maxWidthTF.setEnabled(minWidthTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Width lower bound must be an integer.");
								maxWidthTF.setEnabled(false);
						  }
					 }
					 public void removeUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minWidthTF.getText().trim());
								maxWidthTF.setEnabled(minWidthTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								// don't need to again show error message,
								// just disable maxWidthTF
								maxWidthTF.setEnabled(false);
						  }
					 }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  minWidthTF = new JTextField(minWidthPD, "", 0);
		  widthP.add(new JLabel("Width (bp):"));
		  widthP.add(minWidthTF);
		  widthP.add(new JLabel("to", JLabel.CENTER));
		  PlainDocument maxWidthPD = new PlainDocument();
		  maxWidthPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(maxWidthTF.getText().trim());
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Width upper bound must be an integer.");
						  }
					 }
					 public void removeUpdate(DocumentEvent e) { ; }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  maxWidthTF = new JTextField(maxWidthPD, "", 0);
		  maxWidthTF.setEnabled(false);
		  widthP.add(maxWidthTF);
		  //		  qualifiersP.add(new JLabel("Width (bp):"));
		  qualifiersP.add(widthP);
		  qualifiersP.add(new JLabel(""));

		  JPanel seqPosP = new JPanel(new GridLayout(1,0));
		  PlainDocument minSeqPosPD = new PlainDocument();
		  minSeqPosPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minSeqPosTF.getText().trim());
								maxSeqPosTF.setEnabled(minSeqPosTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Location lower bound must be an integer.");
								maxSeqPosTF.setEnabled(false);
						  }
					 }
					 public void removeUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minSeqPosTF.getText().trim());
								maxSeqPosTF.setEnabled(minSeqPosTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								// don't need to again show error message,
								// just disable maxSeqPosTF
								maxSeqPosTF.setEnabled(false);
						  }
					 }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  minSeqPosTF = new JTextField(minSeqPosPD, "", 0);
		  seqPosP.add(new JLabel("Location (bp):"));
		  seqPosP.add(minSeqPosTF);
		  seqPosP.add(new JLabel("and", JLabel.CENTER));
		  PlainDocument maxSeqPosPD = new PlainDocument();
		  maxSeqPosPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(maxSeqPosTF.getText().trim());
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Location upper bound must be an integer.");
						  }
					 }
					 public void removeUpdate(DocumentEvent e) { ; }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  maxSeqPosTF = new JTextField(maxSeqPosPD, "", 0);
		  maxSeqPosTF.setEnabled(false);
		  seqPosP.add(maxSeqPosTF);
		  //		  qualifiersP.add(new JLabel("Location (bp):"));
		  qualifiersP.add(seqPosP);
		  qualifiersP.add(new JLabel(""));

		  JPanel repeatP = new JPanel(new GridLayout(1,0));
		  PlainDocument minRepeatPD = new PlainDocument();
		  minRepeatPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minRepeatTF.getText().trim());
								maxRepeatTF.setEnabled(minRepeatTF.getText().trim().length() > 0);
								minWithinTF.setEnabled(minRepeatTF.getText().trim().length() > 0);
								maxWithinTF.setEnabled(minWithinTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Repeat lower bound must be an integer.");
								maxRepeatTF.setEnabled(false);
								minWithinTF.setEnabled(false);
								maxWithinTF.setEnabled(false);
						  }
					 }
					 public void removeUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minRepeatTF.getText().trim());
								maxRepeatTF.setEnabled(minRepeatTF.getText().trim().length() > 0);
								minWithinTF.setEnabled(minRepeatTF.getText().trim().length() > 0);
								maxWithinTF.setEnabled(minWithinTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								// don't need to again show error message,
								// just disable maxRepeatTF
								maxRepeatTF.setEnabled(false);
								minWithinTF.setEnabled(false);
								maxWithinTF.setEnabled(false);
						  }
					 }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  minRepeatTF = new JTextField(minRepeatPD, "", 0);
		  repeatP.add(new JLabel("Repeat:"));
		  repeatP.add(minRepeatTF);
		  repeatP.add(new JLabel("to", JLabel.CENTER));
		  PlainDocument maxRepeatPD = new PlainDocument();
		  maxRepeatPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(maxRepeatTF.getText().trim());
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Repeat upper bound must be an integer.");
						  }
					 }
					 public void removeUpdate(DocumentEvent e) { ; }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  maxRepeatTF = new JTextField(maxRepeatPD, "", 0);
		  maxRepeatTF.setEnabled(false);
		  repeatP.add(maxRepeatTF);
		  //		  qualifiersP.add(new JLabel("Repeat:"));
		  qualifiersP.add(repeatP);

		  JPanel withinP = new JPanel(new GridLayout(1,0));
		  PlainDocument minWithinPD = new PlainDocument();
		  minWithinPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minWithinTF.getText().trim());
								maxWithinTF.setEnabled(minWithinTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Within lower bound must be an integer.");
								maxWithinTF.setEnabled(false);
						  }
					 }
					 public void removeUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(minWithinTF.getText().trim());
								maxWithinTF.setEnabled(minWithinTF.getText().trim().length() > 0);
						  } catch (NumberFormatException exception) {
								// don't need to again show error message,
								// just disable maxWithinTF
								maxWithinTF.setEnabled(false);
						  }
					 }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  minWithinTF = new JTextField(minWithinPD, "", 0);
		  minWithinTF.setEnabled(false);
		  withinP.add(new JLabel("Within:"));
		  withinP.add(minWithinTF);
		  withinP.add(new JLabel("to", JLabel.CENTER));
		  PlainDocument maxWithinPD = new PlainDocument();
		  maxWithinPD.addDocumentListener(new DocumentListener() {
					 public void insertUpdate(DocumentEvent e) {
						  try {
								Integer.parseInt(maxWithinTF.getText().trim());
						  } catch (NumberFormatException exception) {
								GloDBUtils.printError("Within upper bound must be an integer.");
						  }
					 }
					 public void removeUpdate(DocumentEvent e) { ; }
					 public void changedUpdate(DocumentEvent e) { ; }
				});
		  maxWithinTF = new JTextField(maxWithinPD, "", 0);
		  maxWithinTF.setEnabled(false);
		  withinP.add(maxWithinTF);
		  //		  qualifiersP.add(new JLabel("Within:"));
		  qualifiersP.add(withinP);

		  // pad the remaining spaces
		  //		  qualifiersP.add(new JLabel(""));
		  //		  qualifiersP.add(new JLabel(""));
		  optionsP.add(qualifiersP);

		  editP.add(optionsP, BorderLayout.CENTER);
		  editP.add(buttonP, BorderLayout.SOUTH);

		  // *************************************************
		  // create panel with overall control buttons
		  JPanel bottomButtonsP = new JPanel(new GridLayout(1,0,100,5));
		  //		  JToolBar bottomButtonsP = new JToolBar();
		  //		  bottomButtonsP.setFloatable(false);
		  bottomButtonsP.setBorder(emptyBorder5);
		  // add compute button
		  computeB = new JButton("Compute");
		  //		  computeB = new JButton(new ImageIcon("icons/compute.png"));
		  //		  computeB.setToolTipText("Compute");
		  computeB.setEnabled(false);
		  computeB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  if (trackID.length() == 0) {
								setID(getID());
								if (trackID.length() == 0) {
									 GloDBUtils.printError("Query Builder requires a target track.");
									 return;
								}
						  }
						  String cmd = "compute(\"" + queryTF.getText() + "\")";
						  Root.runCommand(cmd, true);
					 }
				});
		  bottomButtonsP.add(computeB);
		  // add copy button
		  copyB = new JButton("Copy");
		  //		  copyB = new JButton(new ImageIcon("icons/copy.png"));
		  //		  copyB.setToolTipText("Copy");
		  copyB.setEnabled(false);
		  copyB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  queryTF.selectAll();
						  queryTF.copy();
					 }
				});
		  bottomButtonsP.add(copyB);
		  // add cancel button
		  JButton closeB = new JButton("Close");
		  //		  JButton closeB = new JButton(new ImageIcon("icons/close.png"));
		  //		  closeB.setToolTipText("Close");
		  closeB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  thisFrame.dispose();
					 }
				});
		  bottomButtonsP.add(closeB);

		  // get the target Track ID, if necessary.  If user doesn't set
		  // id here, then the "compute" button will ask.
		  if (GloDBUtils.isEmpty(id)) {
				id = getID();
				// if user exited ID dialog with the cancel button then
				// don't launch QueryBuilder
				if (id == null) {
					 thisFrame.dispose();
					 return;
				}
				else setID(id);
		  } else {
				setID(id);
		  }
		  
		  getContentPane().add(editP, BorderLayout.NORTH);
		  getContentPane().add(queryP, BorderLayout.CENTER);
		  getContentPane().add(bottomButtonsP, BorderLayout.SOUTH);
        pack();

		  // set the default window size
		  setSize(getSize().width, getSize().height + 70);

		  // initialize the edit panel
		  updateEditPanel(null);

		  // display the window
		  //        setVisible(true);
		  show();
    }

	 private void updateEditPanel(QueryElement qElement) {
		  if (qElement == null) {  // reset values
				// if empyt list then clear operatorL selection, else
				// default to first item in list
				if (tableModel.getRowCount() == 0) { 
					 operatorL.clearSelection();
				} else {
					 operatorL.setSelectedIndex(0);
				}
				minPosTF.setText("");
				maxPosTF.setText("");
				negateChB.setSelected(false);
				trackL.setSelectedIndex(0);
				sequenceChB.setSelected(false);
				sequenceCB.setSelectedIndex(0);
				sequenceCB.setEnabled(false);
				minWidthTF.setText("");
				maxWidthTF.setText("");
				minSeqPosTF.setText("");
				maxSeqPosTF.setText("");
				minRepeatTF.setText("");
				maxRepeatTF.setText("");
				minWithinTF.setText("");
				maxWithinTF.setText("");
		  } else {
				/*
				if (table.getSelectedRow() == 0) {
					 // first row selected so unselect operatorL
					 operatorL.clearSelection();
					 minPosTF.setText("");
					 maxPosTF.setText("");
				} else {
				*/
					 //					 operatorL.setSelectedValue(qElement.operator, true);
					 operatorL.setSelectedIndex(qElement.operator);
					 minPosTF.setText(qElement.getMinPos());
					 maxPosTF.setText(qElement.getMaxPos());
					 /*
				}
					 */
				negateChB.setSelected(qElement.negate);
				if (qElement.isGrouped()) {
					 trackL.clearSelection();
				} else {
					 trackL.setSelectedValue(qElement.track, true);
				}					 

				if (qElement.sequence.length() > 0) {
					 sequenceChB.setSelected(true);
					 sequenceCB.setSelectedItem(qElement.sequence);
					 sequenceCB.setEnabled(true);
				} else {
					 sequenceChB.setSelected(false);
					 sequenceCB.setSelectedIndex(0);
					 sequenceCB.setEnabled(false);
				}
				minWidthTF.setText(qElement.getMinLength());
				maxWidthTF.setText(qElement.getMaxLength());
				minSeqPosTF.setText(qElement.getMinSeqPos());
				maxSeqPosTF.setText(qElement.getMaxSeqPos());
				minRepeatTF.setText(qElement.getMinRepeat());
				maxRepeatTF.setText(qElement.getMaxRepeat());
				minWithinTF.setText(qElement.getMinWithin());
				maxWithinTF.setText(qElement.getMaxWithin());
		  }
	 }

	 private void updateQueryElement(QueryElement qElement) {
		  // don't allow operator if first item
		  if (tableModel.getRowCount() == 0) { 
				//				qElement.operator = "";
				qElement.operator = -1;
				qElement.setMinPos("");
				qElement.setMaxPos("");
		  } else {
				//				qElement.operator = (String) operatorL.getSelectedValue();
				qElement.operator = operatorL.getSelectedIndex();

				// only add min/maxPos if order operator ('POS'/'.')
				if (QueryElement.isOrderOperator(qElement.operator)) {
					 qElement.setMinPos(minPosTF.getText().trim());
					 if (minPosTF.getText().trim().length() > 0) {
						  qElement.setMaxPos(maxPosTF.getText().trim());
					 } else {
						  qElement.setMaxPos("");
					 }
				} else {  // not order operator, so clear min/maxPos
					 qElement.setMinPos("");
					 qElement.setMaxPos("");
				}
		  }
		  qElement.negate = negateChB.isSelected();
		  if (sequenceChB.isSelected()) {
				qElement.sequence = (String) sequenceCB.getSelectedItem();
		  } else {
				qElement.sequence = "";
		  }
		  qElement.setMinLength(minWidthTF.getText().trim());
		  if (minWidthTF.getText().trim().length() > 0) {
				qElement.setMaxLength(maxWidthTF.getText());
		  } else {
				qElement.setMaxLength("");
		  }
		  qElement.setMinSeqPos(minSeqPosTF.getText().trim());
		  if (minSeqPosTF.getText().trim().length() > 0) {
				qElement.setMaxSeqPos(maxSeqPosTF.getText().trim());
		  } else {
				qElement.setMaxSeqPos("");
		  }
		  qElement.setMinRepeat(minRepeatTF.getText().trim());
		  if (minRepeatTF.getText().trim().length() > 0) {
				qElement.setMaxRepeat(maxRepeatTF.getText().trim());
		  } else {
				qElement.setMaxRepeat("");
		  }
		  qElement.setMinWithin(minWithinTF.getText().trim());
		  if (minWithinTF.getText().trim().length() > 0) {
				qElement.setMaxWithin(maxWithinTF.getText().trim());
		  } else {
				qElement.setMaxWithin("");
		  }
	 }

	 /** 
	  * Updates the query text field based on the current list
	  * items. 
	  */
	 private void updateQuery() {
		  String text;

		  if (trackID.length() == 0) {
				text = "<invalid track> = ";
		  } else {
				text = trackID + " = ";
		  }

		  if (tableModel.getRowCount() == 0) { 
				// no items so stop here
				queryTF.setText(text);
				return;
		  }

		  // there is at least 1 item, so make sure the first item
		  // doesn't have an operator
		  QueryElement qElement = tableModel.getRow(0);
		  //		  qElement.operator = "";
		  qElement.operator = -1;
		  text += qElement.toString();

		  for (int i = 1; i < tableModel.getRowCount(); i++) {
				text += tableModel.getRow(i).toString();
		  }
		  queryTF.setText(text);
	 }

	 private void setID(String id) {
		  if (id == null) id = "";
		  trackID = id;
		  updateQuery();
	 }

	 private String getID() {
		  String[] labels = {" Output Track ID:"};
		  // use this to get the return value from FieldEditDialog
		  ArrayList textFields = new ArrayList();
		  textFields.add(new JTextField(20));
		  new FieldEditDialog("Output Track ID", labels, textFields);
		  
		  // check "exit code" for FieldEditDialog, if false then exit
		  Boolean exitCode = (Boolean) textFields.get(textFields.size()-1);
		  if (! exitCode.booleanValue()) return null;

		  JTextField tf = (JTextField) textFields.get(0);
		  return tf.getText();
	 }

	 private class ListTableModel extends AbstractTableModel {
		  /** Stores the QueryElements that are in the table.	*/
		  private ArrayList items = new ArrayList();

        private String[] columnNames = {"Operator", "Negate", "Track/Group", "Qualifiers"};

		  //////////////////////////////////////////
		  // Methods added to handle items ArrayList

		  /** 
			* Append a new row to the end of the table.
			*/
		  public void addRow(QueryElement qElement) { addRowAt(qElement, -1); }


		  /** 
			* Add new row to table.  If row index is '-1' then add to end
			* of table.
			*/
		  public void addRowAt(QueryElement qElement, int row) { 
				if (row == -1) items.add(qElement);
				else items.add(row, qElement);

				// there is now at least 1 item
				deleteAllB.setEnabled(true);
				computeB.setEnabled(true);
				copyB.setEnabled(true);
				operatorL.setEnabled(true);

				fireTableDataChanged();
		  }

		  /** 
			* Returns the QueryElement that represents the specified row
			* in the table.
			*/
		  public QueryElement getRow(int row) { 
				if (items.isEmpty()) return null;
				else return (QueryElement) items.get(row); 
		  }
		  
		  /** 
			* Removes the QueryElement that represents the specified row
			* in the table.
			*/
		  public void removeRow(int row) { removeRows(row, row); }

		  /** 
			* Removes the QueryElements that represent the rows
			* specified.
			*/
		  public void removeRows(int[] rows) {
				removeRows(rows[0], rows[rows.length-1]);
		  }

		  /** 
			* Removes the QueryElements that represent the specified row
			* in the table.  If the row is -1 then will clear the entire
			* table.
			*/
		  public void removeRows(int minRow, int maxRow) {
				if (items.isEmpty()) {
					 // XXX this should throw an exception, as this
					 // condition should never occur
					 GloDBUtils.printWarning("No items in table");
					 return;
				}

				if (minRow == maxRow) {
					 if (minRow == -1) items.clear();  // remove all rows
					 else items.remove(minRow); // remove one row
				} else {
					 // can't use removeRange() because protected method
					 /*
					 if (minRow > maxRow) {  // make sure minRow < maxRow
						  int tmp = maxRow;
						  maxRow = minRow;
						  minRow = tmp;
					 }
					 */
					 while (maxRow >= minRow) {
						  items.remove(maxRow--);
					 }
				}


				// test if list is now empty
				if (items.isEmpty()) {
					 deleteAllB.setEnabled(false);
					 computeB.setEnabled(false);
					 copyB.setEnabled(false);
					 // don't allow operator if first item
					 operatorL.setEnabled(false);
					 operatorL.clearSelection();
				}

				fireTableDataChanged();
		  }

		  /** Removes all rows from the table. */
		  public void removeAllRows() { removeRow(-1); }

		  
		  //////////////////////////////////////////
		  // Standard AbstractTableModel methods

        public int getColumnCount() { return columnNames.length; }

        public int getRowCount() { 
				if (items.isEmpty()) return 0;
				else return items.size(); 
		  }

        public String getColumnName(int col) { return columnNames[col]; }

		  /** Operator, Negate, Track, Qualifiers	*/
        public Object getValueAt(int row, int col) {
				QueryElement qElement = getRow(row);
				switch (col) {
				case 0: return " " + qElement.getOperatorVal();
				case 1: return ((qElement.negate) ? "    !" : "");
				case 2: 
					 if (qElement.isGrouped()) {
						  String out = " (";
						  for (Iterator i = qElement.groupIterator(); i.hasNext();) {
								out += ((QueryElement) i.next()).toString();
						  }
						  return out + ")";
					 } else {
						  return " " + qElement.track;
					 }
				default: return qElement.toStringQualifiers();
				}
        }
    }

 } // QueryBuilder.java
