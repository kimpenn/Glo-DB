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
 * @(#)ParserUtils.java
 */

package edu.upenn.gloDB.parser;

import edu.upenn.gloDB.*;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.StringReader;

/**
 * Methods to process the Operations ArrayList produced by the parser.
 *
 * @author  Stephen Fisher
 * @version $Id: ParserUtils.java,v 1.46.2.21 2007/03/01 21:17:33 fisher Exp $
 */

public class ParserUtils {

	 public static boolean debug = false;
	 public static boolean debugSolveAll = false;
	 public static boolean debugSolveOps = false;

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 /**
	  * This will parse the input string to create the operations
	  * array, then compute the operations and assign the results to
	  * the Track indicated.  If the Track already exists, it will be
	  * overwritten.
	  * @XXX For now, trackPool and sequencePool are used as the Track
	  * and Sequence sets.  These should be options that the user can
	  * set within the parse string.  Within the parser they are used
	  * to determine if a Track or Sequence object is valid and are
	  * used as the set of all Tracks for the "__T" parser option.
	  */
	 public static Track compute(String parse) {
		  Parser parser = new Parser(new StringReader(parse));
		  ArrayList ops;

		  try {
				// create the array of operations
				ops = parser.run(ObjectHandles.trackPool, ObjectHandles.sequencePool);
		  } catch (ParseException e) {
				if (e.getMessage() == null) {
					 GloDBUtils.printError("Invalid expression.  String can not be solved.");
				} else {
					 GloDBUtils.printError("Invalid expression. " + e.getMessage());
				}
				return null;
		  } catch (TokenMgrError e) {
				if (e.getMessage() == null) {
					 GloDBUtils.printError("Invalid expression.  String can not be solved.");
				} else {
					 GloDBUtils.printError("Invalid expression. " + e.getMessage());
				}
				return null;
		  }

		  // not sure if this will ever happen
		  if (ops == null) return null;

		  // get the id for the assignment track
		  String id = parser.getId();

		  Track track;
		  if (ObjectHandles.trackPool.containsKey(id)) {
				// warn user if 'id' is being overwritten
				GloDBUtils.printMsg("Track \"" + id + "\" already existed and has been overwritten.", 
									GloDBUtils.WARNING);

				// get the existing Track object and erase its attributes
				track = ObjectHandles.getTrack(id);
				track.erase();

				// XXX this is inefficient but will retain any links to
				// the existing Track that the user might already have
				track.setFeatures(solveOpsRecurse(ops).getFeatures());
		  } else {
				track = solveOpsRecurse(ops);

				// add output Track to set of all Track
				ObjectHandles.addTrack(track);

				// need to set the Track ID.  This will also add the Track
				// to ObjectHandles.trackPool
				try { track.setID(id); }
				catch (InvalidIDException e) { 
					track.setID(Track.randomID("_" + id + "_"));
				}
		  }

		  return track;
	 }

	 /**
	  * This will run solveOpsRecurse() which will recursively solve
	  * the Operations in the ArrayList 'ops'.  This wrapper will make
	  * sure the output Track is added to the trackPool.  The temporary
	  * Tracks created along the way are not added to the trackPool.
	 */
	 public static Track solveOps(ArrayList ops) {
		  return solveOps(ops, "");
	 }

	 /**
	  * This will run solveOpsRecurse() which will recursively solve
	  * the Operations in the ArrayList 'ops'.  This wrapper will make
	  * sure the output Track is added to the trackPool.  The temporary
	  * Tracks created along the way are not added to the trackPool.
	  * The 'id' will be used as the output Track ID.
	 */
	 public static Track solveOps(ArrayList ops, String id) {
		  if (ops == null) return null;

		  // get output as a Track
		  Track out = solveOpsRecurse(ops);
		  
		  // add output Track to trackPool.  if ID already exists, then
		  // add a random tag to the ID.  if ID is blank then add the
		  // Track to trackPool and don't change the ID.
		  if (id == "") {
				// add output Track to set of all Track
				ObjectHandles.addTrack(out);
		  } else {
				try { out.setID(id); }
				catch (InvalidIDException e) { 
					 out.setID(Track.randomID("_" + id + "_"));
				}
		  }

		  return out;
	 }

	 /**
	  * This will solve the Operations in the ArrayList 'ops', calling
	  * itself to recursively resolve groups of Operations.
	 */
	 private static Track solveOpsRecurse(ArrayList ops) {
		  if (ops == null) return null;

		  // the output set of Tracks, initialized to the left hand side
		  // of the Operator or set of Operators.
		  Operation operation = getOperation((Operation) ops.get(0));

		  // start out with a copy of the initial Track.  This will
		  // serve as the left side of the next Operation.  
		  
		  // XXX by cloning the Track, we don't change the original
		  // Operation, allowing this array of Operations to be solved
		  // again.  Since the Operation already has a cloned Track,
		  // the usefullness of this may not outway the computational
		  // cost.
		  Track out = (Track) operation.track.cloneTrack(false);

		  // loop through all Operations
		  for (int i = 1; i < ops.size(); i++) {
				operation = getOperation((Operation) ops.get(i));

				if (debugSolveOps) System.out.println("Operation : " + i);
				if (debugSolveOps) System.out.println("Op type: " + operation.getType());

				out = Operator.processOperation(out, operation);

				if (debugSolveOps) System.out.println(out);
		  }

		  return out;
	 }

	 /**
	  * For Operations that contain groups, this will solve the group
	  * and store the resulting TreeSet of Tracks in the Operation.
	  * This will be called recursively if there are nested groups.
	  */
	 private static Operation getOperation(Operation operation) {
		  //		  if (operation.track == null) {
		  if (operation.isGroup()) {
				if (debug) System.out.println("** FOUND NESTED GROUP **");
				
				// 'solved' will contain ALL matches and EACH match will
				// be a Track, the collection of Tracks is the new
				// value of "operation.track".  These Tracks are not
				// in ObjectHandle.trackPool.
				operation.track = solveOpsRecurse(operation.getGroup());
				if (operation.track != null) { // add the set of Tracks to the Operation.  
					 // perform length and seqPos filters
					 //					 operation.filterOnLength();
					 operation.filterOnLength();
					 operation.filterOnSeqPos();
					 operation.filterOnRepeat();

					 // if negate is true, then binary invert the Features
					 if (operation.isNegate()) {
						  operation.track = negate(operation.track);
					 }
				} else {
					 // no match so set tracks to an empty Track, instead
					 // of null.  This way we won't try to solve the group
					 // again.
					 operation.track = new Track(false);
				}
				if (debug) System.out.println("matched nested tracks: " + operation.track.getFeatures());
				if (debug) System.out.println("** FINISHED PROCESSING NESTED GROUP **\n");
		  } else {
				// if negate is true, then binary invert the Features
				if (operation.isNegate()) {
					 operation.track = negate(operation.track);
				}
		  }

		  // since this is a new (untested) Operation, need to reset the
		  // Operation's Track iterator.
		  operation.resetTrack();

		  return operation;
	 }

	 /** 
	  * This will perform a 'binary' inversion of the Features in the
	  * Track.
	  */
	 private static Track negate(Track track) {
		  Track out = new Track(false);
		  
		  // can't invert a Track that doesn't exist
		  if (track.numFeatures() == 0) return out;

		  // merge all overlapping Features.
		  track.mergeContiguous();
		  HashMap sources = track.getSources();

		  // step through each Sequence
		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();
				Sequence sourceObj = (Sequence) ObjectHandles.sequencePool.get(source);

				int sourceMax = sourceObj.getMax() - 1;

				// invert Features for this source
				TreeSet features = (TreeSet) sources.get(source);
				Iterator i = features.iterator();

				// get first Feature to deal with case when first
				// Feature doesn't start at 0
				if (! i.hasNext()) continue;
				Feature feature = (Feature) i.next();

				int minFeature = feature.getMin();
				if (minFeature > 0) { 
					 // deal with first Feature not starting at 0
					 //					 out.addFeature(new ExactFeature(0, minFeature, sourceObj));
					 out.addFeature(new ExactFeature(0, minFeature - 1, sourceObj));
				}

				//				int minOut = feature.getMax();
				int minOut = feature.getMax() + 1;
				while (i.hasNext()) {
					 feature = (Feature) i.next();
					 //					 minFeature = feature.getMin();
					 minFeature = feature.getMin() - 1;
					 out.addFeature(new ExactFeature(minOut, minFeature, sourceObj));
					 //					 minOut = feature.getMax();
					 int fMax = feature.getMax() + 1;
					 if (fMax > minOut) {
						  minOut = fMax;
						  if ((fMax - 2) >= sourceMax) {
								GloDBUtils.printError("Feature position exceeds sequence length");
								return new Track(false);
						  }
					 }
				}

				if (minOut <= sourceMax) {
					 out.addFeature(new ExactFeature(minOut, sourceMax, sourceObj));
				}
		  }

		  return out;
	 }
} // ParserUtils.java
