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
 * @(#)TrackFile.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.Track;

/**
 * Interface for files that contain Features or Sequences (ie. GloDB,
 * FASTA, GFF, and GenBank).
 *
 * @author  Stephen Fisher
 * @version $Id: TrackFile.java,v 1.1.2.5 2007/03/01 21:17:33 fisher Exp $
 */

public interface TrackFile extends DataFile {

    //--------------------------------------------------------------------------
    // Setters and Getters

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * Load all Features in the data file into a single Track and
	  * return the resulting Track object. 
	  */
	 public Track load(String filename);

	 /**
	  * Load all Features in the data file into a single Track and
	  * return the resulting Track object. 
	  */
	 public Track load(String filename, String sourceID);

	 /** 
	  * Save the Track to a file based on it's ID. This should use the
	  * ID as the filename and set the overwrite flag to 'true'.
	  */
	 public void save(String id);

	 /** Save the object data. */
	 public void save(String id, String filename, boolean overwrite);

} // TrackFile.java

	 
