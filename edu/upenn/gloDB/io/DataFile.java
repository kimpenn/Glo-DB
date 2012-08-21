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
 * @(#)DataFile.java
 */

package edu.upenn.gloDB.io;


import javax.swing.filechooser.FileFilter;
 
/**
 * Interface for files that contain Features or Sequences (ie. GloDB,
 * FASTA, GFF, and GenBank).
 *
 * @author  Stephen Fisher
 * @version $Id: DataFile.java,v 1.2.2.6 2007/03/01 21:17:33 fisher Exp $
 */

public interface DataFile {

    //--------------------------------------------------------------------------
    // Setters and Getters

	 /**
	  * Get the file ID.  FileIO contains constant values and string
	  * equivalents for built-in DataFiles.
	  */
	 public int getID();

	 /**
	  * Get a description of the file type.  This description will be
	  * used in the file chooser.
	  */
	 public String getDesc();

	 /**
	  * Get an array of file extensions.  These extensions will be used
	  * by the file chooser.
	  */
	 public String[] getExt();

	 /** Get a FileFilter for use in the GUI. */
	 public FileFilter getFileFilter();

} // DataFile.java

	 
