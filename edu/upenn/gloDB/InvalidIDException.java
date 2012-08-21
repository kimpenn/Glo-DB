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
 * @(#)InvalidIDException.java
 */

package edu.upenn.gloDB;

/**
 * 
 *
 * @author  Stephen Fisher
 * @version $Id: InvalidIDException.java,v 1.2.2.4 2007/03/01 21:17:32 fisher Exp $
 */

public class InvalidIDException extends GloDBException {

	 /** Create a new InvalidIDException without a message. */
	 InvalidIDException() { 
		  // GloDBUtils.printMsg("InvalidIDException");
		  // super.printStackTrace();
		  super();
    }

	 /** Create a new InvalidIDException with a message. */
	 InvalidIDException(String message) { 
		  super(message);
    }
} // InvalidIDException.java
