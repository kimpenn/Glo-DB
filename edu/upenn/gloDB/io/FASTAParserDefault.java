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
 * @(#)FASTAParserDefault.java
 */

package edu.upenn.gloDB.io;

import java.util.HashMap;

/**
 * The default parser for FASTA headers.  It returns a HashMap that
 * contains one entry: the key is "FASTA" and the value is the header
 * line with the leading '>' removed.  Thus no assumptions are made
 * about the structure of the header, other than it beginning with a
 * '>'.
 *
 * @author  Stephen Fisher
 * @version $Id: FASTAParserDefault.java,v 1.2.2.4 2007/03/01 21:17:33 fisher Exp $
 */

public class FASTAParserDefault implements FASTAParser {
	 /**
	  * As the default parser, do not process the header at all,
	  * except to remove the leading '>'.  Add the header to
	  * Sequence.attributes using the key 'FASTA'.
	  */
	 public HashMap parseHeader(String header) {
		  HashMap attributes = new HashMap();
		  attributes.put("FASTA", header.substring(1));
		  return attributes;
	 }
}
	 
