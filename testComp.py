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
#
# testComp.py 
#
# @author  Stephen Fisher
# @version $Id: testComp.py,v 1.1.2.16 2007/03/01 21:17:32 fisher Exp $

# This module was created to test the computational algorithms in
# GLOB.  Each of these functions will return a new Track which will
# not be included in the GLOB trackPool.  These functions are only
# meant to test the correctness of the built-in algorithms (see
# parser.Operations.java), and thus are often very inefficiently and
# slow but should also be accurate.

# AND: done
# sAND: done
# &&: done
# OR: done
# ||: done
# MINUS: done
# sMINUS: done
# -: done
# POS: done
# !: done

# load standard definitions
from header import *
# load required defs from startup.py
from startup import *

timing = 0

def compare(t1, t2):
	"""test(t1, t2)
	Compares the feature sets in two tracks
	"""
	if t1.getFeatures() == t2.getFeatures(): return "CORRECT"
	else:
		if (t1 == None) or (t2 == None): return "** FAILED **"
		if t1.numFeatures() != t2.numFeatures(): return "** FAILED **"
		
		# now we need to check the feature positions since we don't try
		# to preserve any feature attributes here
		i1 = t1.featureIterator()
		i2 = t2.featureIterator()
		while i1.hasNext(): # only need to test i1 because same num in i2
			f1 = i1.next()
			f2 = i2.next()
			if (f1.getStart() != f2.getStart()) or (f1.getStop() != f2.getStop()):
				return "** FAILED **"
		return "CORRECT"

# test("t1 sAND t2", _sAND(t1, t2))
def test(expr, expected):
	"""fullTest(expr, expected):
	Compares the expression 'expr' (ex: 't1 and t2') to the track
	'expected'.
	"""
	msg = "TESTING \"" + expr + "\": "
	expr = "__testComp = " + expr

	# set verbose to 1 to turn off warnings
	verbose_save = GloDBUtils.getVerbose()
	setVerbose(1)

	if timing: print "starting builtin: ", time()
	actual = ParserUtils.compute(expr)
	if timing: print "stopping builtin: ", time()
	if actual == None: printMsg(msg + "Invalid Expression")
	else: printMsg(msg + compare(actual, expected))

	# reset verbosity to original value
	setVerbose(verbose_save)

###############################################################
# AND
def _AND(left, right):
	"""_AND(left, right):
	"""
	if timing: print "starting AND: ", time()

	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if (left.numFeatures() == 0) or (right.numFeatures() == 0):	return out

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in both Tracks then skip
		if not right.contains(source): continue
		
		# step through left and compare all features to right
		for lFeature in left.featuresBySource(source):
			for rFeature in right.featuresBySource(source):
				if lFeature.overlaps(rFeature):
					out.addFeature(lFeature)
					out.addFeature(rFeature)

	if timing: print "stopping AND: ", time()
	return out

###############################################################
# sAND
def _sAND(left, right):
	"""_sAND(left, right):
	"""
	if timing: print "starting sAND: ", time()

	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if (left.numFeatures() == 0) or (right.numFeatures() == 0):	return out

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in both Tracks then skip
		if not right.contains(source): continue

		# found a common source so process Features
		for lFeature in left.featuresBySource(source):
			if right.contains(lFeature): out.addFeature(lFeature)
			
	if timing: print "stopping sAND: ", time()
	return out

###############################################################
# &&
def _bAND(left, right):
	"""_bAND(left, right):
	"""
	if timing: print "starting bAND: ", time()

	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if (left.numFeatures() == 0) or (right.numFeatures() == 0):	return out

	# simplify the computation by merging all contiguous Features
	# within each Track, but don't merge the original Tracks
	left = left.cloneMerged()
	right = right.cloneMerged()

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in both Tracks then skip
		if source not in right.getSourceSet(): continue
		
		# step through left and compare all features to right
		for lFeature in left.featuresBySource(source):
			for rFeature in right.featuresBySource(source):
				if lFeature.overlaps(rFeature):
					out.addFeature(lFeature.overlap(rFeature))

	out.mergeContiguous()
	if timing: print "stopping bAND: ", time()
	return out

###############################################################
# OR
def _OR(left, right):
	"""_OR(left, right):
	"""
	if timing: print "starting OR: ", time()
	# create output Track that isn't in trackPool
	out = left.clone()
	out.addFeatures(right.getFeatures())

	if timing: print "stopping OR: ", time()
	return out

###############################################################
# ||
def _bOR(left, right):
	"""_bOR(left, right):
	"""
	if timing: print "starting bOR: ", time()

	# create output Track that isn't in trackPool.  Merge each Track
	# prior to adding the Track because in many cases this will
	# significantly reduce the amount of Features that need to be
	# processed.
	out = left.cloneMerged()
	out.addFeatures(FeatureUtils.mergeContiguous(right))
	out.mergeContiguous()
	if timing: print "stopping bOR: ", time()
	return out

