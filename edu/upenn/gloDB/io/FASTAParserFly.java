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
 * @(#)FASTAParserFly.java
 */

package edu.upenn.gloDB.io;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Parses the header line at the beginning of each sequence.  The
 * header line is chopped into substrings at each space (" ").  If a
 * substring contains a ":" then it is assumed to be a key:value pair
 * and is added to the attributes field as such.  Otherwise, it is
 * added as a value to the 'descriptors' key.  An example of a
 * parseable header is:
 * 
 * <ul> <b>Unparsed:</b><br> >CG2945 gene symbol:cin FBgn0000316
 * seq_release:3 gene_boundaries:(X:12,390..15,908[+]) cyto:1A1-1A1
 * (GO:0006777 "Mo-molybdopterin cofactor biosynthesis") (GO:0001700
 * "embryonic development (sensu Insecta)") </ul>
 * 
 * <ul> <b>Parsed:</b><br> {dbxref=[GO:0006777 "Mo-molybdopterin
 * cofactor biosynthesis", GO:0001700 "embryonic development (sensu
 * Insecta)"], strand=+, cyto=1A1-1A1, seq_release=3,
 * gene_boundaries=X:12,390..15,908, descriptors=gene FBgn0000316,
 * symbol=cin, ID=CG2945} </ul>
 *
 * @XXX can we assume that the header starts with a sequence ID?
 *
 * @author  Stephen Fisher
 * @version $Id: FASTAParserFly.java,v 1.7.2.4 2007/03/01 21:17:33 fisher Exp $
 */

public class FASTAParserFly implements FASTAParser {

	 public HashMap parseHeader(String header) {
		  HashMap attributes = new HashMap();

		  // store descriptors as a string because some descriptors are
		  // more than one word long.
		  String descriptors = "";

		  // some sequences will have more than one dbxref.
		  HashSet dbxref = new HashSet();

		  // chop off the ">" from the beginning of the header
		  header = header.substring(1);

		  // split header at each space 
		  String[] attribs = header.split(" ");
		  
		  // the header is assumed to start with a sequence ID
		  attributes.put("ID", attribs[0]);

		  //		  GloDBUtils.printMsg("Loading: " + attribs[0]);

		  String[] tmp;
		  int i = 1;
		  while (i < attribs.length) {
				String value = attribs[i];

				// test if a dbxref which is contained in "()"
				if (value.startsWith("(")) {
					 // remove initial parenthesis
					 value = value.substring(1);

					 // continue reading dbxref - ends with ")"
					 i += 1;
					 while (i < attribs.length) {
						  value += " " + attribs[i];
						  if (attribs[i].endsWith(")")) { break; }
						  i += 1;
					 }

					 // remove ")"
					 value = value.substring(0, value.length()-1);

					 // add to dbxref hashSet
					 dbxref.add(value);

				} else { // not a dbxref, so try to split at the first ":"
					 // split the substring at the first ":"
					 tmp = attribs[i].split(":", 2);
				
					 if (tmp.length == 1) {
						  // tmp only has one value (the substring doesn't
						  // contain a ":"), so add it as a descriptor.
						  if (descriptors.length() > 0) { descriptors += " "; }
						  descriptors += tmp[0];
						  i += 1;
						  continue;

					 } else if (tmp[0].equalsIgnoreCase("gene_boundaries")) {
						  // XXX: need to create locations here. will need
						  // to reference what sequence??  what is the
						  // format for the position information?  can a
						  // feature have more than one position pair?

						  // remove parenthesis surrounding positions.
						  value = tmp[1].substring(1);
						  value = value.substring(0, value.length()-1);

						  // if ends with "]", then contains strand information "+/-"
						  if (value.endsWith("]")) {
								String strand = value.substring(value.length()-2, value.length()-1);
								value = value.substring(0, value.length()-3);

								// add strand info to hashMap
								attributes.put("strand", strand);
						  }

						  // get the Sequence ID and start/stop boundaries
						  String pos[] = value.split(":", 2);
						  attributes.put("source", pos[0]);
						  // XXX: This assumes that these Locations do NOT
						  // have more than one position pair.
						  attributes.put("boundaries", pos[1]);

						  // add gene_boundaries info to hashMap
						  //						  attributes.put("gene_boundaries", value);
					 } else {
						  attributes.put(tmp[0], tmp[1]);
					 }
				}
				
				i += 1;
		  }
		  
		  // only add descriptors if they actually exist.
		  if (descriptors.length() > 0) {
				attributes.put("descriptors", descriptors);
		  }

		  // only add dbxrefs if they actually exist.
		  if (dbxref.size() > 0) {
				attributes.put("dbxref", dbxref);
		  }

		  //		   GloDBUtils.printMsg("Attributes raw: " + header);
		  //		   GloDBUtils.printMsg("Attributes parsed: " + attributes);
		  
		  return attributes;
	 }

}  // FASTAParserFly.java
	 
