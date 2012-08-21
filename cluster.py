# Copyright 2007, 2012 Stephen Fisher and Junhyong Kim, University of
# Pennsylvania.
#
# This file is part of Glo-DB.
# 
# Glo-DB is free software: you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Glo-DB is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Glo-DB. If not, see <http://www.gnu.org/licenses/>.

# cluster.py 
#
# @author  Stephen Fisher
# @version $Id: cluster.py,v 1.1.2.4 2007/03/01 21:17:32 fisher Exp $

# This module was created as an example of how to manipulate Tracks
# and Features at the command prompt.

# This module will merge all Features in the Track that are within
# maxSpace of each other.  New Features will be created to span the
# entire cluster.  Threshold sets the minimum number of Features
# necessary to be considered a cluster and thus included in the output
# set.  A new Track (named 'id') will be returned containing the
# clusters.  This will return 'null' if there is no match.

# @param id the name of the new Track
# @param maxSpace the maximum allowed space between Features in a
# cluster
# @param threshold the minimum number of Features needed in a
# cluster, for the cluster to be included in the output


# load standard definitions
from header import *
# load definitions from startup.py
from startup import *

# cluster is identical to Track.cluster(id, maxSpace, threshold)
def cluster(track, id, maxSpace, threshold=0):
	"""cluster(track, id, maxSpace, threshold):
	This will merge all Features in the Track that are within maxSpace
	of each other.  New Features will be created to span the entire
	cluster.  Threshold sets the minimum number of Features necessary
	to be considered a cluster and thus included in the output set.  A
	new Track (named 'id') will be returned containing the clusters.
	This will return 'null' if there is no match.
	"""
	trackObj = getTrack(track)
	if trackObj == None:
		printMsg("Track not found", ERROR)
		return None
	features = __compute(trackObj, maxSpace, threshold);

	# return null if no match
	if features.size() == 0: return None

	if len(id) == 0:
		# use the parent Track's ID as the base for a random ID.
		# The clone isn't added to the trackPool but still make
		# sure it has a valid ID.
		id = Track.randomID("_" + track + "_")

	newTrack = Track(id)
	newTrack.setAttributes(trackObj.attributes)
	newTrack.addFeatures(features)

#	print track.toStringMore()
#	print newTrack.toStringMore()
	return newTrack


# __compute is identical to FeatureUtils.cluster(track, maxSpace, threshold)
def __compute(track, maxSpace, threshold):
	clusters = java.util.TreeSet();

	# if Track is empty then there will be no matches so just
	# return an empty set
	if track.numFeatures() == 0: return clusters

	# make sure maxSpace is legal
	if maxSpace < 0:
		printMsg("Illegal \"maxSpace\" argument in FeatureUtils.cluster.", ERROR)
		return clusters
	
	# make sure maxSpace is legal
	if threshold < 0:
		printMsg("Illegal \"threshold\" argument in FeatureUtils.cluster.", ERROR)
		return clusters
	
	# Source Sets for Track
	sources = track.getSources()
	keys = sources.keySet()
	
	# test Features based on source
	for source in keys:
		# get Features for this source
		features = sources.get(source)
		
		i = features.iterator()
		fLast = i.next()
		# max here refers to the range for the beginning of the
		# next Feature
		max = fLast.getMax() + maxSpace
		
		group = java.util.TreeSet()
		group.add(fLast)
		# stores the minimum position for the group
		gMin = fLast.getMin()
		
		while i.hasNext():
			 fCurrent = i.next()
		
			 if fCurrent.getMin() <= max:
				 # within cluster spacing
				 group.add(fCurrent)
		
				 if fCurrent.getMax() > fLast.getMax():
					 # new maximum
					 fLast = fCurrent
					 max = fLast.getMax() + maxSpace
			 else:
				 if group.size() >= threshold:
					 # enough Features in cluster to warrent
					 # inclusion in output
		
					 if group.size() > 1:
						 # more than one Feature in cluster to
						 # make new Feature to span the set of
						 # Features
						 clusters.add(ExactFeature(gMin, fLast.getMax(), 
															ObjectHandles.getSequence(source)))
					 else:
						 # only one Feature in group so just add
						 # that Feature
						 clusters.add(group.first())
	
				 fLast = fCurrent
				 max = fLast.getMax() + maxSpace
	
				 group.clear()
				 group.add(fLast)
				 gMin = fLast.getMin()
	
		# add last group, if present
		if group.size() >= threshold:
			# enough Features in cluster to warrent inclusion in
			# output
			if group.size() > 1:
				# more than one Feature in cluster so make new
				# Feature to span the set of Features
				clusters.add(ExactFeature(gMin, fLast.getMax(), 
												  ObjectHandles.getSequence(source)))
			else:
				# only one Feature in group so just add that
				# Feature
				clusters.add(group.first())

	return clusters
	
