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
 * @(#)FASTAParserMinimal.java
 */

package edu.upenn.gloDB.io;

import java.util.HashMap;

/**
 * Parses the header line at the beginning of each FASTA sequence.
 * The header line is chopped into substrings at each space (" ").  If
 * a substring contains a ":" then it is assumed to be a key:value
 * pair and is added to the attributes field as such.  Otherwise, it
 * is added as a value to the 'descriptors' key.  This is very similar
 * to FASTAParserFly.java, however it doesn't processes the
 * gene_boundaries in any special way.  Thus this should not be used
 * for FASTA files that contain Features.
 *
 * @XXX this assumes that the header starts with a sequence ID.
 *
 * @author  Stephen Fisher
 * @version $Id: FASTAParserMinimal.java,v 1.7.2.4 2007/03/01 21:17:33 fisher Exp $
 */

public class FASTAParserMinimal implements FASTAParser {
	 
	 public HashMap parseHeader(String header) {
		  HashMap attributes = new HashMap();
		  String descriptors = "";
		  
		  // chop off the ">" from the beginning of the header
		  header = header.substring(1);
		  
		  // split header at each space 
		  String[] attribs = header.split(" ");
		  
		  // the header is assumed to start with a sequence ID
		  attributes.put("ID", attribs[0]);
		  
		  //		  GloDBUtils.printMsg("Loading: " + attribs[0]);
		  
		  String[] tmp;
		  for (int i = 1; i < attribs.length; i++) {
				// split the substring at each colon
				tmp = attribs[i].split(":", 2);
				
				// if tmp only has one value, then the substring 
				// didn't contain a ":", so add it as a descriptor.
				if (tmp.length == 1) {
					 descriptors += " " + tmp[0];
				} else {
					 //					 GloDBUtils.printMsg(attribs[i]+"  "+tmp[0]+"   "+tmp[1]);
					 attributes.put(tmp[0], tmp[1]);
				}
		  }
		  
		  // only add descriptors if they actually exist.
		  if (descriptors.length() > 0) {
				attributes.put("descriptors", descriptors);
		  }
		  
		  return attributes;
	 }

}  // FASTAParserMinimal.java
	 
