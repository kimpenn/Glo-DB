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
# test.py 
#
# @author  Stephen Fisher
# @version $Id: test.py,v 1.37.2.39 2007/03/01 21:17:32 fisher Exp $

# Routines and sequence/track/feature objects to be used for testing
# the program.

# load standard definitions
from header import *
# load required defs from startup.py
from startup import *


# ----------------------------------------------------------------------------
# Create sequence 'Y' that is 30 bp long, and tracks 'f1' and 'f2'.
# Similar tracks (t1 and t2) and sequence (T) are also created
# specifically to test the query (see setTests() and binTests()
# below).

#             aa                            bb
# sequence Y: |------------------------------|
#             |012345678901234567890123456789|
# track    f1:  r-s    t-------u v---w  x-y
# track    f2:  a-b   c-d e-f g---h i-j
#
# Y:   (aa, bb)
#      ( 0, 29)
# f1: {(r,s), (t, u), ( v, w), ( x, y)}
#     {(1,3), (8,16), (18,22), (25,27)}
# f2: {(a,b), (c, d), ( e, f), ( g, h), ( i, j)}
#     {(1,3), (7, 9), (11,13), (15,19), (21,23)}

sy = Sequence("Y")
sy.addAttribute("ID", "Y")
sy.addAttribute("Notes", "Used for testing")
#sy.setData("THIS_IS_30_CHAR_LONG0123456END")
sy.setData("THIS_IS_60_CHAR_LONG0123456789012345678901234567890123456END")

f1 = Track("f1")
f1.addAttribute("ID", "f1")
f1.addAttribute("strand", "+")
rs = ExactFeature(1, 3, sy)
f1.addFeature(rs)
tu = ExactFeature(8, 23, sy)
f1.addFeature(tu)
vw = ExactFeature(18, 22, sy)
f1.addFeature(vw)
xy = ExactFeature(25, 27, sy)
f1.addFeature(xy)

f2 = Track("f2")
f2.addAttribute("ID", "f2")
f2.addAttribute("strand", "+")
ab = ExactFeature(1, 3, sy)
f2.addFeature(ab)
cd = ExactFeature(7, 9, sy)
f2.addFeature(cd)
ef = ExactFeature(11, 13, sy)
f2.addFeature(ef)
gh = ExactFeature(15, 19, sy)
f2.addFeature(gh)
ij = ExactFeature(21, 23, sy)
f2.addFeature(ij)

f1.addFeature(ExactFeature(0, 2, sy))
f1.addFeature(ExactFeature(3, 5, sy))
f1.addFeature(ExactFeature(30, 50, sy))
f1.addFeature(ExactFeature(33, 33, sy))
f1.addFeature(ExactFeature(40, 59, sy))
f2.addFeature(ExactFeature(2, 2, sy))
f2.addFeature(ExactFeature(33, 33, sy))

a1 = Track("a1")
b1 = Track("b1")
a1.addFeature(ExactFeature(63503,63513,sy))
a1.addFeature(ExactFeature(63552,63565,sy))
a1.addFeature(ExactFeature(63552,63566,sy))
a1.addFeature(ExactFeature(63552,63566,sy))
a1.addFeature(ExactFeature(63552,63566,sy))
a1.addFeature(ExactFeature(63554,63564,sy))
a1.addFeature(ExactFeature(63695,63705,sy))
b1.addFeature(ExactFeature(48734,49868,sy))
b1.addFeature(ExactFeature(48734,49868,sy))
b1.addFeature(ExactFeature(51008,51190,sy))
b1.addFeature(ExactFeature(51008,70436,sy))
b1.addFeature(ExactFeature(51008,70437,sy))
b1.addFeature(ExactFeature(51177,68570,sy))
b1.addFeature(ExactFeature(51782,51969,sy))
b1.addFeature(ExactFeature(52042,52168,sy))
b1.addFeature(ExactFeature(59919,60547,sy))
b1.addFeature(ExactFeature(59919,68502,sy))
b1.addFeature(ExactFeature(59919,70436,sy))
b1.addFeature(ExactFeature(65021,65066,sy))
b1.addFeature(ExactFeature(65021,70437,sy))
b1.addFeature(ExactFeature(65152,65314,sy))
b1.addFeature(ExactFeature(65300,68570,sy))
b1.addFeature(ExactFeature(66007,66186,sy))
b1.addFeature(ExactFeature(66255,66372,sy))
b1.addFeature(ExactFeature(66255,66372,sy))
b1.addFeature(ExactFeature(66255,66372,sy))
b1.addFeature(ExactFeature(66501,67438,sy))
b1.addFeature(ExactFeature(66501,67438,sy))
b1.addFeature(ExactFeature(66501,67438,sy))
b1.addFeature(ExactFeature(67517,67607,sy))
b1.addFeature(ExactFeature(67517,67607,sy))
b1.addFeature(ExactFeature(67517,67607,sy))
b1.addFeature(ExactFeature(67722,67911,sy))
b1.addFeature(ExactFeature(67722,67911,sy))
b1.addFeature(ExactFeature(67722,67911,sy))
b1.addFeature(ExactFeature(67977,68395,sy))
b1.addFeature(ExactFeature(67977,68395,sy))
b1.addFeature(ExactFeature(67977,70436,sy))
b1.addFeature(ExactFeature(68476,70436,sy))
b1.addFeature(ExactFeature(68476,70437,sy))
b1.addFeature(ExactFeature(73638,74389,sy))
b1.addFeature(ExactFeature(73638,78496,sy))


