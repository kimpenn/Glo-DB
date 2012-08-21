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
 * @(#)ObjectSelectorDialog.java
 */

package edu.upenn.gloDB.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * This class is used to present a modal dialog for selecting an item
 * from the 'objects' array and includes the selected item in the
 * 'out' arraylist.
 * 
 *
 * @author  Stephen Fisher
 * @version $Id: ObjectSelectorDialog.java,v 1.2.2.6 2007/03/01 21:17:33 fisher Exp $
 */
public class ObjectSelectorDialog extends JDialog {
	 ObjectSelectorDialog thisDialog;
	 ArrayList output;

	 JList objectL;
	 JButton selectB;
	 JButton cancelB;

	 public ObjectSelectorDialog(String title, Object[] objects, ArrayList out) {
		  super((Frame) null, title, true);

		  // keep pointer to self so can 'dispose' Dialog below
		  thisDialog = this;
		  output = out;

		  setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		  JPanel objectP = new JPanel(new BorderLayout());
		  objectL = new JList(objects);
		  objectL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		  JScrollPane objectSP = new JScrollPane(objectL);
		  objectP.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
		  objectP.setPreferredSize(new Dimension(200, 400));
		  objectP.setLayout(new BorderLayout());
		  objectP.add(new Label("Select Item:"), BorderLayout.NORTH);
		  objectP.add(objectSP, BorderLayout.CENTER);

		  // select button sub-panel
		  JPanel buttonP = new JPanel(new GridLayout(1,0,5,5));
		  buttonP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		  // select objects "select" Button
		  selectB = new JButton("Select");
		  selectB.setEnabled(true);
		  selectB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  output.add(objectL.getSelectedValue());
						  thisDialog.dispose();
					 }
				});
		  buttonP.add(selectB);
		  // select objects "cancel" Button
		  cancelB = new JButton("Cancel");
		  cancelB.setEnabled(true);
		  cancelB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  thisDialog.dispose();
					 }
				});
		  buttonP.add(cancelB);
		  
		  getContentPane().setLayout(new BorderLayout());
		  getContentPane().add(objectP, BorderLayout.CENTER);
		  getContentPane().add(buttonP, BorderLayout.SOUTH);
		  pack();
		  
		  // set the default window size
		  setSize(getSize().width + 100, getSize().height + 30);
		  
		  // display the window
		  show();
	 }
} // ObjectSelectorDialog.java
