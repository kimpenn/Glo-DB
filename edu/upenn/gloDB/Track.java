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
 * @(#)Track.java
 */

package edu.upenn.gloDB;

import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;

/**
 * Tracks are collections of Features and allow accessing the Features
 * as a sorted Set (sorted by source and position information) or
 * grouped by the Feature source information.
 *
 * @author  Stephen Fisher
 * @version $Id: Track.java,v 1.1.2.34 2007/03/01 21:17:33 fisher Exp $
 */

public class Track implements Cloneable { 

	 /** 
	  * This is a unique name for the Track, that is used by the
	  * parser to identify the Track.  This is 'protected' to allow
	  * ObjectHandles to change the value.
	  */
	 protected String id;

	 /** 
	  * This is similar to "qualifiers" in GenBank (ex: scores, strand
	  * (+/-), phase (within codon)).  
	  */
	 protected HashMap attributes = new HashMap();

	 /** 
	  * TreeSet of Feature objects comprising the Track.
	  * @XXX We should be able to remove this set after we create
	  * 'sources' since that is a more useful structure for the Feature
	  * data.
	  */
	 private TreeSet features = null;

	 /** 
	  * Map of Sequence object IDs to Features.  Maintaining this set
	  * slows down the adding/removing of Features but should speed up
	  * the management of the Features.  It allows for easy access of
	  * Features by Sequence.  For each sequence, a TreeSet of
	  * Features is maintained.
	  * @XXX This can not be directly changed by the user but rather is
	  * created and updated based on {@link #features features}.
	  */
	 private HashMap sources = null;

	 /** Used to create random IDs. */
    private static Random random = new Random(System.currentTimeMillis());

	 /** 
	  * Create a new Track object and add it to the trackPool.
	  */
	 public Track() { 
		  this(true, "");
	 }

	 /** 
	  * Create a new Track object with the specified ID, and add it
	  * to the trackPool.
	  */
	 public Track(String id) { 
		  this(true, id);
	 }

	 /** 
	  * Create a new Track object and add the newly create
	  * Track object to the trackPool if addToPool is true.
	  * @XXX This should probably be 'protected' instead of 'public'
	  * because all Tracks should really be added to trackPool.
	  */
	 public Track(boolean addToPool) {
		  this(addToPool, "");
	 }

	 /** 
	  * Create a new Track object and add the newly create
	  * Track object to the trackPool if addToPool is true.
	  * @XXX This should probably be 'protected' instead of 'public'
	  * because all Tracks should really be added to trackPool.
	  */
	 public Track(boolean addToPool, String id) {
		  // if no ID, then create a random ID for this Track
		  if (id == "") id = randomID("_T");
		  this.id = id;

		  if (addToPool) { 
				try {
					 // add self to the trackPool
					 ObjectHandles.addTrack(this);
				} catch (InvalidIDException e) {
					 String id_new = randomID("_T");
					 String msg = "ID \"" + id + "\" already exists, using ID \"" + id_new + "\" instead.";
					 GloDBUtils.printWarning(msg);
					 
					 // add self to set of all Tracks, using new ID
					 this.id = id_new;
					 ObjectHandles.addTrack(this);
				}
		  }
	 }

    //--------------------------------------------------------------------------
    // Setters and Getters

    /** 
	  * Set the ID.  If the new ID is the same as the current ID, then
	  * doesn't do anything.  If the new ID already exists in the
	  * trackPool, then throws an exception.
	  * @param id a String that is a unique identifier for the Track.
	  */
    public void setID(String id) throws InvalidIDException { 
		  try { setID(id, true); } 
		  catch (InvalidIDException e) { throw e; }
	 }

    /** 
	  * Set the ID.  If the new ID is the same as the current ID, then
	  * doesn't do anything.  If the new ID already exists in the
	  * trackPool, then throws an exception.  If 'updatePool' is
	  * true, then the trackPool is updated.  'updatePool' must be
	  * true if the Track is in the trackPool, else the trackPool
	  * will become out of sync.
	  * @param id a String that is a unique identifier for the Track.
	  */
	 public void setID(String id, boolean updatePool) throws InvalidIDException { 
		  // don't do anything if new and old values are the same
		  if (this.id == id) return;

		  if (updatePool) {
				// the Track should already be in trackPool, but if not
				// then warn the user that the Track is being added to the
				// pool
				if (! ObjectHandles.trackPool.containsKey(this.id)) {
					 GloDBUtils.printWarning("Adding track to trackPool.");
				}

				// renameTrack() will do the actual changing of the
				// Track's id.
				try { ObjectHandles.renameTrack(this, id); }
				catch (InvalidIDException e) { throw e; }
		  } else {
				// since not in trackPool, just change ID
				this.id = id;    
		  }
	 }

    /** Get the ID. */
    public String getID() { return id; }

    /** 
	  * Set the attributes. 
	  * @param attributes a HashMap of Feature attributes
	  */
    public void setAttributes(HashMap attributes) { 
		  // make sure attributes is never set to null
		  if (attributes == null) attributes = new HashMap();
		  this.attributes = attributes; 
	 }

    /** Get the attributes. */
    public HashMap getAttributes() { return attributes; }

	 /**  
	  * This will replace 'features' with the TreeSet argument.  This
	  * will update the {@link #sources sources} HashMap based on the
	  * new set of Features.
	  */
	 public void setFeatures(TreeSet features) { 
		  // empty out the existing set of Features
		  this.features = null;
		  this.sources = null;

		  addFeatures(features);
	 }
    
	 /** Get the features, sorted by their min values. */
	 public TreeSet getFeatures() { return features; }
    
	 /** 
	  * Get the features, sorted by their max values. The TreeSet
	  * returned is effectively a clone of this Track's TreeSet and
	  * thus changes to the TreeSet will not be reflected in the
	  * Track's 'features' TreeSet. 
	  */
	 public TreeSet getFeaturesByMax() { 
		  TreeSet featuresByMax = new TreeSet(new FeatureMaxComparator());
		  featuresByMax.addAll(features);
		  return featuresByMax;
	 }
    
	 /** Get the map of source Sequences to Features. */
	 public HashMap getSources() { return sources; }
    
    //--------------------------------------------------------------------------
    // Miscellaneous Methods
   
	 /** Sort the Feature TreeSet by max values. */
	 public static TreeSet sortByMax(TreeSet features) { 
		  TreeSet featuresByMax = new TreeSet(new FeatureMaxComparator());
		  featuresByMax.addAll(features);
		  return featuresByMax;
	 }
    
	 /** 
	  * This will remove all Features from this Track that do not exist
	  * on the specified Sequence.  
	  */
	 public void filterOnSequence(String sequence) {
		  // if no Sequence or Features then don't do anything
		  if ((sequence.length() == 0) || (numFeatures() == 0)) return;

		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// if same sequence, then skip
				if (source.compareToIgnoreCase(sequence) == 0) continue;

				// different source, so remove the features
				TreeSet sFeatures = (TreeSet) sources.get(source);
				for (Iterator f = sFeatures.iterator(); f.hasNext();) {
					 features.remove(f.next());
				}

				// now remove Sequence key from the sources HashMap
				s.remove();
		  }
	 }

	 /** 
	  * This will remove all Features from this Track that are not
	  * within the 'min'/'max' boundaries.  If 'max' is -1, then goes
	  * to maximum Sequence length.
	  * @XXX Should throw an exception if max < min.
	  */
	 public void filterOnSeqPos(int min, int max) {
		  // make sure positions are legal
		  if ((max != -1) && (max < min)) { 
				GloDBUtils.printError("The max pos is less than the min pos, when filtering on sequence position.");
				return; 
		  }

		  // if max is 0 or no features then don't filter
		  if ((max == 0) || (numFeatures() == 0)) return;

		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// remove relevant features
				TreeSet sFeatures = (TreeSet) sources.get(source);
				for (Iterator f = sFeatures.iterator(); f.hasNext();) {
					 Feature feature = (Feature) f.next();
					 if ((feature.getMin() < min) 
						  || ((max != -1) && (feature.getMax() > max))) {
						  features.remove(feature);
						  f.remove();  // remove Feature from sources HashMap
					 }
				}

				// if no more Features on this source, then remove the source
				if (sFeatures.size() == 0) s.remove();
		  }
	 }

	 /** 
	  * This will remove all Features from this Track that are outside
	  * of the specifed range.
	  * @XXX Should throw an exception if max < min.
	  */
	 public void filterOnLength(int min, int max) {
		  // make sure lengths are legal
		  if (max < min) { 
				GloDBUtils.printError("The max length is less than the min length, when filtering on length.");
				return; 
		  }

		  // if max is 0 or no features then don't filter
		  if ((max == 0) || (numFeatures() == 0)) return;

		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// remove relevant features
				TreeSet sFeatures = (TreeSet) sources.get(source);
				for (Iterator f = sFeatures.iterator(); f.hasNext();) {
					 Feature feature = (Feature) f.next();
					 if ((feature.length() < min) || (feature.length() > max)) {
						  features.remove(feature);
						  f.remove();  // remove Feature from sources HashMap
					 }
				}

				// if no more Features on this source, then remove the source
				if (sFeatures.size() == 0) s.remove();
		  }
	 }

	 /** 
	  * This will remove all Features from this Track that do not
	  * conform to the repeat criterion.  Within min/max are used to
	  * define the min/max space between features.  Repeat min/max are
	  * used to define the min/max number of features that must follow
	  * in a row, based on the min/max criterion, in order for those
	  * features to be included.  
	  * @XXX Should throw an exception if max < min.
	  * @XXX Need to allow for within values that don't have a min.
	  */
	 public void filterOnRepeat(int minR, int maxR, int minW, int maxW) {
		  // make sure repeats are legal
		  if (maxR < minR) { 
				GloDBUtils.printError("The max repeat is less than the min repeat, when filtering on repeats.");
				return; 
		  }
		  if (maxW < minW) { 
				GloDBUtils.printError("The max within is less than the min within, when filtering on repeats.");
				return; 
		  }

		  // if minR/maxR are less than 2 or no features then nothing to
		  // filter
		  if ((minR < 2) || (maxR < 2) || (numFeatures() == 0)) return;

		  // this will contain all features that need to be removed from
		  // the track
		  ArrayList toBeRemoved = new ArrayList();

		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// step through all features on current source
				TreeSet features = (TreeSet) sources.get(source);
				Iterator i = features.iterator();
				
				// this is the first feature of the current repeat
				Feature fRepeat = (Feature) i.next();

				// fStart/fEnd refer to the range for the beginning of the
				// next Feature
				int fStart = (minW > 0) ? fRepeat.getMax() + minW : fRepeat.getMin(); 
				int fEnd = fRepeat.getMax() + maxW;

				// count of how many repeats found
				int count = 1;

				while (i.hasNext()) {
					 Feature fCurrent = (Feature) i.next();

					 // test if new feature is within appropriate range.
					 // if fStart = 0 then allow for features overlapping
					 if ((fCurrent.getMin() >= fStart) && (fCurrent.getMin() <= fEnd)) {
						  // valid repeat
						  count++;

						  if (count > maxR) {
								// reached max number of features in a
								// repeating group, so start over from here
								fRepeat = fCurrent;

								// reset repeat counter
								count = 1;
						  } 

						  if ((! i.hasNext()) && (count < minR)) {
								// not enough repeats so discard from fRepeat
								// to the current feature
								toBeRemoved.addAll(features.subSet(fRepeat, fCurrent));
								toBeRemoved.add(fCurrent);
						  }

					 } else {
						  if (count < minR) {
								// not enough repeats so discard from fRepeat
								// to the current feature
								toBeRemoved.addAll(features.subSet(fRepeat, fCurrent));
						  }
						  
						  if (! i.hasNext()) {
								// started a new repeat but there is
								// nothing left to repeat with
								toBeRemoved.add(fCurrent);
						  }

						  // start new over from here, because were able to
						  // discard features from fRepeat, or found enough
						  // repeats (current >= minR)
						  fRepeat = fCurrent;

						  // reset repeat counter
						  count = 1;
					 }

					 // if count is 1, then we started a new group
					 if (count == 1) {
						  // update for new feature
						  fStart = (minW > 0) ? fCurrent.getMax() + minW : fCurrent.getMin(); 
						  fEnd = fCurrent.getMax() + maxW;
					 } else {
						  // update for new feature
						  int tStart = (minW > 0) ? fCurrent.getMax() + minW : fCurrent.getMin(); 
						  int tEnd = fCurrent.getMax() + maxW;

						  // only replace range boundaries if new feature has a
						  // stop point larger than the previous stop point
						  if (tEnd > fEnd) {
								fStart = tStart;
								fEnd = tEnd;
						  }
					 }
				}
		  }
			
		  // remove non-repeating features
		  for (Iterator i = toBeRemoved.iterator(); i.hasNext();) {
				removeFeature((Feature) i.next());
		  }
	 }

	 /** 
	  * This will remove all Features from this Track that do not
	  * contain the specified attribute.
	  */
	 public void filterOnAttribute(String key, String value) {
		  // make sure key and value are non-null
		  if (key.length() == 0) {
				GloDBUtils.printError("Empty key, when filtering on attribute.");
				return; 
		  }
		  /*
		  if (value.length() == 0) { 
				GloDBUtils.printError("Empty value, when filtering on attribute.");
				return; 
		  }
		  */

		  // if no features then nothing to filter
		  if (numFeatures() == 0) return;

		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// remove relevant features
				TreeSet sFeatures = (TreeSet) sources.get(source);
				for (Iterator f = sFeatures.iterator(); f.hasNext();) {
					 Feature feature = (Feature) f.next();
					 if ((! feature.containsAttribute(key))
						  || (! feature.getAttribute(key).equals(value))) {
						  features.remove(feature);
						  f.remove();  // remove Feature from sources HashMap
					 }
				}

				// if no more Features on this source, then remove the source
				if (sFeatures.size() == 0) s.remove();
		  }
	 }

    /** Add an attribute. */
    public void addAttribute(Object key, Object value) { attributes.put(key, value); }

    /** Remove an attribute. */
    public void delAttribute(Object key) { attributes.remove(key); }

    /** Returns true if attribute 'key' exists. */
    public boolean containsAttribute(Object key) { return attributes.containsKey(key); }

    /** Get value for attribute 'key'. */
    public Object getAttribute(Object key) { return attributes.get(key); }
    
	 /**  
	  * This will add 'features' to the current feature set.  This will
	  * update the {@link #sources sources} HashMap based on the new
	  * set of Features.
	  */
	 public void addFeatures(TreeSet features) { 
		  // if features is null, then just need to update the
		  // ObjectHandle sets
		  if (features != null) {
				for (Iterator i = features.iterator(); i.hasNext();) {
					 // use addFeature() to build the sources HashMap
					 addFeature((Feature) i.next(), false);
				}
		  }

		  // rebuild trackPool lists if the Track is in the trackPool
		  if (ObjectHandles.containsTrack(id)) ObjectHandles.rebuildTrack(this);
	 }
    
	 /** 
	  * Adds a Feature to 'features'.  This will update the {@link
	  * #sources sources} HashMap.  If 'features' doesn't exist a new
	  * TreeSet will be created.  If 'newFeature' is null, then this
	  * method won't do anything.
	  */
	 public void addFeature(Feature newFeature) { 
		  // rebuild the trackPool lists if the Track is in the
		  // trackPool.
		  if (ObjectHandles.containsTrack(id)) {
				addFeature(newFeature, true);
		  } else {
				addFeature(newFeature, false);
		  }
	 }

	 /** 
	  * Adds a Feature to 'features'.  This will update the {@link
	  * #sources sources} HashMap.  If 'features' doesn't exist a new
	  * TreeSet will be created.  If 'newFeature' is null, then this
	  * method won't do anything.
	  */
	 public void addFeature(Feature newFeature, boolean rebuildPool) { 
		  // ignore null newFeature values
		  if (newFeature != null) { 
				// create features if it doesn't already exist.  Create
				// sources also, since if there are no features then
				// sources must also not exist.
				if (features == null) { 
					 features = new TreeSet(); 
					 sources = new HashMap();
				}

				features.add(newFeature); 
				String sequence = newFeature.getSource().getID(); 
				TreeSet seqSet;
				if (sources.containsKey(sequence)) {
					 seqSet = (TreeSet) sources.get(sequence);
				} else {
					 seqSet = new TreeSet();
				}
				seqSet.add(newFeature);
				sources.put(sequence, seqSet);

				// only rebuild the trackPool structures if necessary
				if (rebuildPool) ObjectHandles.rebuildTrack(this);
		  }
	 }
    
	 /** 
	  * Removes a Feature from 'features'.  This will update
	  * the {@link #sources sources} HashMap.  If 'newFeature' is null,
	  * then this method won't do anything.
	  */
	 public void removeFeature(Feature newFeature) { 
		  // don't do anything if newFeature or features is empty
		  if ((newFeature != null) && (features != null)) { 
				features.remove(newFeature); 
				String sequence = newFeature.getSource().getID();
				TreeSet seqSet = (TreeSet) sources.get(sequence);
				if (seqSet.size() > 1) {
					 seqSet.remove(newFeature);
					 sources.put(sequence, seqSet);
				} else {
					 // only Feature on Sequence, so remove entire key
					 sources.remove(sequence);
				}
		  }
	 }
    
	 /**
	  * Returns the number of Features contained in the Track.  If
	  * Features exactly overlap, they will be still be counted
	  * separately.
	  */
	 public int numFeatures() { 
		  if (features == null) {
				return 0;
		  } else {
				return features.size();
		  }
	 }

	 /** Returns the number of Sources spanned by the Track. */
	 public int numSources() { 
		  if (numFeatures() == 0) {
				return 0;
		  } else {
				return sources.size();
		  }
	 }

	 /** Returns an Iterator over 'features'. */
	 public Iterator featureIterator() { 
		  if (numFeatures() == 0) {
				return null;
		  } else {
				return features.iterator(); 
		  }
	 }

	 /** Get the set of source Sequence objects. */
	 public Set getSourceSet() { 
		  if (sources == null) {
				return null;
		  } else {
				return sources.keySet(); 
		  }
	 }
    
	 /** 
	  * Get the set of Features based on the Sequence object. 
	  */
	 public TreeSet featuresBySource(Sequence sequence) { 
		  return featuresBySource(sequence.getID()); 
	 }
    
	 /** 
	  * Get the set of Features based on the Sequence ID. 
	  */
	 public TreeSet featuresBySource(String sequence) { 
		  if (sources == null) {
				return null;
		  } else {
				return (TreeSet) sources.get(sequence); 
		  }
	 }
    
	 /** 
	  * Returns the sequence data.  Sequence data that occurs on
	  * different contigs or is non-contiguous with separate items in
	  * the ArrayList.
	  * @XXX Should return sets of Sequences.  A new set for each
	  * sequence and within each Sequence set, a new set for each
	  * non-contiguous Feature.  However, if 2 Sequences have same data
	  * (ie to contigs are a repeat), then using Sets won't work.
	  */
	 public ArrayList getData() { 
		  if (sources == null) return null;

		  ArrayList output = new ArrayList();
		  
		  // loop through all sources
		  for (Iterator seqs = sources.keySet().iterator(); seqs.hasNext();) {
				String seqID = (String) seqs.next();
				Sequence seq = (Sequence) ObjectHandles.sequencePool.get(seqID);
				TreeSet track = (TreeSet) sources.get(seqID);

				// loop through the Set of features based on the source.
				// If sequences overlap or are contiguous, then merge
				// them.
				Iterator features = track.iterator();
				Feature feature = (Feature) features.next();
				int start = feature.getMin();
				int stop = feature.getMax();

				while (features.hasNext()) {
					 feature = (Feature) features.next();

					 // if find a gap then start next sequence string
					 if (feature.getMin() > (stop + 1)) { 
						  output.add(seq.getDataBounded(start, stop));
						  start = feature.getMin();
					 }
					 
					 if (stop < feature.getMax()) stop = feature.getMax();
				}

				output.add(seq.getDataBounded(start, stop));
		  }
		  return output; 
	 }

	 /** 
	  * Returns the sequence data formatted with "\n" inserted every
	  * Sequence.FORMAT_WIDTH characters and blank lines inserted
	  * between sequences.
	  */
	 public String getDataFormatted() {
		  if (sources == null) return "";

		  ArrayList dataArray = getData();

		  StringBuffer out = new StringBuffer("");

		  int i = 0;
		  int total = 0;
		  for (Iterator iDA = dataArray.iterator(); iDA.hasNext();) {
				String data = (String) iDA.next();

				// don't insert blank line if first sequence.
				if (total != 0) out.append("\n");

				i = Sequence.FORMAT_WIDTH;
				total = data.length();

				while (i < total) {
					 out.append(data.substring(i - Sequence.FORMAT_WIDTH, i) + "\n");
					 i += Sequence.FORMAT_WIDTH;
				}

				// have less than Sequence.FORMAT_WIDTH chars remaining in sequence
				if (i >= total) out.append(data.substring(i - Sequence.FORMAT_WIDTH, total) + "\n");
		  }

		  return out.toString();
	 }

	 /** 
	  * Returns the sequence data formatted as a multi-sequence FASTA
	  * file.  New lines ("\n") are inserted every
	  * Sequence.FORMAT_WIDTH characters and a blank line is inserted
	  * between sequences.
	  */
	 public String getDataFASTA() {
		  if (sources == null) return "";

		  StringBuffer out = new StringBuffer("");

		  // loop through all sources
		  for (Iterator seqs = sources.keySet().iterator(); seqs.hasNext();) {
				String seqID = (String) seqs.next();
				TreeSet track = (TreeSet) sources.get(seqID);

				// loop through the Set of features based on the source.
				for (Iterator features = track.iterator(); features.hasNext();) {
					 Feature feature = (Feature) features.next();
					 
					 // XXX should include more formatting
					 String header = ">" + seqID + " ";

					 // create a copy of the attributes so we can remove
					 // objects from the HashMap as we process them below
					 HashMap attribs = feature.getAttributesMap();

					 // start with the ID attribute, if not present, then
					 // use the Track's ID.
					 if (attribs.containsKey("ID")) {
						  header += attribs.get("ID");
						  attribs.remove("ID");
					 } else {
						  header += id;
					 }

					 // if contains 'descriptors' then remove label
					 if (attribs.containsKey("descriptors")) {
						  header += " " + attribs.get("descriptors");
						  attribs.remove("descriptors");
					 }

					 // if "gene_boundaries" already exists, then we
					 // probably didn't process this header and so we
					 // should just leave it alone
					 if (! attribs.containsKey("gene_boundaries")) {
						  String gb = "gene_boundaries:(" + feature.getSource().getID() + ":";
						  gb += feature.getStart() + ".." + feature.getStop();

						  // if 'source' already handled, then remove from
						  // attribs map
						  if (attribs.containsKey("source")) {
								String value = (String) attribs.get("source");
								//								if (value.equalsIgnoreCase(feature.getSource().getID())) {
								if (value.equals(feature.getSource().getID())) {
									 attribs.remove("source");
								}
						  }

						  // if 'boundaries' exists then remove from attribs
						  // map, because this should be equivalent to the
						  // Feature's start/stop
						  attribs.remove("boundaries");

						  if (attribs.containsKey("strand")) {
								gb += "[" + (String) attribs.get("strand") + "]";
								attribs.remove("strand");  // don't need anymore
						  }
						  gb += ")";
						  header += " " + gb;
					 }

					 // if contains 'dbxref' then remove label and enclose
					 // in '()'
					 if (attribs.containsKey("dbxref")) {
						  header += " (";
						  Set dbxref = (Set) attribs.get("dbxref");
						  for (Iterator dI = dbxref.iterator(); dI.hasNext();) {
								header += dI.next();
						  }
						  header += ")";
						  attribs.remove("dbxref");
					 }

					 // add remaining attributes to the header
					 for (Iterator l = (attribs.keySet()).iterator(); l.hasNext();) {
						  String key = (String) l.next();
						  header += " " + key + ":" + attribs.get(key);
					 }

					 out.append(header + "\n");
					 out.append(feature.getDataFormatted() + "\n");
				}
		  }

		  return out.toString();
	 }

	 /**
	  * Returns 'true' if the Track does not contain gaps between
	  * Features.  If the Features occur on different sequences , then
	  * this will return 'false'.
	  */
	 public boolean isContiguous() { 
		  // return false if more than one source or no features
		  if ((numFeatures() == 0) || (sources.size() > 1)) return false;
		  return FeatureUtils.isContiguous(features);
	 }

	 /**
	  * This will merge all overlapping Features in the Track,
	  * creating new Feature objects as necessary.
	  */
	 public void mergeContiguous() { 
		  setFeatures(FeatureUtils.mergeContiguous(this));
	 }

	 /**
	  * This will merge all Features in the Track that are within
	  * maxSpace of each other.  New Features will be created to span
	  * the entire cluster.  Threshold sets the minimum number of
	  * Features necessary to be considered a cluster and thus included
	  * in the output set.  A new Track will be returned containing the
	  * clusters.  This will return 'null' if there is no match.
	  * @deprecated replaced with cluster.py
	  * @param id the name of the new Track
	  * @param maxSpace the maximum allowed space between Features in a
	  * cluster
	  * @param threshold the minimum number of Features needed in a
	  * cluster, for the cluster to be included in the output
	  */
	 public Track cluster(String id, int maxSpace, int threshold) { 
		  TreeSet features = FeatureUtils.cluster(this, maxSpace, threshold);

		  // return null if no match
		  if (features.size() == 0) return null;

		  if (id.length() == 0) {
				// use the parent Track's ID as the base for a random ID.
				// The clone isn't added to the trackPool but still make
				// sure it has a valid ID.
				id = randomID("_" + this.id + "_");
		  }
		  Track track = new Track(id);
		  track.attributes = this.attributes;
		  track.setFeatures(features);
		  return track;
	 }

	 /** 
	  * This will return a copy of the track without any duplicate
	  * features (based on start/stop values).  
	  */
	 public Track noRepeats() {
		  boolean saveFlag = GloDBUtils.ignoreAttributes();
		  GloDBUtils.setIgnoreAttributes(true);
		  Track newTrack = (Track) clone();
		  GloDBUtils.setIgnoreAttributes(saveFlag);
		  return newTrack;
	 }

	 /** 
	  * This will remove duplicate features, based on start/stop
	  * values.  Attributes for the first of the duplicates will be the
	  * primary attributes used.  NOTE that this is not relevant if
	  * GloDBUtils.IGNORE_ATTRIBUTES is set to 'true'.
	  */
	 /*
	 public void removeRepeats() {
		  if (sources == null) return;

		  // loop through all sources
		  for (Iterator s = sources.keySet().iterator(); s.hasNext();) {
				String source = (String) s.next();

				// loop through the Set of features based on the source.
				TreeSet sFeatures = (TreeSet) sources.get(source);
				Iterator f = sFeatures.iterator(); 
				if (! f.hasNext()) return;

				// get first feature
				Feature featureLast = (Feature) f.next();
				int lastStart = featureLast.getStart();
				int lastStop = featureLast.getStop();

				while (f.hasNext()) {
					 Feature feature = (Feature) f.next();

					 // XXX not sure why ".equals()" doesn't work
					 //					 if (featureLast.equals(feature)) {
					 if ((lastStart == feature.getStart()) && (lastStop == feature.getStop())) {
						  // merge attributes, keeping
						  // featureLast.attributes as the default
						  HashMap attribs = feature.getAttributesMap();
						  attribs.putAll(featureLast.getAttributesMap());
						  featureLast.setAttributes(attribs);

						  // remove current feature from collection
						  features.remove(feature);
						  f.remove();  // remove Feature from sources HashMap
					 } else {
						  featureLast = feature;
						  lastStart = featureLast.getStart();
						  lastStop = featureLast.getStop();
					 }
				}

				// if no more Features on this source, then remove the source
				if (sFeatures.size() == 0) s.remove();
		  }
	 }
	 */

	 /**
	  * Returns 'true' if the Features contained in the Track all refer
	  * to the same sequence.  This is similar to {@link #isContiguous()
	  * isContiguous()} but allows for gaps between Features.
	  */
	 public boolean isSingleSource() { 
		  try {
				if (sources.size() > 1) { return false; } 
				else { return true; }
		  } catch (NullPointerException e) {
				GloDBUtils.printError("No features.");
				return false;
		  }
	 }

	 /**
	  * Inverts the positions of each feature in the Track.  For
	  * example, if a feature had a start position of 10 and a stop
	  * position of 20 on a contig that was 100 positions long, then
	  * flipping the feature would result in a new Feature object
	  * with a start position of 80 and a stop position of 90.
	  * Flipping a Track will result in the creation of new Feature
	  * objects for each feature in the Track.
	  * @return Returns a new Track object in which the positions of
	  * all features are flipped.
	  * @XXX Not yet implemented.
	  */
	 public Track flip() { return null; }

	 /**
	  * Returns the minimum start position in the Track.  Will return
	  * '-1' if there are no features.  This will return '-1' if the
	  * Track contains features on different contigs (ie {@link
	  * #isSingleSource() isSingleSource()} returns 'false').
	  */
	 public int getMin() { 
		  if (numFeatures() == 0) return -1;

		  return ((Feature) features.first()).getMin();
	 }

	 /**
	  * Returns the maximum stop position in the Track.  Will return
	  * '-1' if there are no Features.  Note that the Features are
	  * sorted by min values, so it's not clear what the max Feature
	  * value is, except by testing each Feature. This will return '-1'
	  * if the Track contains Features on different contigs (ie {@link
	  * #isSingleSource() isSingleSource()} returns 'false').
	  */
	 public int getMax() { 
		  if (numFeatures() == 0) return -1;

		  Iterator i = features.iterator();
		  Feature feature = (Feature) i.next();
		  int max = feature.getMax();

		  while (i.hasNext()) {
				feature = (Feature) i.next();
				if (feature.getMax() > max) { max = feature.getMax(); }
		  }
		  return max; 
	 }

	 /**
	  * Returns the number of positions contained in the Track.
	  * Overlapping positions will only be counted once.
	  */
	 public int length() { 
		  // if no Features then 0 length
		  if (numFeatures() == 0) return -1;

		  int length = 0;

		  // loop through all features.  If sequences overlap or are
		  // contiguous, then merge them.
		  Iterator i = features.iterator();
		  Feature feature = (Feature) i.next();
		  int start = feature.getMin();
		  int stop = feature.getMax();
					 
		  while (i.hasNext()) {
				feature = (Feature) i.next();
					 
				// skip over gaps
				if (feature.getMin() > (stop + 1)) { 
					 length += (stop - start) + 1;
					 start = feature.getMin();
				}
					 
				if (stop < feature.getMax()) stop = feature.getMax();
		  }

		  length += (stop - start) + 1;

		  return length; 
	 }

	 /**
	  * Compares this object with the specified object for order.
	  * Returns a negative integer, zero, or a positive integer as this
	  * object is less than, equal to, or greater than the specified
	  * object.
	  * @XXX This is necessary for 'Comparable'.
	  * @XXX Not yet implemented.
	  */
	 public int compareTo(Object o) { 
		  // convert object to Track
		  Track track = (Track) o;

		  // who ever has min is less.
		  if (this.getMin() < track.getMin()) {
				return -1;
		  } else if (this.getMin() > track.getMin()) {
				return 1;
		  }

		  // min are equal, so whoever ends first is less.
		  if (this.getMax() < track.getMax()) {
				return -1;
		  } else if (this.getMax() > track.getMax()) {
				return 1;
		  }

		  // min and max are equal
		  return 0; 
	 }

	 /**
	  * Returns '-1' if this Track exists after the integer 'pos',
	  * returns '0' if 'pos' is contained in this Track, and '1' if
	  * 'pos' occurs after this Track.
	  * @XXX This assumes 'pos' is positive within this Track's
	  * Sequence boundaries.
	  * @XXX Not clear how to deal with Sequences in Tracks.
	  * @XXX For Tracks, this should test contains() for each
	  * Feature within the Track.
	  * @XXX Not yet implemented.
	  */
	 public int contains(int pos) { return 0; }

	 /**
	  * Returns 'true' if 'feature' exists in this Track.
	  */
	 public boolean contains(Feature feature) { 
		  return features.contains(feature);
	 }

	 /**
	  * Returns 'true' if this Track contains any Features on 'source'.
	  */
	 public boolean contains(String source) { 
		  if (sources == null) return false;
		  Set keys = sources.keySet();
		  return keys.contains(source);
	 }

	 /**
	  * Returns 'true' if the Feature 'featureB' overlaps at least one
	  * Feature in this Track.  
	  * @XXX Should use Sequences to limit the searches
	  */
	 public boolean overlaps(Feature featureB) { 
		  if ((numFeatures() == 0) || (featureB == null)) return false;

		  Feature featureA;
		  // only check Features that have the same source
		  String source = featureB.getSourceID();
		  Iterator iA = featuresBySource(source).iterator();

		  // get initial Features
		  if (iA.hasNext()) { 
				featureA = (Feature) iA.next(); 
		  } else { 
				return false; 
		  }
		  int minA = featureA.getMin();
		  int maxA = featureA.getMax();
		  int minB = featureB.getMin();
		  int maxB = featureB.getMax();
		  
		  while (true) {
				if (minB <= maxA) {
					 if (minA <= maxB) {
						  // B.min <= A.max and A.min <= B.max
						  return true; 
					 } else {
						  // featureB didn't match
						  return false; 
					 }
				} else {
					 if (iA.hasNext()) { 
						  // featureA is less than featureB, so increment featureA
						  featureA = (Feature) iA.next(); 
						  minA = featureA.getMin();
						  maxA = featureA.getMax();
						  continue;
					 } else { 
						  // have run out of Features in A
						  return false; 
					 }
				}
		  }
	 }

	 /**
	  * Returns 'true' if a Feature in trackB overlaps at least one
	  * Feature in this Track.
	  * @XXX Should use Sequences to limit the searches
	  */
	 public boolean overlaps(Track trackB) { 
		  if ((numFeatures() == 0) || (trackB == null)) return false;

		  for (Iterator sourceIt = sources.keySet().iterator(); sourceIt.hasNext();) {
				String source = (String) sourceIt.next();

				// get Features for this source
				TreeSet featuresA = (TreeSet) sources.get(source);

				// if sourcesB doesn't include 'source' then add all of
				// the Features on the current Sequence and continue.
				if (! trackB.contains(source)) continue;
					 
				// get Features for this source
				TreeSet featuresB = (TreeSet) trackB.featuresBySource(source);

				Feature featureA, featureB;
				Iterator iA = featuresA.iterator();
				Iterator iB = featuresB.iterator();

				// get initial Features
				if (iA.hasNext() && iB.hasNext()) { 
					 featureA = (Feature) iA.next(); 
					 featureB = (Feature) iB.next();
				} else { 
					 return false; 
				}
				int minA = featureA.getMin();
				int maxA = featureA.getMax();
				int minB = featureB.getMin();
				int maxB = featureB.getMax();
				
				while (true) {
					 if (minB <= maxA) {
						  if (minA <= maxB) {
								// B.min <= A.max and A.min <= B.max
								return true; 
						  } else {
								if (iB.hasNext()) { 
									 // featureB is less than featureA, so increment featureB
									 featureB = (Feature) iB.next(); 
									 minB = featureB.getMin();
									 maxB = featureB.getMax();
									 continue;
								} else { 
									 // have run out of Features in B
									 return false; 
								}
						  }
					 } else {
						  if (iA.hasNext()) { 
								// featureA is less than featureB, so increment featureA
								featureA = (Feature) iA.next(); 
								minA = featureA.getMin();
								maxA = featureA.getMax();
								continue;
						  } else { 
								// have run out of Features in A
								return false; 
						  }
					 }
				}
		  }
		  return false;
	 }

	 /**
	  * Create a shallow clone of the existing object (clone the
	  * structure but not the Objects).  This differs from clone() in
	  * that the clone will have the Features merged.
	  * @XXX Although public, this is not meant for use by the end user
	  * and does will not add the Track to the ObjectHandles Track
	  * pool.
	  */
	 public Track cloneMerged() {
		  Track track = new Track(false);
		  // use the parent Track's ID as the base for a random ID.
		  // The clone isn't added to the trackPool but still make
		  // sure it has a valid ID.
		  track.id = randomID("_" + this.id + "_");
		  track.attributes = this.attributes;
		  track.setFeatures(FeatureUtils.mergeContiguous(this));
		  return track;
	 }

	 /**
	  * Create a shallow clone of the existing object (clone the
	  * structure but not the Objects).  This clone will be added to
	  * ObjectHandles.trackPool.
	  */
	 public Object clone() {
		  return cloneTrack(true);
	 }

	 /**
	  * Create a shallow clone of the existing object (clone the
	  * structure but not the Objects).
	  * @XXX This could probably be done in a much more efficient way
	  * by cloning each field of a Track, rather than rebuilding the
	  * features. However, rebuilding the features allows us to use
	  * IGNORE_ATTRIBUTES to remove repeats.
	  */
	 public Object cloneTrack(boolean addToPool) {
		  Track track = new Track(addToPool);
		  // use the parent Track's ID as the base for a random ID.  The
		  // clone isn't added to the trackPool but still make sure it
		  // has a valid ID.
		  track.id = randomID("_" + this.id + "_");
		  track.attributes = this.attributes;
		  track.setFeatures(this.features);
		  return track;
	 }

	 /**
	  * Erases all Track information, except for the ID.
	  */
	 public void erase() {
		  setAttributes(null);
		  setFeatures(null);
	 }

	 /** 
	  * Uses 'base' to create a random ID string that doesn't already
	  * exist in the trackPool.
	  */
	 public static String randomID(String base) {
		  String id = base + Long.toString(Math.abs(random.nextLong()));
		  while (ObjectHandles.trackPool.containsKey(id)) {
				id = base + Long.toString(Math.abs(random.nextLong()));
		  }
		  return id;
	 }

	 /** Only returns Feature start/stop position information. */
	 public String toString() {
		  if (sources == null) return "";

		  String out = "";
		  for (Iterator i = (sources.keySet()).iterator(); i.hasNext();) {
				String sequence = (String) i.next();
				TreeSet features = featuresBySource(sequence);
				out += "\n Source (" + sequence + "): " + features.size() + " features";
				//				out += "\n " + features.size() + " features on source \"" + sequence + "\"\n";
		  }
		  out += "\n";
		  return out;
	 }
	
	 /** Only returns Feature start/stop position information. */
	 public String toStringMore() {
		  if (sources == null) return "";

		  String out = "";
		  for (Iterator i = (sources.keySet()).iterator(); i.hasNext();) {
				String sequence = (String) i.next();
				TreeSet features = featuresBySource(sequence);
				out += "\n Source (" + sequence + "):";
				for (Iterator j = features.iterator(); j.hasNext();) {
					 out += " " + ((Feature) j.next()).toStringMin();
				}
		  }
		  out += "\n";
		  return out;
	 }
	
	 /** Returns all description and Feature information. */
	 public String toStringFull() {
		  String out = "";

		  out += "\nID: " + id + "\n";

		  if (attributes == null) {
				out += "Attributes: none";
		  } else {
				out += "Attributes:\n  " + attributes;  // will convert itself to a string
		  }

		  /*
		  out += "\nSources: ";
		  for (Iterator i = (sources.keySet()).iterator(); i.hasNext();) {
				out += " " + ((Sequence) i.next()).getID();
		  }
		  //		  if (sources.isEmpty()) { out += "0"; }
		  //		  else { out += sources.size(); }
		  */

		  out += "\nFeatures:\n";
		  if (numFeatures() == 0) {
				out += "  null";
		  } else {
				for (Iterator i = features.iterator(); i.hasNext();) {
					 out += "  " + ((Feature) i.next()).toString();
				}
		  }
		  out += "\n";

		  return out;
	 }

	 /*
	 // allow the use of jython's built-in "+" function to add tracks
	 public Track __add__(Track other) {
		  Operation op = new Operation(other);
		  Track left = (Track) clone();
		  return Operator.fxn_AND(left, op);
	 }
   
	 // allow the use of jython's built-in "is" function to add tracks
	 public Track is_(Track other) {
		  Operation op = new Operation(other);
		  Track left = (Track) clone();
		  return Operator.fxn_sAND(left, op);
	 }
   
	 // allow the use of jython's built-in "-" function to add tracks
	 public Track __sub__(Track other) {
		  Operation op = new Operation(other);
		  Track left = (Track) clone();
		  return Operator.fxn_MINUS(left, op);
	 }
	 */

	 private static class FeatureMaxComparator implements Comparator {
		  /**
			* Compares this object with the specified object for order.
			* Returns a negative integer, zero, or a positive integer as
			* this object is less than, equal to, or greater than the
			* specified object.  If can't cast argument as an Feature,
			* then throws a java.lang.ClassCastException.  If different
			* sources, then sorts on the source ID.
			*/
		  public int compare(Object o1, Object o2) throws ClassCastException {
				// convert objects to Features
				Feature featureA;
				Feature featureB;
				try {
					 featureA = (Feature) o1;
					 featureB = (Feature) o2;				
				} catch (ClassCastException e) {
					 throw new ClassCastException("FeatureMaxComparator.compare() requires arguments of type Feature.");
				}
				
				int source = featureA.getSourceID().compareTo(featureB.getSourceID());
				if (source == 0) { // same source
					 // who ever has max is less.
					 if (featureA.getMax() < featureB.getMax()) {
						  return -1;
					 } else if (featureA.getMax() > featureB.getMax()) {
						  return 1;
					 }
					 
					 // max are equal.
					 if (featureA.getMin() < featureB.getMin()) {
						  return -1;
					 } else if (featureA.getMin() > featureB.getMin()) {
						  return 1;
					 }
					 
					 if (GloDBUtils.ignoreAttributes()) {
						  // don't use attributes to compare Features, so at
						  // this point the Features are the same
						  return 0;
					 } else {
						  // min and max are equal, so return the comparison of
						  // the hashCodes for each of the attributes.  If we
						  // don't then when 2 Features overlap only one will be
						  // included in the Track.
						  Integer hashA = new Integer((featureA.getAttributes()).hashCode());
						  Integer hashB = new Integer((featureB.getAttributes()).hashCode());
						  return hashA.compareTo(hashB);
					 }
				} else {
					 // different sources so sort by source
					 return source;
				}
		  }
	 }
   
} // Track.java