# ----------------------------------------------------------------------------
# User-defined functions

def randomTrack(name, numFeat, min, max, seqID):
	"""randomTrack(name, numFeat, min, max, seqID):
	Create a track with 'numFeat' number of features that range from
	'min', to 'max'.  The features will be placed on 'seqID'.
	"""
	seq = getSequence(seqID)
	if seq == None:
		printMsg("Sequence not found", ERROR)
		return None
	f = Track(name)
	r = Random()
	tot = max - min + 1
	for i in range(0, numFeat):
		a = r.nextInt(tot) + min
		b = r.nextInt(tot) + min
		if a < b:
			feat = ExactFeature(a, b, seq)
		else:
			feat = ExactFeature(b, a, seq)
		f.addFeature(feat)
	return f

def randomTrack2(name, numFeat, min, max, seqID):
	"""randomTrack(name, numFeat, min, max, seqID):
	Create a track with 'numFeat' number of features that range from
	'min', to 'max'.  The features will be placed on 'seqID'.
	"""
	f = Track(name)
	seq = ObjectHandles.getSequence(seqID)
	r = Random()

	tot = max - min + 1
	aMax = int((tot * 0.9) + min)
	bMin = int((tot * 0.1) + min)
	bMax = max - bMin
	for i in range(0, numFeat):
		a = r.nextInt(tot) + min
		b = r.nextInt(tot) + min
		while b < a:
			b = r.nextInt(tot) + min

		# randInt() goes from 0 (inc) to max (exc).  however, features
		# go from 1 (inc) to max (inc).
		feat = ExactFeature(a, b, seq)
		f.addFeature(feat)
	return f

def buildTreeSet(list):
	"""buildTreeSet(list):
	Converts a Python 'list' into a java TreeSet.
	"""
	tSet = java.util.TreeSet()
	for i in list:
		tSet.add(i)
	return tSet

def testMatch(track, expected):
	"""test(track, expected):
	Compares the expected match() output to the actual match()
	output.  This should be used to test the parsing code.  'expected'
	should be a java.util.TreeSet containing the Features that are
	expected to exist in the Track 'track'.
	"""
	if len(expected) == 0:
		if track.numFeatures() == 0: return "CORRECT"
		else: return "** FAILED **"
	elif track.numFeatures() == 0:
		return "** FAILED **"

	tSet = buildTreeSet(expected)
	if tSet == track.features: return "CORRECT"
	else: return "** FAILED **"

def fullTest(expr, expected):
	"""fullTest(expr, expected):
	"""
	actual = ParserUtils.compute(expr)
	if actual == None: 
		printMsg("TESTING \"" + expr + "\": Invalid Expression")
	else:
		printMsg("TESTING \"" + expr + "\": " + testMatch(actual, expected))
"""
	actual = Track(0)
	p = Parser(expr)
	try:
		ops = p.run()
	except:
		printMsg("TESTING \"" + expr + "\": Invalid Expression")
		return
	actual = ParserUtils().solveOps(ops, expr)
	printMsg("TESTING \"" + expr + "\": " + testMatch(actual, expected))
"""