###############################################################
# MINUS
def _MINUS(left, right):
	"""_MINUS(left, right):
	"""
	if timing: print "starting MINUS: ", time()
	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if (left.numFeatures() == 0): return out
	if (right.numFeatures() == 0):
		out.addFeatures(left.getFeatures())
		return out							

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in "right" Track, then add all Features from the
		# Source
		if not right.contains(source):
			out.addFeatures(left.featuresBySource(source))
			continue

		# found a common source so process Features
		for lFeature in left.featuresBySource(source):
			if not right.overlaps(lFeature): out.addFeature(lFeature)
			#			for rFeature in right.featuresBySource(source):
			#				if not lFeature.overlaps(rFeature): out.addFeature(lFeature)

	if timing: print "stopping MINUS: ", time()
	return out


###############################################################
# sMINUS
def _sMINUS(left, right):
	"""_sMINUS(left, right):
	"""
	if timing: print "starting sMINUS: ", time()
	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if left.numFeatures() == 0: return out
	if right.numFeatures() == 0:
		out.addFeatures(left.getFeatures())
		return out							

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in "right" Track, then add all Features from the
		# Source
		if not right.contains(source):
			out.addFeatures(left.featuresBySource(source))
			continue

		# found a common source so process Features
		for lFeature in left.featuresBySource(source):
			if not right.contains(lFeature): out.addFeature(lFeature)
			
	if timing: print "stopping sMINUS: ", time()
	return out


###############################################################
# -
def _bMINUS(left, right):
	"""_bMINUS(left, right):
	"""
	if timing: print "starting bMINUS: ", time()
	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if left.numFeatures() == 0: return out
	if right.numFeatures() == 0:
		out.addFeatures(left.getFeatures())
		return out							

	# simplify the computation by merging all contiguous Features
	# within each Track, but don't merge the original Tracks
	left = left.cloneMerged()
	right = right.cloneMerged()

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in both Tracks then skip
		if not right.contains(source): continue
		
		# step through left and compare all features to right
		for lFeature in left.featuresBySource(source):
			# To start with we assume there is no overlap, and thus we
			# have the entire length of lFeature. As we go, these
			# boundaries will be reduced if they overlap with rFeatures.
			lMin = lFeature.getMin()
			lMax = lFeature.getMax()

			for rFeature in right.featuresBySource(source):
				if not lFeature.overlaps(rFeature):
					continue
				else:
					rMin = rFeature.getMin()
					rMax = rFeature.getMax()

					# we know the Features overlap so we don't need to test for
					# that here
					if lMin >= rMin:
						if lMax <= rMax:
							# overlap from lMin to lMax, so increment lFeature
							lMax = -1
							break
						else:
							# overlap from lMin to rMax, so remove everything
							# prior to rMax
							lMin = rMax + 1
					else:  # rMin > lMin
						if rMax < lMax:
							# overlap from rMin to rMax, so add the section
							# of lFeature that is prior to rFeature and
							# continue with the section following rFeature.
							out.addFeature(ExactFeature(lMin, rMin-1, lFeature.getSource()))
							lMin = rMax + 1
						else:
							# overlap from rMin to lMax, so add section prior
							# to rFeature and then increment lFeature
							out.addFeature(ExactFeature(lMin, rMin-1, lFeature.getSource()))
							lMax = -1
							break

			if lMax > -1: out.addFeature(ExactFeature(lMin, lMax, lFeature.getSource()))

	out.mergeContiguous()
	if timing: print "stopping bMINUS: ", time()
	return out

###############################################################
# POS
def _POS(left, right, pMin, pMax):
	"""_POS(left, right, pMin, pMax):
	"""
	if timing: print "starting POS: ", time()
	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have valid Tracks
	if (left.numFeatures() == 0) or (right.numFeatures() == 0):	return out

	# process Features by source Sequence	
	for source in left.getSourceSet():
		# if source not in both Tracks then skip
		if not right.contains(source): continue
		
		# step through left and compare all features to right
		for lFeature in left.featuresBySource(source):
			lMin = lFeature.getMax() + pMin
			lMax = lFeature.getMax() + pMax

			for rFeature in right.featuresBySource(source):
				if (lMin <= rFeature.getMin()) and (rFeature.getMin() <= lMax):
					# left.min <= rigth.min <= left.max
					out.addFeature(lFeature)
					out.addFeature(rFeature)

	if timing: print "stopping POS: ", time()
	return out


###############################################################
# bNOT
def _bNOT(left):
	"""_bNOT(left):
	"""
	if timing: print "starting bNOT: ", time()
	# create output Track that isn't in trackPool
	out = Track(0)

	# make sure we have a valid Track
	if (left.numFeatures() == 0): return out

	# process Features by source Sequence	
	for source in left.getSourceSet():
		sequence = getSequence(source)
		sMin = sequence.getMin()
		sMax = sequence.getMax() - 1
		if sMax == -1:
			printMsg("No sequence data", ERROR)
			return Track(0)

		# step through left and invert
		for lFeature in left.featuresBySource(source):
			lMin = lFeature.getMin() - 1
			lMax = lFeature.getMax() + 1
			if sMin <= lMin: 
				out.addFeature(ExactFeature(sMin, lMin, sequence))
			if lMax > sMin:
				sMin = lMax
				if lMax > sMax + 1:
					printMsg("Feature position exceeds sequence length", ERROR)
					return Track(0)
					

		if sMin <= sMax:
			out.addFeature(ExactFeature(sMin, sMax, sequence))
							
	if timing: print "stopping bNOT: ", time()
	return out

