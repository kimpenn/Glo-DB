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
 * @(#)ViewParserDefs.java
 */

package edu.upenn.gloDB.gui;

import java.awt.*;
//import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * View parser definitions.
 *
 * @author  Stephen Fisher
 * @version $Id: ViewParserDefs.java,v 1.4.2.9 2007/03/01 21:17:33 fisher Exp $
 */

public class ViewParserDefs extends JFrame {
	 ViewParserDefs thisFrame;
	 
	 public ViewParserDefs() {
		  super("GLODB: Parser Definitions");
		  
		  // keep pointer to self so can 'dispose' Frame below
		  thisFrame = this;
		  
		  setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		  
		  JTextArea text = new JTextArea(20, 110);
		  text.setEditable(false);
		  JScrollPane textSP = new JScrollPane(text);
		  textSP.setBorder(BorderFactory.createLoweredBevelBorder());

		  // this should probably be in an HTML file that is displayed
		  text.append(" Sequence:\n");
		  text.append("      |------------------------------|\n");
		  text.append("      |012345678901234567890123456789|\n");
		  text.append("   t1:  r-s    t-------u v---w  x-y\n");
		  text.append("   t2:  a-b   c-d e-f g---h i-j\n");
		  text.append("\n Tracks:\n");
		  text.append("   t1: {(r,s), (t,u), (v,w), (x,y)}\n");
		  text.append("   t2: {(a,b), (c,d), (e,f), (g,h), (i,j)}\n");
		  text.append("\n These treat Tracks as containing sets of intervals:\n");
        text.append("   t1 AND t2:        {(r,s), (t,u), (v,w), (c,d), (e,f), (g,h), (i,j)}\n");
        text.append("   t1 OR t2:         {(r,s), (t,u), (v,w), (x,y), (c,d), (e,f), (g,h), (i,j)}\n");
        text.append("   t1 MINUS t2:      {(x,y)}\n");
        text.append("   t2 MINUS t1:      {}\n");
        text.append("   t1 sAND t2:       {(r,s)}\n");
        text.append("   t1 sMINUS t2:     {(t,u), (v,w), (x,y)}\n");
        text.append("   t2 sMINUS t1:     {(c,d), (e,f), (g,h), (i,j)}\n");
        text.append("   t1 sMINUS t2 AND t2:   {(t,u), (v,w), (c,d), (e,f), (g,h), (i,j)}\n");
        text.append("   t1 sMINUS (t2 AND t2): {(t,u), (v,w), (x,y)}\n");
		  text.append("   (t1 MINUS t2) OR (t2 MINUS t1): {(x,y)}\n");
		  text.append("   (t1 sMINUS t2) OR (t2 sMINUS t1): {(t,u), (v,w), (x,y), (c,d), (e,f), (g,h), (i,j)}\n");
        text.append("   t1 POS{0} t2:     {}\n");
        text.append("   t1 POS{5} t2:     {(t,u), (i,j)}\n");
        text.append("   t2 POS{5} t1:     {(a,b), (t,u), (e,f), (v,w)}\n");
        text.append("   t1 POS{-5} t2:    {(t,u), (e,f)}\n");
        text.append("   t1 POS{3,6} t2:   {(r,s), (c,d), (t,u), (i,j)}\n");
        text.append("   t1 POS{-5,-1} t2: {(r,s), (t,u), (e,f), (g,h), (v,w), (i,j)}\n");
        text.append("\n These treat Tracks as masks over the Sequence string:\n");
        text.append("   t1 && t2:         {(1,3), (8,9), (11,13), (15,16), (18,19), (21,22)}\n");
        text.append("   t1 || t2:         {(1,3), (7,23), (25,27)}\n");
        text.append("   t1 - t2:          {(10,10), (14,14), (20,20), (25,27)}\n");
        text.append("   t2 - t1:          {(7,7), (17,17), (23,23)}\n");
        text.append("   ! t1:             {(0,0), (4,7), (17,17), (23,24), (28,29)}\n");
        text.append("   ! t2:             {(0,0), (4,6), (10,10), (14,14), (20,20), (24,29)}\n");
        text.append("   ! t1 && t2:       {(7,7), (17,17), (23,23)}\n");
        text.append("   t1 && ! t2:       {(10,10), (14,14), (20,20), (25,27)}\n");
        text.append("   ! (t1 && t2):     {(0,0), (4,7), (10,10), (14,14), (17,17), (20,20), (23,29)}\n");
        text.append("   ! (t1 || t2):     {(0,0), (4,6), (24,24), (28,29)}\n");
        text.append("   ! (t1 - t2 && t1):   {(0,9), (11,13), (15,19), (21,24), (28,29)}\n");
        text.append("   ! (t1 - (t2 && t1)): {(0,9), (11,13), (15,19), (21,24), (28,29)}\n");
		  text.append("   (t1 - t2) || (t2 - t1): {(7,7), (10,10), (14,14), (17,17), (20,20), (23,23), (25,27)}\n");
		  /*
        text.append("   t1 .{0} t2:       {(1,3), (3,3), (8,16), (16,16), (18,22), (22,22)}\n");
        text.append("   t1 .{5} t2:       {(1,3), (8,8), (8,16), (21,21)}\n");
        text.append("   t2 .{5} t1:       {(1,3), (8,8), (7,9), (14,14), (11,13), (18,18)}\n");
        text.append("   t1 .{-5} t2:      {(8,16), (11,11), (18,22), (17, 17), (25,27), (22,22)}\n");
        text.append("   t1 .{3,6} t2:     {(1,3), (7,9), (8,16), (19,19), (21,22)}\n");
        text.append("   t1 .{-5,-1} t2:   {(1,3), (1,2), (8,16), (11,13), (15,15), (18,22), (17,19), (25,27), (21,23)}\n");
		  */

        text.append("\n\n\nTOKEN SYNTAX:\n");
		  text.append("   EXPR      := <TRACK> (<opEXPR>)*\n");
		  text.append("   opEXPR    := (<OPERATOR> | <POS> (<pREPEAT> | <oREPEAT>)?) <TRACK>\n");
		  text.append("   TRACK     := (<IGNORE>)? (<bNOT>)? ( <GROUP> | <FEATURE>) (<QUALIFIER>)*\n");
		  text.append("   GROUP     := \"(\" <EXPR> \")\"\n");
		  text.append("   QUALIFIER := (<dREPEAT> | <oREPEAT> | <LENGTH> | <SEQUENCE>)\n");
		  text.append("   OPERATOR  := <AND> | <OR> | <MINUS> | <sAND> | <sMINUS> | <bAND> | <bOR> | <bMINUS>\n");
		  //		  text.append("   POSITION  := <POS> | <bPOS>\n");
		  text.append("   FEATURE   := <allTRACKS> | <TRACKREF>\n");
		  text.append("   oREPEAT   := <HOOK> | <PLUS> | <STAR>\n");
		  text.append("   pREPEAT   := <lREPEAT> <nINTEGER> (\",\" <nINTEGER>)? <rREPEAT>\n");
		  text.append("   dREPEAT   := <lREPEAT> <INTEGER> (\",\" <INTEGER>)? (\";\" <INTEGER> (\",\" <INTEGER>)?)? <rREPEAT>\n");
		  text.append("   LENGTH    := <lLENGTH2> <INTEGER> (\",\" <INTEGER>)? <rLENGTH> \n");
		  text.append("   | <lLENGTH2> <INTEGER> (\",\" <INTEGER>)? (\";\" <INTEGER> (\",\" <INTEGER>)?)? <rLENGTH> \n");
		  text.append("   | <lLENGTH2> <INTEGER> (\",\" <INTEGER>)? (\"from\" <INTEGER> (\",\" <INTEGER>)?)? <rLENGTH> \n");
		  text.append("   | \"from\" <INTEGER> (\",\" <INTEGER>)? \n");

		  text.append("\n\n\nTOKENS:\n");
		  text.append("     #SP0:      ( \" \" )*     // used to construct tokens below (0 or more spaces)\n");
		  text.append("     #SP1:      ( \" \" )+     // used to construct tokens below (at least 1 space)\n");
		  text.append("     EQUALS:    \"=\" \n");
		  text.append("     SEPARATOR: \",\" \n");
		  text.append("     INTEGER:   ( [\"0\"-\"9\"] )+ \n");
		  text.append("     nINTEGER:  \"-\" <INTEGER> \n");
		  text.append("     lLENGTH:   \"<\" \n");
		  text.append("     lLENGTH2:  <lLENGTH> <SP0> \";\" \n");
		  text.append("     rLENGTH:   \">\" \n");
		  text.append("     LENGTH:    \"len\"\n");
		  text.append("             |  \"length of\" \n");
		  text.append("     FROM:      \"from\"\n");
		  text.append("             |  \"from position\" \n");
		  text.append("     lREPEAT:   \"{\" \n");
		  text.append("     rREPEAT:   \"}\" \n");
		  text.append("     lGROUP:    \"(\" \n");
		  text.append("     rGROUP:    \")\" \n");
		  text.append("     lSET:      \"[\" \n");
		  text.append("     rSET:      \"]\" \n");
		  text.append("     IGNORE:    \"~\" \n");
		  text.append("     POS:       \"POS\" \n");
		  //		  text.append("     bPOS:      \".\" \n");
		  text.append("     AND:       \"AND\" \n");
		  text.append("     sAND:      \"sAND\" \n");
		  text.append("     OR:        \"OR\" \n");
		  text.append("     MINUS:     \"MINUS\"\n");
		  text.append("     sMINUS:    \"sMINUS\"\n");
		  text.append("     bAND:      \"&&\" \n");
		  text.append("     bOR:       \"||\" \n");
		  text.append("     bMINUS:    \"-\" \n");
		  text.append("     bNOT:      \"!\" \n");
		  text.append("     HOOK:      \"?\" \n");
		  text.append("     PLUS:      \"+\" \n");
		  text.append("     STAR:      \"*\" \n");
		  text.append("     EOL:       \"\\n\" \n");
		  text.append("     WITHIN:    \"within\" \n");
		  text.append("     TIMES:     \"times\" \n");
		  text.append("     REPEATED:  \"repeated\" \n");
		  text.append("     POSITIONS: \"positions\" \n");
		  text.append("     allTRACKS: \"__T\" \n");
		  text.append("             |  \"a feature in any track\" \n");
		  text.append("     OBJECT:    [\"a\"-\"z\",\"A\"-\"Z\",\"_\"] ( [\"a\"-\"z\",\"A\"-\"Z\",\"_\",\"0\"-\"9\"] )*  // a track or sequence name\n");
		  text.append("     ASSIGN:    <OBJECT> <SPO> <EQUALS> \n");
		  text.append("     TRACK:     \"a feature in track\" <SP1> \n");
		  text.append("     SEQUENCE:  \"S:\" <SP0> \n");
		  text.append("             |  \"seq\" <SP1> \n");
		  text.append("             |  \"on sequence\" <SP1> \n");

		  // setup image area to display data
		  //		  ImageCanvas canvas = new ImageCanvas("C:\\Documents and Settings\\Gromit\\Desktop\\gloDB\\src\\documentation\\parser.gif");
		  
		  // setup close button
		  JButton closeB = new JButton("Close Definitions Viewer");
		  closeB.addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						  thisFrame.dispose();
					 }
				});
		  
		  getContentPane().setLayout(new BorderLayout());
		  getContentPane().add(textSP, BorderLayout.CENTER);
		  getContentPane().add(closeB, BorderLayout.SOUTH);
		  pack();
		  
		  // set the default window size
		  setSize(800, 900);
		  
		  // display the window
		  //		  setVisible(true);
		  show();
	 }

	 /*
	 private class ImageCanvas extends Canvas {
		  Image image;

		  public ImageCanvas(String name) {
				MediaTracker media = new MediaTracker(this);
				image = Toolkit.getDefaultToolkit().getImage(name);
				media.addImage(image, 0);
				try { media.waitForID(0); }
				catch (Exception e) {}
		  }

		  public ImageCanvas(ImageProducer imageProducer) {
				image = createImage(imageProducer);
		  }

		  public void paint(Graphics g) {
				g.drawImage(image, 0,0, this);
		  }
	 }
	 */

} // ViewParserDefs.java