def sops(ops):
	"""sops(ops):
	Solves the finite state automata created by the regular expression
   parser.
	"""
	return ParserUtils().solveOps(ops)

def parse(expr):
	"""parse(expr):
	"""
	p = Parser(expr)
	try:
		ops = p.run()
	except:
		printMsg("Invalid expression to be solved", ERROR)
		return []
	printMsg(p.toString())
	return ops


# ----------------------------------------------------------------------------
# SET AND BINARY TESTS
#
#             aa                             bb
# sequence T: |------------------------------|
#             |012345678901234567890123456789|
# track    t1:  r-s    t-------u v---w  x-y          
# track    t2:  a-b   c-d e-f g---h i-j
# track    t3:         t--------------j
#                             g-----i
#                                 h-i
# track    t4:        c-d
#                       d------u        x-y
#
# T:   (aa, bb)
#      ( 0, 29)
# t1: {(r,s), (t, u), ( v, w), ( x, y)}
#     {(1,3), (8,16), (18,22), (25,27)}
# t2: {(a,b), (c, d), ( e, f), ( g, h), ( i, j)}
#     {(1,3), (7, 9), (11,13), (15,19), (21,23)}
# t3: {(t, j), ( g, i), ( h, i)}
#     {(8,23), (15,21), (19,21)}
# t4: {(c, d), (d, u), ( x, y)}
#     {(7, 9), (9,16), (25,27)}


st = Sequence("T")
st.addAttribute("ID", "T")
st.addAttribute("Notes", "Used for testing set/binary queries")
st.setData("THIS_IS_60_CHAR_LONG0123456END")
# st.setData("THIS_IS_60_CHAR_LONG0123456789012345678901234567890123456END")

t_0_0   = ExactFeature( 0,  0, st)
t_0_1   = ExactFeature( 0,  1, st)
t_0_6   = ExactFeature( 0,  6, st)
t_0_7   = ExactFeature( 0,  7, st)
t_0_9   = ExactFeature( 0,  9, st)
t_0_29  = ExactFeature( 0, 29, st)
t_1_2   = ExactFeature( 1,  2, st)
t_1_3   = ExactFeature( 1,  3, st)
t_3_3   = ExactFeature( 3,  3, st)
t_3_7   = ExactFeature( 3,  7, st)
t_3_8   = ExactFeature( 3,  8, st)
t_4_6   = ExactFeature( 4,  6, st)
t_4_7   = ExactFeature( 4,  7, st)
t_7_7   = ExactFeature( 7,  7, st)
t_7_8   = ExactFeature( 7,  8, st)
t_7_9   = ExactFeature( 7,  9, st)
t_7_23  = ExactFeature( 7, 23, st)
t_8_8   = ExactFeature( 8,  8, st)
t_8_9   = ExactFeature( 8,  9, st)
t_8_16  = ExactFeature( 8, 16, st)
t_8_23  = ExactFeature( 8, 23, st)
t_9_11  = ExactFeature( 9, 11, st)
t_9_16  = ExactFeature( 9, 16, st)
t_10_10 = ExactFeature(10, 10, st)
t_11_11 = ExactFeature(11, 11, st)
t_11_13 = ExactFeature(11, 13, st)
t_13_15 = ExactFeature(13, 15, st)
t_14_14 = ExactFeature(14, 14, st)
t_15_15 = ExactFeature(15, 15, st)
t_15_16 = ExactFeature(15, 16, st)
t_15_19 = ExactFeature(15, 19, st)
t_15_21 = ExactFeature(15, 21, st)
t_15_23 = ExactFeature(15, 23, st)
t_16_16 = ExactFeature(16, 16, st)
t_16_18 = ExactFeature(16, 18, st)
t_17_17 = ExactFeature(17, 17, st)
t_17_19 = ExactFeature(17, 19, st)
t_17_23 = ExactFeature(17, 23, st)
t_17_24 = ExactFeature(17, 24, st)
t_18_18 = ExactFeature(18, 18, st)
t_18_19 = ExactFeature(18, 19, st)
t_18_22 = ExactFeature(18, 22, st)
t_19_19 = ExactFeature(19, 19, st)
t_19_21 = ExactFeature(19, 21, st)
t_20_20 = ExactFeature(20, 20, st)
t_21_21 = ExactFeature(21, 21, st)
t_21_22 = ExactFeature(21, 22, st)
t_21_23 = ExactFeature(21, 23, st)
t_21_24 = ExactFeature(21, 24, st)
t_22_22 = ExactFeature(22, 22, st)
t_22_23 = ExactFeature(22, 23, st)
t_22_25 = ExactFeature(22, 25, st)
t_22_29 = ExactFeature(22, 29, st)
t_23_23 = ExactFeature(23, 23, st)
t_23_24 = ExactFeature(23, 24, st)
t_23_25 = ExactFeature(23, 25, st)
t_23_29 = ExactFeature(23, 29, st)
t_24_24 = ExactFeature(24, 24, st)
t_24_25 = ExactFeature(24, 25, st)
t_24_29 = ExactFeature(24, 29, st)
t_25_27 = ExactFeature(25, 27, st)
t_26_29 = ExactFeature(26, 29, st)
t_27_29 = ExactFeature(27, 29, st)
t_28_29 = ExactFeature(28, 29, st)

