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
 * @(#)SequenceFile.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.Sequence;
import java.util.HashSet;

/**
 * Interface for files that contain Sequences (ie. FASTA).
 *
 * @author  Stephen Fisher
 * @version $Id: SequenceFile.java,v 1.1.2.5 2005/01/07 19:56:59 fisher Exp $
 */

public interface SequenceFile extends DataFile {

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /** 
	  * Load the first sequence in the data file and return the
	  * resulting Sequence object. 
	  */
	 public Sequence load(String filename);

	 /** 
	  * Load the first sequence in the data file and return the
	  * resulting Sequence object. 
	  */
	 public Sequence load(String filename, String id);

	 /**
	  * Load the first Sequence in the data file and return the
	  * resulting Sequence object.
	  */
	 public Sequence load(String filename, String id, FASTAParser parser);

	 /** 
	  * Load all Sequences in the data file and return a HashSet
	  * containing the resulting Sequence objects.
	  */
	 public HashSet loadAll(String filename);

	 /**
	  * Load all Sequences in the data file and return a HashSet
	  * containing the resulting Sequence objects.
	  */
	 public HashSet loadAll(String filename, FASTAParser parser);

	 /** 
	  * Save the Sequence object to a file based on it's ID. This
	  * should use the ID as the filename and set the overwrite flag to
	  * 'true'.
	  */
	 public void save(String id);

	 /** Save the Sequence object data. */
	 public void save(String id, String filename, boolean overwrite);

} // SequenceFile.java

	 
