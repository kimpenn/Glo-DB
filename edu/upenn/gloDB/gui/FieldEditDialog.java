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
 * @(#)FieldEditDialog.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * This class is used to present a modal dialog for editing text in
 * the 'text' HashMap.  The HashMap should contain label:value pairs.
 *
 * @author  Stephen Fisher
 * @version $Id: FieldEditDialog.java,v 1.1.2.7 2007/03/01 21:17:33 fisher Exp $
 */
public class FieldEditDialog extends JDialog {
	 FieldEditDialog thisDialog;
	 ArrayList save = new ArrayList();
	 ArrayList textFields;

	 JButton okB;
	 JButton cancelB;

	 public FieldEditDialog(String title, String[] labels, ArrayList orig) {
		  super((Frame) null, title, true);

		  // keep pointer to self so can 'dispose' Dialog below
		  thisDialog = this;

		  this.textFields = orig;

		  setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		  addWindowListener(new WindowAdapter() {
					 public void windowClosing(WindowEvent e) { 
						  // revert to original values
						  for (int i = 0; i < textFields.size(); i++) {
								JTextField textField = (JTextField) textFields.get(i);
								textField.setText((String) save.get(i));
						  }
						  textFields.add(new Boolean(false));
						  thisDialog.dispose();
					 }
				});


		  // entry panel
		  JPanel labelP = new JPanel(new GridLayout(0,1,5,5));
		  labelP.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		  JPanel editP = new JPanel(new GridLayout(0,1,5,5));
		  
		  if (labels.length != textFields.size()) {
				GloDBUtils.printError("Labels and textFields must be same size for FieldEditDialog.");
				return;
		  }

		  for (int i = 0; i < labels.length; i++) {
				labelP.add(new JLabel(labels[i]));
				JTextField textField = (JTextField) textFields.get(i);
				editP.add(textField);
				save.add(textField.getText());
		  }

		  JPanel contentP = new JPanel(new BorderLayout());
		  contentP.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
		  contentP.add(labelP, BorderLayout.WEST);
		  contentP.add(editP, BorderLayout.CENTER);

		  // select button sub-panel
		  JPanel buttonP = new JPanel(new GridLayout(1,0,5,5));
		  buttonP.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		  // select objects "ok" Button
		  okB = new JButton("Ok");
		  okB.setEnabled(true);
		  okB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  textFields.add(new Boolean(true));
						  thisDialog.dispose();
					 }
				});
		  buttonP.add(okB);
		  // select objects "cancel" Button
		  cancelB = new JButton("Cancel");
		  cancelB.setEnabled(true);
		  cancelB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  // revert to original values
						  for (int i = 0; i < textFields.size(); i++) {
								JTextField textField = (JTextField) textFields.get(i);
								textField.setText((String) save.get(i));
						  }

						  textFields.add(new Boolean(false));
						  thisDialog.dispose();
					 }
				});
		  buttonP.add(cancelB);
		  
		  getContentPane().setLayout(new BorderLayout());
		  getContentPane().add(contentP, BorderLayout.NORTH);
		  getContentPane().add(buttonP, BorderLayout.SOUTH);
		  pack();
		  
		  // set the default window size
		  setSize(getSize().width + 100, getSize().height + 30);
		  
		  // display the window
		  show();
	 }
} // FieldEditDialog.java