t1 = Track("t1")
t1.addFeature(t_1_3)
t1.addFeature(t_8_16)
t1.addFeature(t_18_22)
t1.addFeature(t_25_27)

t2 = Track("t2")
t2.addFeature(t_1_3)
t2.addFeature(t_7_9)
t2.addFeature(t_11_13)
t2.addFeature(t_15_19)
t2.addFeature(t_21_23)

t3 = Track("t3")
t3.addFeature(t_8_23)
t3.addFeature(t_15_21)
t3.addFeature(t_19_21)

t4 = Track("t4")
t4.addFeature(t_7_9)
t4.addFeature(t_9_16)
t4.addFeature(t_25_27)

# -------------------------------------------
# These treat Tracks as sets of intervals:

def setTests():
	"""setTests():
	"""
	# set verbose to 1 to turn off warnings
	verbose_save = GloDBUtils.getVerbose()
	if verbose_save > 1: setVerbose(1)

	# t1 AND t2:		{(r,s), (t,u), (v,w), (c,d), (e,f), (g,h), (i,j)}
	fullTest("_t = t1 AND t2", [t_1_3, t_8_16, t_18_22, t_7_9, t_11_13, t_15_19, t_21_23])
	# t1 OR t2:			{(r,s), (t,u), (v,w), (x,y), (c,d), (e,f), (g,h), (i,j)}
	fullTest("_t = t1 OR t2", [t_1_3, t_8_16, t_18_22, t_25_27, t_7_9, t_11_13, t_15_19, t_21_23])
	# t1 MINUS t2:		{(x,y)}
	fullTest("_t = t1 MINUS t2", [t_25_27])
	# t2 MINUS t1:		{}
	fullTest("_t = t2 MINUS t1", [])
	# t1 sAND t2:		{(r,s)}
	fullTest("_t = t1 sAND t2", [t_1_3])
	# t1 sMINUS t2:	{(t,u), (v,w), (x,y)}
	fullTest("_t = t1 sMINUS t2", [t_8_16, t_18_22, t_25_27])
	# t2 sMINUS t1:	{(c,d), (e,f), (g,h), (i,j)}
	fullTest("_t = t2 sMINUS t1", [t_7_9, t_11_13, t_15_19, t_21_23])
	# t1 sMINUS t2 AND t2:	{(t,u), (v,w), (c,d), (e,f), (g,h), (i,j)}
	fullTest("_t = t1 sMINUS t2 AND t2", [t_8_16, t_18_22, t_7_9, t_11_13, t_15_19, t_21_23])
	# t1 sMINUS (t2 AND t2):	{(t,u), (v,w), (x,y)}
	fullTest("_t = t1 sMINUS ( t2 AND t2 )", [t_8_16, t_18_22, t_25_27])
	# (t1 MINUS t2) OR (t2 MINUS t1):	{(x,y)}
	fullTest("_t = (t1 MINUS t2) OR (t2 MINUS t1)", [t_25_27])
	# (t1 sMINUS t2) OR (t2 sMINUS t1):	{(t,u), (v,w), (x,y), (c,d), (e,f), (g,h), (i,j)}
	fullTest("_t = (t1 sMINUS t2) OR (t2 sMINUS t1)", [t_8_16, t_18_22, t_25_27, t_7_9, t_11_13, t_15_19, t_21_23])
	# t1 POS{0} t2:	{}
	fullTest("_t = t1 POS{0} t2", [])
	# t1 POS{5} t2:	{(t,u), (i,j)}
	fullTest("_t = t1 POS{5} t2", [t_8_16, t_21_23])
	# t2 POS{5} t1:	{(a,b), (t,u), (e,f), (v,w)}
	fullTest("_t = t2 POS{5} t1", [t_1_3, t_8_16, t_11_13, t_18_22])
	# t1 POS{-5} t2:	{(t,u), (e,f)}
	fullTest("_t = t1 POS{-5} t2", [t_8_16, t_11_13])
	# t1 POS{3,6} t2:	{(r,s), (c,d), (t,u), (i,j)}
	fullTest("_t = t1 POS{3,6} t2", [t_1_3, t_7_9, t_8_16, t_21_23])
	# t1 POS{-5,-1} t2:	{(r,s), (t,u), (e,f), (g,h), (v,w), (i,j)}
	fullTest("_t = t1 POS{-5,-1} t2", [t_1_3, t_8_16, t_11_13, t_15_19, t_18_22, t_21_23])
	# t3 AND t4:		{(t,j), (g,i), (c,d), (d,u)}
	fullTest("_t = t3 AND t4", [t_8_23, t_15_21, t_7_9, t_9_16])
	# t3 OR t4:			{(t,j), (g,i), (h,i), (c,d), (d,u), (x,y)}
	fullTest("_t = t3 OR t4", [t_8_23, t_15_21, t_19_21, t_7_9, t_9_16, t_25_27])
	# t3 MINUS t4:		{(h,i)}
	fullTest("_t = t3 MINUS t4", [t_19_21])
	# t4 MINUS t3:		{(x,y)}
	fullTest("_t = t4 MINUS t3", [t_25_27])
	# t3 sAND t4:		{}
	fullTest("_t = t3 sAND t4", [])
	# t3 sMINUS t4:	{(t,j), (g,i), (h,i)}
	fullTest("_t = t3 sMINUS t4", [t_8_23, t_15_21, t_19_21])
	# t4 sMINUS t3:	{(c,d), (d,u), (x,y)}
	fullTest("_t = t4 sMINUS t3", [t_7_9, t_9_16, t_25_27])

	# test comparing built-in and external scripts
	testComp.test("t1 AND t2", testComp._AND(t1, t2))
	testComp.test("t1 sAND t2", testComp._sAND(t1, t2))
	testComp.test("t1 OR t2", testComp._OR(t1, t2))
	testComp.test("t1 MINUS t2", testComp._MINUS(t1, t2))
	testComp.test("t2 MINUS t1", testComp._MINUS(t2, t1))
	testComp.test("t1 sMINUS t2", testComp._sMINUS(t1, t2))
	testComp.test("t2 sMINUS t1", testComp._sMINUS(t2, t1))
	testComp.test("t1 POS{0} t2", testComp._POS(t1, t2, 0, 0))
	testComp.test("t1 POS{5} t2", testComp._POS(t1, t2, 5, 5))
	testComp.test("t2 POS{5} t1", testComp._POS(t2, t1, 5, 5))
	testComp.test("t1 POS{-5} t2", testComp._POS(t1, t2, -5, -5))
	testComp.test("t1 POS{3,6} t2", testComp._POS(t1, t2, 3, 6))
	testComp.test("t1 POS{-5,-1} t2", testComp._POS(t1, t2, -5, -1))
	testComp.test("t1 POS{-5,1} t2", testComp._POS(t1, t2, -5, 1))
	testComp.test("t1 POS{-3,5} t2", testComp._POS(t1, t2, -3, 5))
	testComp.test("f1 AND f2", testComp._AND(f1, f2))
	testComp.test("f1 sAND f2", testComp._sAND(f1, f2))
	testComp.test("f1 OR f2", testComp._OR(f1, f2))
	testComp.test("f1 MINUS f2", testComp._MINUS(f1, f2))
	testComp.test("f2 MINUS f1", testComp._MINUS(f2, f1))
	testComp.test("f1 sMINUS f2", testComp._sMINUS(f1, f2))
	testComp.test("f2 sMINUS f1", testComp._sMINUS(f2, f1))
	testComp.test("f1 POS{0} f2", testComp._POS(f1, f2, 0, 0))
	testComp.test("f1 POS{5} f2", testComp._POS(f1, f2, 5, 5))
	testComp.test("f2 POS{5} f1", testComp._POS(f2, f1, 5, 5))
	testComp.test("f1 POS{-5} f2", testComp._POS(f1, f2, -5, -5))
	testComp.test("f1 POS{3,6} f2", testComp._POS(f1, f2, 3, 6))
	testComp.test("f1 POS{-5,-1} f2", testComp._POS(f1, f2, -5, -1))
	testComp.test("f1 POS{-5,1} f2", testComp._POS(f1, f2, -5, 1))
	testComp.test("f1 POS{-3,5} f2", testComp._POS(f1, f2, -3, 5))
	
	# reset verbosity to original value
	setVerbose(verbose_save)
	

