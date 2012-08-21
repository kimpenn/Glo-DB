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
 * @(#)QueryElement.java
 */

package edu.upenn.gloDB.gui;

import edu.upenn.gloDB.GloDBUtils;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains query info for either a single Track or a group of
 * QueryElements (ie Tracks).
 *
 * @author  Stephen Fisher
 * @version $Id: QueryElement.java,v 1.1.2.19 2007/03/01 21:17:33 fisher Exp $
 */

public class QueryElement implements Cloneable { 
	 //	 public static final String[] OPERATORS = { "AND", "sAND", "&&", "OR", "||", "MINUS", "sMINUS", "-", "POS", "." };
	 public static final String[] OPERATORS = { "AND", "sAND", "&&", "OR", "||", "MINUS", "sMINUS", "-", "POS" };

	 /** Group of QueryElements (ie Tracks). */
	 private ArrayList group = new ArrayList();

	 /** Track ID. */
	 public String track = "";

	 /** 
	  * Index for type of operation to be performed.  The string values
	  * are stored in OPERATORS.
	  */
	 public int operator = -1;

	 /** Negate? */
	 public boolean negate = false;

	 /** Sequence ID. */
	 public String sequence = "";

	 /** Minimum acceptible Feature width. */
	 private int minLength = -1;

	 /** Maximum acceptible Feature width. */
	 private int maxLength = -1;

	 /** Minimum acceptible position within 'sequence'. */
	 private int minSeqPos = -1;

	 /** 
	  * Maximum acceptible position within 'sequence'.  If -1, then
	  * goes to maximum Sequence length.
	  */
	 private int maxSeqPos = -1;

	 /** Minimum ordered position ('POS'). */
	 private int minPos = -1;

	 /** Maximum ordered position ('POS'). */
	 private int maxPos = -1;

	 /** 
	  * Minimum number of repeating features.
	  * @XXX Default = '1'
	  */
	 private int minRepeat = 1;

	 /** 
	  * Maximum number of repeating features.
	  * @XXX Default = '1'
	  */
	 private int maxRepeat = 1;

	 /** 
	  * Minimum number of positions between repeating features. If '0',
	  * then any overlap between features will be valid.
	  * @XXX Default = '0'
	  */
	 private int minWithin = 0;

	 /** 
	  * Maximum number of positions between repeating features.
	  * @XXX Default = '0'
	  */
	 private int maxWithin = 0;

	 /** Create a new QueryElement object. */
	 public QueryElement() {
	 }

	 /** Create a new QueryElement object. */
	 public QueryElement(String track) {
		  this.track = track;
	 }

    //--------------------------------------------------------------------------
    // Setters and Getters
   
	 public ArrayList getGroup() { return group; }

	 /** If null value, then will set group to a new ArrayList(). */
	 public void setGroup(ArrayList group) { 
		  if (group == null) this.group = new ArrayList();
		  else this.group = group;
	 }

	 public void setMinLength(String val) { 
		  if (val.trim().length() == 0) {
				this.minLength = -1;
		  } else {
				try {
					 this.minLength = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for width lower bound, using default (\"-1\").");
					 this.minLength = -1;
				}
		  }
	 }
	 public void setMaxLength(String val) { 
		  if (val.trim().length() == 0) {
				this.maxLength = -1;
		  } else {
				try {
					 this.maxLength = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for width upper bound, using default (\"-1\").");
					 this.maxLength = -1;
				}
		  }
	 }

	 public void setMinSeqPos(String val) { 
		  if (val.trim().length() == 0) {
				this.minSeqPos = -1;
		  } else {
				try {
					 this.minSeqPos = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for location lower bound, using default (\"-1\").");
					 this.minSeqPos = -1;
				}
		  }
	 }
	 public void setMaxSeqPos(String val) { 
		  if (val.trim().length() == 0) {
				this.maxSeqPos = -1;
		  } else {
				try {
					 this.maxSeqPos = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for location upper bound, using default (\"-1\").");
					 this.maxSeqPos = -1;
				}
		  }
	 }

	 public void setMinRepeat(String val) { 
		  if (val.trim().length() == 0) {
				this.minRepeat = 1;
		  } else {
				try {
					 this.minRepeat = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for repeat lower bound, using default (\"1\").");
					 this.minRepeat = 1;
				}
		  }
	 }
	 public void setMaxRepeat(String val) { 
		  if (val.trim().length() == 0) {
				this.maxRepeat = 1;
		  } else {
				try {
					 this.maxRepeat = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for repeat upper bound, using default (\"1\").");
					 this.maxRepeat = 1;
				}
		  }
	 }

	 public void setMinWithin(String val) { 
		  if (val.trim().length() == 0) {
				this.minWithin = 0;
		  } else {
				try {
					 this.minWithin = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for within lower bound, using default (\"0\").");
					 this.minWithin = 0;
				}
		  }
	 }
	 public void setMaxWithin(String val) { 
		  if (val.trim().length() == 0) {
				this.maxWithin = 0;
		  } else {
				try {
					 this.maxWithin = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for within upper bound, using default (\"0\").");
					 this.maxWithin = 0;
				}
		  }
	 }

	 public void setMinPos(String val) { 
		  if (val.trim().length() == 0) {
				this.minPos = -1;
		  } else {
				try {
					 this.minPos = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for width lower bound, using default (\"-1\").");
					 this.minPos = -1;
				}
		  }
	 }
	 public void setMaxPos(String val) { 
		  if (val.trim().length() == 0) {
				this.maxPos = -1;
		  } else {
				try {
					 this.maxPos = Integer.parseInt(val.trim());
				} catch (NumberFormatException e) {
					 GloDBUtils.printError("Illegal value for width upper bound, using default (\"-1\").");
					 this.maxPos = -1;
				}
		  }
	 }

	 /** Most often need these as Strings */
	 public String getMinLength() { 
		  if (minLength == -1) return "";
		  else return Integer.toString(minLength); 
	 }
	 public String getMaxLength() { 
		  if (maxLength == -1) return "";
		  return Integer.toString(maxLength); 
	 }
	 public String getMinSeqPos() { 
		  if (minSeqPos == -1) return "";
		  return Integer.toString(minSeqPos); 
	 }
	 public String getMaxSeqPos() { 
		  if (maxSeqPos == -1) return "";
		  return Integer.toString(maxSeqPos); 
	 }
	 public String getMinRepeat() { 
		  if (minRepeat == 1) return "";
		  return Integer.toString(minRepeat); 
	 }
	 public String getMaxRepeat() { 
		  if (maxRepeat == 1) return "";
		  return Integer.toString(maxRepeat); 
	 }
	 public String getMinWithin() { 
		  if (minWithin == 0) return "";
		  return Integer.toString(minWithin); 
	 }
	 public String getMaxWithin() { 
		  if (maxWithin == 0) return "";
		  return Integer.toString(maxWithin); 
	 }
	 public String getMinPos() { 
		  if (minPos == -1) return "";
		  else return Integer.toString(minPos); 
	 }
	 public String getMaxPos() { 
		  if (maxPos == -1) return "";
		  return Integer.toString(maxPos); 
	 }


    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /** Returns the string equivalent to the operator index value. */
	 public static String getOperatorVal(int index) { 
		  if (index < 0) return "";
		  else return OPERATORS[index]; 
	 }

	 /** Returns the string equivalent to the operator index value. */
	 public static boolean isOrderOperator(int index) { 
		  if (index < 0) return false;

		  String operator = OPERATORS[index];
		  //		  if ((operator.compareToIgnoreCase("POS") == 0) 
		  //				|| (operator.compareToIgnoreCase(".") == 0)) {
		  if (operator.compareToIgnoreCase("POS") == 0) {
				return true;
		  } else {
				return false;
		  }
	 }

	 /** Returns the string equivalent to the operator index value. */
	 public static boolean isOrderOperator(String operator) { 
		  //		  if ((operator.compareToIgnoreCase("POS") == 0) 
		  //				|| (operator.compareToIgnoreCase(".") == 0)) {
		  if (operator.compareToIgnoreCase("POS") == 0) {
				return true;
		  } else {
				return false;
		  }
	 }

	 /** Returns the string equivalent to the operator index value. */
	 public String getOperatorVal() { 
		  if (operator < 0) {
				return "";
		  } else {
				String op = OPERATORS[operator];
				if (isOrderOperator(op)) {
					 if (maxPos == -1) op += "{" + getMinPos() + "}";
					 else op += "{" + getMinPos() + ", " + getMaxPos() + "}";
				}
				return op; 
		  }
	 }

	 /** 'element' is assumed to be a QueryElement. */
	 public void addToGroup(Object element) { 
		  group.add(element);
	 }

	 public boolean isGrouped() { 
		  if (group.size() > 0) return true;
		  else return false; 
	 }

	 public int groupSize() { return group.size(); }

	 public Iterator groupIterator() { return group.iterator(); }

	 /**
	  * Create a shallow clone (just clone the structure, not the
	  * Objects) of the existing object.
	  */
	 public Object clone() {
		  QueryElement queryElement = new QueryElement();
		  queryElement.group = this.group;
		  queryElement.track = this.track;
		  queryElement.operator = this.operator;
		  queryElement.negate = this.negate;
		  queryElement.sequence = this.sequence;
		  queryElement.minLength = this.minLength;
		  queryElement.maxLength = this.maxLength;
		  queryElement.minSeqPos = this.minSeqPos;
		  queryElement.maxSeqPos = this.maxSeqPos;
		  queryElement.minRepeat = this.minRepeat;
		  queryElement.maxRepeat = this.maxRepeat;
		  queryElement.minWithin = this.minWithin;
		  queryElement.maxWithin = this.maxWithin;
		  queryElement.minPos = this.minPos;
		  queryElement.maxPos = this.maxPos;
		  return queryElement;
	 }

	 /**
	  * Creates an array with the fields in the following order:
	  * "operator, negate, group, track, sequence, min/max length,
	  * min/max pos".
	  */
	 public Object[] toArray() {
		  Object[] out = new Object[10];
		  out[0] = getOperatorVal();
		  out[1] = new Boolean(negate);
		  if (isGrouped()) {
				out[2] = new Boolean(true);
				out[3] = "";
		  } else {
				out[2] = new Boolean(false);
				out[3] = track;
		  }
		  out[4] = sequence;
		  out[5] = getMinLength();
		  out[6] = getMaxLength();
		  out[7] = getMinSeqPos();
		  out[8] = getMaxSeqPos();
		  out[9] = getMinRepeat();
		  out[10] = getMaxRepeat();
		  out[11] = getMinWithin();
		  out[12] = getMaxWithin();
		  out[13] = this.toString();
		  return out;
	 }

	 /**
	  * Returns a String that contains the qualifiers (Sequence,
	  * Length, and SeqPos) formatted for a query.
	  */
	 public String toStringQualifiers() {
		  String out = "";

		  // add sequence
		  if (sequence != "") out += " S:" + sequence;

		  // add length and seqPos
		  String tmp = "";
		  if (minLength > -1) {
				if (maxLength == -1) { // <min>
					 tmp = " <" + Integer.toString(minLength);
				} else { // <min, max>
					 tmp = " <" + Integer.toString(minLength) + ", " + Integer.toString(maxLength);
				}
		  }
		  if (minSeqPos > -1) {
				if (tmp == "") tmp = " <";
				if (maxSeqPos == -1) { // ;min>
					 tmp += "; " + Integer.toString(minSeqPos) + ">";
				} else { // ;min, max>
					 tmp += "; " + Integer.toString(minSeqPos) + ", " + Integer.toString(maxSeqPos) + ">";
				}
		  } else {
				if (tmp != "") tmp += ">";
		  }
		  out += tmp;

		  // add repeat
		  tmp = "";
		  if (minRepeat > 1) {
				if (maxRepeat == 1) { // {min}
					 tmp = " {" + Integer.toString(minRepeat);
				} else { // {min, max}
					 tmp = " {" + Integer.toString(minRepeat) + ", " + Integer.toString(maxRepeat);
				}

				if (minWithin > 0) {
					 if (maxWithin == 0) { // ; min}
						  tmp += "; " + Integer.toString(minWithin);
					 } else { // ;min, max}
						  tmp += "; " + Integer.toString(minWithin) + ", " + Integer.toString(maxWithin);
					 }
				}

				tmp += "}";
		  }
		  out += tmp;

		  return out;
	 }

	 /** Returns QueryElement information for debugging purposes. */
	 public String toString() {
		  if ((! isGrouped()) && (track == "")) return "";

		  // add operator
		  String out = "";
		  //		  if (operator.length() > 0) out += " " + operator + " ";
		  if (operator > -1) out += " " + getOperatorVal() + " ";

		  if (isGrouped()) {
				// add negate and group
				if (negate) out += "! ( ";
				else out += "( ";
				for (Iterator i = group.iterator(); i.hasNext();) {
					 out += i.next();
				}
				out += " )";
		  } else {
				// add negate and track
				if (negate) out += "! " + track;
				else out += track;
		  }

		  out += toStringQualifiers();

		  return out;
	 }
} // QueryElement.java
