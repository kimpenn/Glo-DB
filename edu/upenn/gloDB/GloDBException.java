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
 * @(#)GloDBException.java
 */

package edu.upenn.gloDB;

/**
 * A wrapper for all GloDB exceptions.
 *
 * @author  Stephen Fisher
 * @version $Id: GloDBException.java,v 1.1.2.4 2007/03/01 21:17:32 fisher Exp $
 */

public class GloDBException extends RuntimeException {

	 /** Create a new GloDBException without a message. */
	 public GloDBException() { 
		  // GloDBUtils.printMsg("GloDBException");
		  // super.printStackTrace();
		  super();
    }

	 /** Create a new GloDBException with a message. */
	 public GloDBException(String message) { 
		  super(message);
    }
} // GloDBException.java