# --------------------------------------------------------
# These treat Tracks as masks over the Sequence string:

def binTests():
	"""binTests():
	"""
	# set verbose to 1 to turn off warnings
	verbose_save = GloDBUtils.getVerbose()
	if verbose_save > 1: setVerbose(1)

	# t1 && t2:			{(1,3), (8,9), (11,13), (15,16), (18,19), (21,22)}
	fullTest("_t = t1 && t2", [t_1_3, t_8_9, t_11_13, t_15_16, t_18_19, t_21_22])
	# t1 || t2:			{(1,3), (7,23), (25,27)}
	fullTest("_t = t1 || t2", [t_1_3, t_7_23, t_25_27])
	# t1 - t2:			{(10,10), (14,14), (20,20), (25,27)}
	fullTest("_t = t1 - t2", [t_10_10, t_14_14, t_20_20, t_25_27])
	# t2 - t1:			{(7,7), (17,17), (23,23)}
	fullTest("_t = t2 - t1", [t_7_7, t_17_17, t_23_23])
	# ! t1:				{(0,0), (4,7), (17,17), (23,24), (28,29)}
	fullTest("_t = ! t1", [t_0_0, t_4_7, t_17_17, t_23_24, t_28_29])
	# ! t2:				{(0,0), (4,6), (10,10), (14,14), (20,20), (24,29)}
	fullTest("_t = ! t2", [t_0_0, t_4_6, t_10_10, t_14_14, t_20_20, t_24_29])
	# ! t1 && t2:		{(7,7), (17,17), (23,23)}
	fullTest("_t = ! t1 && t2", [t_7_7, t_17_17, t_23_23])
	# t1 && ! t2:		{(10,10), (14,14), (20,20), (25,27)}
	fullTest("_t = t1 && ! t2", [t_10_10, t_14_14, t_20_20, t_25_27])
	# ! (t1 && t2):	{(0,0), (4,7), (10,10), (14,14), (17,17), (20,20), (23,29)}
	fullTest("_t = ! ( t1 && t2 )", [t_0_0, t_4_7, t_10_10, t_14_14, t_17_17, t_20_20, t_23_29])
	# ! (t1 || t2):	{(0,0), (4,6), (24,24), (28,29)}
	fullTest("_t = ! ( t1 || t2 )", [t_0_0, t_4_6, t_24_24, t_28_29])
	# ! (t1 - t2 && t1):		{(0,9), (11,13), (15,19), (21,24), (28,29)}
	fullTest("_t = ! ( t1 - t2 && t1 )", [t_0_9, t_11_13, t_15_19, t_21_24, t_28_29])
	# ! (t1 - (t2 && t1)):	{(0,9), (11,13), (15,19), (21,24), (28,29)}
	fullTest("_t = ! ( t1 - ( t2 && t1 ) )", [t_0_9, t_11_13, t_15_19, t_21_24, t_28_29])
	# ! (t1 - t2 && t2):		{}
	fullTest("_t = ! ( t1 - t2 && t2 )", [])
	# (t1 - t2) || (t2 - t1):	{(7,7), (10,10), (14,14), (17,17), (20,20), (23,23), (25,27)}
	fullTest("_t = (t1 - t2) || (t2 - t1)", [t_7_7, t_10_10, t_14_14, t_17_17, t_20_20, t_23_23, t_25_27])
	# (t1 || t2)<10,100>: {(7,23)}
	fullTest("_t = (t1 || t2)<10,100>", [t_7_23])
	# (t1<10,100> || t2<10,100>): {}
	fullTest("_t = (t1<10,100> || t2<10,100>)", [])
	# (t1<6,100> || t2<6,100>): {(8,16)}
	fullTest("_t = (t1<6,100> || t2<6,100>)", [t_8_16])
	# (t1 || t2)<;10,100>: {(25,27)}
	fullTest("_t = (t1 || t2)<;10,100>", [t_25_27])
	# (t1<;10,100> || t2<;10,100>): {(11,13), (15,23), (25,27)}
	fullTest("_t = (t1<;10,100> || t2<;10,100>)", [t_11_13, t_15_23, t_25_27])
	# t1 && t2:			{(1,3), (8,9), (11,13), (15,16), (18,19), (21,22)}
	fullTest("_t = t1 && t2", [t_1_3, t_8_9, t_11_13, t_15_16, t_18_19, t_21_22])
	# t3 || t4:			{(7,23), (25,27)}
	fullTest("_t = t3 || t4", [t_7_23, t_25_27])
	# t3 - t4:			{(17,23)}
	fullTest("_t = t3 - t4", [t_17_23])
	# t4 - t3:			{(7,7), (25,27)}
	fullTest("_t = t4 - t3", [t_7_7, t_25_27])
	# ! t3:				{(0,7), (24,29)}
	fullTest("_t = ! t3", [t_0_7, t_24_29])
	# ! t4:				{(0,6), (17,24), (28,29)}
	fullTest("_t = ! t4", [t_0_6, t_17_24, t_28_29])

	# tests comparing builtin and external scripts
	testComp.test("t1 && t2", testComp._bAND(t1, t2))
	testComp.test("t1 || t2", testComp._bOR(t1, t2))
	testComp.test("t1 - t2", testComp._bMINUS(t1, t2))
	testComp.test("t2 - t1", testComp._bMINUS(t2, t1))
	testComp.test("! t1", testComp._bNOT(t1))
	testComp.test("! t2", testComp._bNOT(t2))
	testComp.test("f1 && f2", testComp._bAND(f1, f2))
	testComp.test("f1 || f2", testComp._bOR(f1, f2))
	testComp.test("f1 - f2", testComp._bMINUS(f1, f2))
	testComp.test("f2 - f1", testComp._bMINUS(f2, f1))
	testComp.test("! f1", testComp._bNOT(f1))
	testComp.test("! f2", testComp._bNOT(f2))

	# reset verbosity to original value
	setVerbose(verbose_save)



