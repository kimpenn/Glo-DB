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
 * @(#)FASTAParser.java
 */

package edu.upenn.gloDB.io;

// import edu.upenn.gloDB.*;
import java.util.HashMap;

/**
 * Interface for parsing of FASTA headers.  Since there is no standard
 * for FASTA headers, this allows for users to define their own parser
 * if necessary.
 *
 * @author  Stephen Fisher
 * @version $Id: FASTAParser.java,v 1.3.2.3 2005/01/07 19:56:59 fisher Exp $
 */

public interface FASTAParser {
	 
	 public HashMap parseHeader(String header);

} // FASTAParser.java
