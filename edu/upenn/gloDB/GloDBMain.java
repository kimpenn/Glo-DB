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
 * @(#)GloDBMain.java
 */

package edu.upenn.gloDB;

import org.python.util.InteractiveConsole; 
import org.python.core.*; 
import javax.swing.UIManager;
import java.util.prefs.*;

/**
 * GloDBMain program.
 *
 * @author  Stephen Fisher
 * @version $Id: GloDBMain.java,v 1.1.2.11 2007/03/01 21:17:32 fisher Exp $
 */

public class GloDBMain { 
	 public static InteractiveConsole console = new InteractiveConsole();
	 public static boolean ISBATCH = false;

	 /** 
	  * Built-in default property values.  
	  */
	 //	 public static Preferences systemDefaults = Preferences.systemRoot().node("/edu/upenn/gloDB");

	 /** User established properties. */
	 public static Preferences userDefaults = Preferences.userRoot().node("/edu/upenn/gloDB");

    /**
     * @param args  Command line arguments.
     */
    public static void main(String[] args) throws PyException { 
		  try {
				// use the local (platform-dependent) look and feel for the GUI
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		  } catch (Exception e) { }

		  //		  System.out.println("Hello, brave new world");
		  //		  console.push("print sys");
		  //		  console.set("a", new PyInteger(42));
		  //		  console.push("print a");
		  //		  console.push("x = 2 + 2");
		  //		  PyObject x = console.get("x");
		  //		  System.out.println("x: " + x);
		  //		  console.push("print a");

		  // setup some convenient references
		  console.set("trackPool", ObjectHandles.getTrackPool());
		  //		  console.set("featurePool", ObjectHandles.getFeaturePool());
		  console.set("sequencePool", ObjectHandles.getSequencePool());

		  //		  console.push("print");
		  console.push("print 'Glo-DB'");
		  console.push("print 'version 1.0'");
		  console.push("print 'Copyright 2007, 2012 Stephen Fisher and Junhyong Kim, University of Pennsylvania.'");
		  console.push("print 'This program comes with ABSOLUTELY NO WARRANTY; for details type: warranty()'");
		  console.push("print ");

		  try {
				// process the built-in command line arguments
				for (int i = 0; i < args.length; i++) {
					 // check for the BATCH flag
					 if (args[i].compareToIgnoreCase("-BATCH") == 0) {
						  ISBATCH = true;
						  continue;
					 }
				}

				console.push("print 'Loading startup scripts...'");
				// load user-defined functions into the console.
				console.exec("from startup import *");

				// process the user-defined command line arguments
				for (int i = 0; i < args.length; i++) {
					 // skip the BATCH flag
					 if (args[i].compareToIgnoreCase("-BATCH") == 0) {
						  continue;
					 }

					 GloDBUtils.printMsg("Running command line arg: " + args[i]);
					 console.push(args[i]);
				}
		  } catch (Exception e) { 
				System.err.println(" ** ERROR: Error loading startup script.");
				System.err.println(" ** ERROR: Startup script not loaded.");

				// XXX Should turn on the Stack dumping with a flag when
				// running the program.

				// dump stack because getMessage() doesn't work here.
				// 'console' must munge this up.
				System.err.println(e);
		  }

		  if (! ISBATCH) console.interact("");
    }

	 public static String getSystemProperty(String property) {
		  return System.getProperty(property);
	 }

	 public static void setSystemProperty(String property, String value) {
		  if ((property == null) || (property == "")) return;
		  System.setProperty(property, value);
	 }

	 /**
	  * A simple class to experiment with your JVM's garbage collector
	  * and memory sizes for various data types.
	  *
	  * @author <a href="mailto:vlad@trilogy.com">Vladimir Roubtsov</a>
	  */
    public static void sizeOf() {
        // "warm up" all classes/methods that we are going to use:
        runGC();
        usedMemory();
        
        // array to keep strong references to allocated objects:
        final int count = 10000; // 10000 or so is enough for small ojects
        Object[] objects = new Object[count];
        
        long heap1 = 0;

		  Sequence s = new Sequence(false);
		  edu.upenn.gloDB.io.GFFTrack gt = new edu.upenn.gloDB.io.GFFTrack();

        // allocate count+1 objects, discard the first one:
        for (int i = -1; i < count; ++i) {
            Object object;
            
            // INSTANTIATE YOUR DATA HERE AND ASSIGN IT TO 'object':
            
				//            object = new Object(); // 8 bytes
            //object = new Integer (i); // 16 bytes
            //object = new Long (i); // same size as Integer?
            //object = createString (10); // 56 bytes? fine...
            //object = createString (9)+' '; // 72 bytes? the article explains why
            //object = new char [10]; // 32 bytes
            //object = new byte [32][1]; // 656 bytes?!

				//				object = new Sequence();  // 521 bytes
				//				object = new Track(false);  // 283 bytes (248 not in trackPool)
				/*
				Feature feature = new ExactFeature(0, 1, s);  // 184 bytes; 
																 // 144 not in featurePool;
																 // 323 if contains 6 attributes
				String attributes = "";
				attributes += "source=gadfly";
				attributes += "feature=translation";
				attributes += "score=.";
				attributes += "strand=-"; 
				attributes += "frame=.";
				attributes += "attributes=genegrp=CG3038; transgrp=CG3038-RB; name=CG3038:1";
				HashMap attributes = new HashMap();
				attributes.put("source", "gadfly");    // get source
				attributes.put("feature", "translation");   // get feature label
				attributes.put("score", ".");     // get score
				attributes.put("strand", "-");    // get strand
				attributes.put("frame", ".");     // get frame
				attributes.put("attributes", "genegrp=CG3038; transgrp=CG3038-RB; name=CG3038:1");
				feature.setAttributes(attributes);
				feature.setAttributes("source=sim4:na_dbEST.same.dmel;feature=match;score=.;strand=-;frame=.;attributes=ID=:2687161_sim4;Name=RH64340.5prime");
				object = feature;
				*/

				//				gt.load("data/fly/dmel-4-r4.0.gff", FileIO.GFF);

				// claims 3188 bytes but jvm usage increases 35 MB
				object = gt.load("data/gb1.gff");
          
            if (i >= 0)
                objects[i] = object;
            else {
                object = null; // discard the "warmup" object
                runGC();
                heap1 = usedMemory(); // take a "before" heap snapshot
            }
        }

        runGC();
        long heap2 = usedMemory(); // take an "after" heap snapshot:
        
        final int size = Math.round (((float)(heap2 - heap1))/count);
        System.out.println ("'before' heap: " + heap1 + ", 'after' heap: " + heap2);
        System.out.println ("heap delta: " + (heap2 - heap1) +
            ", {" + objects [0].getClass () + "} size = " + size + " bytes");
    }

    // a helper method for creating Strings of desired length
    // and avoiding getting tricked by String interning:
    public static String createString(final int length) {
        final char[] result = new char[length];
        for (int i = 0; i < length; ++i) result[i] = (char) i;
        
        return new String (result);
    }

    // this is our way of requesting garbage collection to be run:
    // [how aggressive it is depends on the JVM to a large degree, but
    // it is almost always better than a single Runtime.gc() call]
	 public static void runGC() {
        // for whatever reason it helps to call Runtime.gc()
        // using several method calls:
		  try {
				for (int r = 0; r < 4; ++r) _runGC();
		  } catch (Exception e) { System.err.println(e); }
    }

    public static void _runGC() {
        long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;

		  try {
				for (int i = 0; (usedMem1 < usedMem2) && (i < 1000); ++i) {
					 s_runtime.runFinalization();
					 s_runtime.gc();
					 Thread.currentThread().yield();
					 
					 usedMem2 = usedMem1;
					 usedMem1 = usedMemory();
				}
		  } catch (Exception e) { System.err.println(e); }
    }

    public static long usedMemory() {
        return s_runtime.totalMemory() - s_runtime.freeMemory();
    }
    
    public static final Runtime s_runtime = Runtime.getRuntime();

} // GloDBMain.java