# ----------------------------------------------------------------------------
# Miscellaneous stuff

# create a new track "f1c" which contains all features from f1,
# clustered such that any features that are within 2 bp are grouped
# into a new feature object.
#f1c = cluster("f1", "f1c", 2, 0)

# create a new sequence called "chrA" and link the sequence to the
# FASTA file called "seq.fasta" in the data subdirectory.
#chrA = newSequence("chrA")
#setSequenceSourceFile(chrA, "data/seq.fasta", FASTA)

# load the FASTA sequence file called "seq_sm.fasta" from the data
# subdirectory.  The sequence object created will be called "seq_sm"
# (the filename, less the ".fasta" file extension).
#chrB = loadSequence("data/seq_sm.fasta", FASTA)

# create an empty sequence "X"
#x = newSequence("X")

# create 2 tracks containing randomly generated features
#ran1 = randomTrack("ran1", 100000, 1, 50000, "X")
#ran2 = randomTrack("ran2", 100000, 1, 50000, "X")
#rand1 = randomTrack("rand1", 500000, 1, 250000, "X")
#rand2 = randomTrack("rand2", 500000, 1, 250000, "X")
# time(); compute("t:ran1 pos{0,10} t:ran2"); time()
# 15s
#r1 = randomTrack("r1", 10000, 1, 5000, "X")
#r2 = randomTrack("r2", 10000, 1, 5000, "X")
# time(); compute("t:r1 pos{0,10} t:r2"); time()

# load the GFF file called "gb.gff" from the data subdirectory.  The
# resulting track will be called "gb" (the filename, less the ".gff"
# file extension.  In this example, the track is located on the
# sequence called "chr1".  Since this sequence doesn't exist, an empty
# sequence object called "chr1" will be created.
#gb = loadTrack("data\\gb.gff", GFF)

# display the track "gb" using Genome Browser
# genomeBrowserTrack(gb)


