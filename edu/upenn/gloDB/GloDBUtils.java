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
 * @(#)GloDBUtils.java
 */

package edu.upenn.gloDB;

import java.awt.Toolkit;
import javax.swing.JTextArea;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.io.ByteArrayOutputStream;

/**
 * GloDBUtils.  Miscellaneous functions.
 *
 * @author  Stephen Fisher
 * @version $Id: GloDBUtils.java,v 1.1.2.9 2007/03/01 21:17:32 fisher Exp $
 */

public class GloDBUtils {

	 public final static int TRACK = 1;
	 public final static int SEQUENCE = 2;
	 public final static int FEATURE = 3;
	 
	 public final static int WARNING = 1;
	 public final static int ERROR = 2;
	 public final static int FEEDBACK = 3;

	 /** 
	  * Amount of detailed feedback: 2 = lots, 1 = no warnings, 0 = no
	  * feedback.
	  */
	 private static int VERBOSE = GloDBMain.userDefaults.getInt("VERBOSE", 1);

	 /**
	  * This flag is used in FeatureUtils.compareFeatures() to compare
	  * two Features.  If 'true', then only the Feature source and
	  * start/stop positions will be used.  If 'false', then the
	  * hashCode of the attributes HashMap will also be used.  This
	  * allows for Features identically placed but with different
	  * attributes to coexist in a Track.  Thus if two Features exist
	  * at the exact same location on a sequence, this flag will
	  * determine whether the attributes field will be used when
	  * deciding if these are in fact the same Feature.
	  */
	 private static boolean IGNORE_ATTRIBUTES = GloDBMain.userDefaults.getBoolean("IGNORE_ATTRIBUTES", 
																										 false);

	 /**
	  * If not null, all error and warning messages will be sent here.
	  * This messages panel can be disabled by setting the value to null.
	  * When disabled, all messages printed will be sent to stderr,
	  * which will effectively display them in the console.
	  */
	 public static JTextArea guiMessages = null;

    //--------------------------------------------------------------------------
    // Getters and Setters

	 /** Set the VERBOSE flag. */
    public static void setVerbose(int verbose) { 
		  VERBOSE = verbose; 
		  GloDBMain.userDefaults.putInt("VERBOSE", verbose);
	 }

	 /** Get the VERBOSE flag. */
    public static int getVerbose() { return VERBOSE; }

	 /** Set the IGNORE_ATTRIBUTES flag. */
    public static void setIgnoreAttributes(boolean ignoreAttributes) { 
		  IGNORE_ATTRIBUTES = ignoreAttributes; 
		  GloDBMain.userDefaults.putBoolean("IGNORE_ATTRIBUTES", ignoreAttributes);
		  String msg = "Changing IGNORE_ATTIBUTES will not affect the features already loaded \n";
		  msg += "into tracks but will affect any new features added.";
		  printMsg(msg, WARNING);
	 }

	 /** Get the IGNORE_ATTRIBUTES flag. */
    public static boolean ignoreAttributes() { return IGNORE_ATTRIBUTES; }

    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /** Returns true if 'str' is empty (ignores spaces) or null. */
	 public static boolean isEmpty(String str) {
		  if ((str == null) || (str.trim().length() == 0)) return true;
		  else return false;
	 }

	 /** RE-set GloDB user defaults.  */
    public static void resetGloDBDefaults() { 
		  setVerbose(1);
		  setIgnoreAttributes(false);
	 }

	 /**
	  * Convert from the integer constant value to a string equivalent.
	  */
	 public static String convertConstant(int val) {
		  switch (val) {
		  case TRACK: return "Track";
		  case SEQUENCE: return "Sequence";
		  case FEATURE: return "Feature";
		  }
		  return "";
	 }

    /** Get the class name without any package info. */
	 public static String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

	 public static byte[] compressString(String val) {
		  byte[] bVal = val.getBytes();
    
		  // Create the compressor with highest level of compression
		  Deflater compressor = new Deflater();
		  compressor.setLevel(Deflater.BEST_SPEED);
		  
		  // Give the compressor the data to compress
		  compressor.setInput(bVal);
		  compressor.finish();
		  
		  // Create an expandable byte array to hold the compressed data.
		  // You cannot use an array that's the same size as the orginal because
		  // there is no guarantee that the compressed data will be smaller than
		  // the uncompressed data.
		  ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length());
		  
		  // Compress the data
		  byte[] buf = new byte[2048];
		  while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
		  }
		  try {
				bos.close();
		  } catch (Exception e) { System.err.println(e); }
		  
		  return bos.toByteArray();
	 }

	 public static String uncompressString(byte[] compressedData) {
		  // Create the decompressor and give it the data to compress
		  Inflater decompressor = new Inflater();
		  decompressor.setInput(compressedData);
		  
		  // Create an expandable byte array to hold the decompressed data
		  ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
		  
		  // Decompress the data
		  byte[] buf = new byte[1024];
		  while (!decompressor.finished()) {
				try {
					 int count = decompressor.inflate(buf);
					 bos.write(buf, 0, count);
				} catch (Exception e) { System.err.println(e); }
		  }
		  try {
				bos.close();
		  } catch (Exception e) { System.err.println(e); }
		  
		  return bos.toString();
	 }

	 /** Display the message to either the gui or stderr. */
	 public static void printMsg(String msg) { printMsg(msg, 0, true); }

	 /** Display the message to either the gui or stderr. */
	 public static void printMsg(String msg, int type) { printMsg(msg, type, true); }

	 /** Display the message to either the gui or stderr. */
	 public static void printMsg(String msg, boolean newline) { printMsg(msg, 0, newline); }

	 /** Display the error message to either the gui or stderr. */
	 public static void printError(String msg) { printMsg(msg, ERROR, true); }

	 /** Display the warning message to either the gui or stderr. */
	 public static void printWarning(String msg) { printMsg(msg, WARNING, true); }

	 /**
	  * Display the message to either the gui or stderr.  The message
	  * type can be set which will add the appropriate label to the
	  * message (1 = warning, 2 = error).  
	  */
	 public static void printMsg(String msg, int type, boolean newline) {
		  // if VERBOSE = 0, then don't print any messages
		  if (VERBOSE == 0) return;

		  // add appropriate message label
		  switch (type) {
		  case FEEDBACK: 
				if (VERBOSE < 2) return;
				break;
		  case WARNING: 
				if (VERBOSE < 2) return;
				msg = " * WARNING: " + msg; 
				break;
		  case ERROR: 
				if (VERBOSE < 1) return;
				msg = " ** ERROR: " + msg; 
				Toolkit.getDefaultToolkit().beep();
				break;
		  //		  default: msg = " " + msg;
		  }

		  if (newline) msg += "\n";

		  if (guiMessages == null) System.err.print(msg);
		  else guiMessages.append(msg);
	 }

} // GloDBUtils.java
