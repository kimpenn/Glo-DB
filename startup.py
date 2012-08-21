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
#
# startup.py - initialize console
#
# @author  Stephen Fisher
# @version $Id: startup.py,v 1.70.2.63 2007/03/01 21:17:32 fisher Exp $


# load standard definitions
from header import *

#----------------------------------------------------------------------------
# General functions
#----------------------------------------------------------------------------

def quit():
	"""quit():
	Quits the application.
	"""
	sys.exit(0)

def viewAPI():
	"""viewAPI():
	Launch HTML viewer to view Java API documentation.
	"""
	return ViewHTML(java.net.URL("file:documentation/index.html"))

def parserDefs():
	"""parserDefs():
	View parser definitions.
	"""
	return ViewParserDefs()

def resetGlobals():
	"""resetGlobals():
	Reset user-definable global variables to their default values.
	"""
	resetGloDBDefaults()
	GUIUtils.resetGUIDefaults()

def about():
	printMsg("Glo-DB")
	printMsg("version 1.0")
	printMsg("Copyright 2012 Stephen Fisher and Junhyong Kim, University of Pennsylvania.")
	printMsg("This program comes with ABSOLUTELY NO WARRANTY; for details type 'warranty()'.")

def time():
	"""time():
	Prints the current time.
	"""
	print java.util.Calendar.getInstance().time

def warranty():
	"""Prints warranty section from GPL license.
	"""
	printMsg("THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY")
	printMsg("APPLICABLE LAW.  EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT")
	printMsg("HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM 'AS IS' WITHOUT WARRANTY")
	printMsg("OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,")
	printMsg("THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR")
	printMsg("PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM")
	printMsg("IS WITH YOU.  SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF")
	printMsg("ALL NECESSARY SERVICING, REPAIR OR CORRECTION.")

def help(*args):
	"""Prints help for user-defined functions:
	help(): print list of commands.
	help(<UDF>): print extended documentation for 'UDF'.
	"""
	if len(args) == 0: helpList()
	if len(args) == 1: printMsg(args[0].__doc__)

def helpList():
	printMsg("For help on a specific built-in functions type 'help(<name>)' at the console.")
	printMsg("Example: help(loadTrack)")
	printMsg("   quit()")
	printMsg("   viewAPI()")
	printMsg("   parserDefs()")
	printMsg("   resetGlobals()")
	printMsg("   about()")
	printMsg("   warranty()")
	printMsg("   renameTrack()")
	printMsg("   removeTrack()")
	printMsg("   getTrack()")
	printMsg("   newSequence()")
	printMsg("   getSequence()")
	printMsg("   loadTrack()")
	printMsg("   saveTrack()")
	printMsg("   loadSequence()")
	printMsg("   saveSequence()")
	printMsg("   setSequenceSourceFile()")
	#printMsg("   genomeBrowserData()")
	#printMsg("   genomeBrowserTrack()")
	printMsg("   addTrackFileType()")
	printMsg("   addSequenceFileType()")
	printMsg("   menu()")
	printMsg("   guiLoadTrack()")
	printMsg("   guiLoadSequence()")
	printMsg("   compute()")

#----------------------------------------------------------------------------
# edu.upenn.gloDB functions
#----------------------------------------------------------------------------

#def printMsg(*args):
#	"""printMsg(message [, type]):
#	Will print the message to the default output, either the message
#	window in the GUI or the console.  The message is labelled
#	according to the optional 'type' argument (1 = WARNING, 2 = ERROR).
#	"""
#	if len(args) == 1: GloDBUtils.printMsg(args[0])
#	else: GloDBUtils.printMsg(args[0], args[1])

def setVerbose(val):
	"""setVerbose(val):
	Changes the feedback verbosity of the application.  The amount of
	detailed feedback is as follows: 2 = lots, 1 = no warnings, 0 = no
	feedback.  This value will persist across instances of the
	application.
	"""
	GloDBUtils.setVerbose(val)

def renameTrack(*args):
	"""renameTrack(old [, new]):
	Changes the track's name from 'old' to 'new'.
	"""
	if (len(args) == 1): GUIUtils.renameTrack(args[0])
	else: ObjectHandles.renameTrack(args[0], args[1])

def removeTrack(id):
	"""renameTrack(id):
	Removes the track from the set of all tracks ('trackPool').
	"""
	ObjectHandles.removeTrack(id)

def getTrack(id):
	"""getTrack(id):
	Returns a reference to the track idd 'id'.
	"""
	return ObjectHandles.getTrack(id)

def newSequence(*args):
	"""newSequence(id [, filename, format):
	Creates a new sequence.  The sequence will not have any attributes
	or data.  If filename and format (FAS | FASTA, GB | GENBANK) are
	included then the sequence will be linked to the appropriate source
	file (see setSequenceSourceFile()).
	"""
	s = Sequence(args[0])
	if len(args) > 1: setSequenceSourceFile(s, args[1], args[2])
	return s

def getSequence(id):
	"""getSequence(id):
	Returns a reference to the sequence named 'id'.
	"""
	return ObjectHandles.getSequence(id)

#----------------------------------------------------------------------------
# edu.upenn.gloDB.io functions
#----------------------------------------------------------------------------

def loadTrack(*args):
	"""loadTrack(filename, format [, sequence]):
	Loads a Track from a file of type 'format' (FAS | FASTA, GFF, or GB
	| GENBANK).  If 'sequence' is included, then this will be the
	Sequence used for all Features loaded.  If 'sequence' is not
	included, then an attempt will be made to get Sequence data from
	the file.
	"""
	if len(args) == 3: sequence = args[2]
	else: sequence = ""
	
	if args[1] == FASTA: format = FASTATrack()
	elif args[1] == GFF: format = GFFTrack()
	elif args[1] == GENBANK: format = GenBankTrack()
	else: return printMsg("Invalid format", ERROR)

	return format.load(args[0], sequence)

def saveTrack(*args):
	"""saveTrack(trackID, format [, filename, overwrite]):
	Saves the track called 'trackID' in the file 'filename',
	overwritting the file if 'overwrite' equals 1 (don't overwrite if
	equal 0).  If 'filename' and 'overwrite' are not included, then the
	trackID will be used for 'filename' and a preexisting file WILL BE
	overwritten.  In either case, filename will be forced to include
	the extension (eg '.gff' or '.fasta').  The format of the file is
	specified by 'format' (FAS | FASTA, GFF, or GB | GENBANK).
	"""
	if args[1] == FASTA: format = FASTATrack()
	elif args[1] == GFF: format = GFFTrack()
	elif args[1] == GENBANK: format = GenBankTrack()
	else: return printMsg("Invalid format", ERROR)
	
	if len(args) == 4: format.save(args[0], args[2], args[3])
	else: format.save(args[0])

def loadSequence(filename, format, id="", parser=FASTAParserMinimal()):
	"""loadSequences(filename, format [, id [, parser]]):
	Loads all Sequences from a file of type 'format' (FAS | FASTA, GB |
	GENBANK).  If 'id' not included, then the file name will be
	used. If 'parser' is included, then this will be the parser used
	for all sequences loaded.  If 'parser' is not included, then
	FASTAParserMinimal will be used.
	"""
	if format == GLODB: return printMsg("Loading binary sequences is not yet implemented.")
	elif format == FASTA: format = FASTASequence()
	elif format == GENBANK: return printMsg("Loading GenBank sequences is not yet implemented.")
	else: return printMsg("Invalid sequence file format", ERROR)

	return format.load(filename, id, parser)

def setSequenceSourceFile(id, filename, format):
	"""setSequenceSourceFile(id, filename, format):
	This will connect a sequence object with a data source.  For now
	FASTA files ('FASTA') are the only valid data source.
	"""
	if format == GLODB: return printMsg("Loading binary sequences is not yet implemented.")
	elif format == FASTA: loader = FASTASequence()
	elif format == GENBANK: return printMsg("Loading GenBank sequences is not yet implemented.")
	else: return printMsg("Invalid sequence file format", ERROR)

	sequence = getSequence(id)
	sequence.setDataLoader(loader)
	sequence.addLoaderArg("filename", filename)
	

#def addTrackFileType(type):
#	"""addTrackFileType(type):
#	Adds the track file type to the list of possible types.
#	"""
#	FileIO.addTrackFileType(type)

#def addSequenceFileType(type):
#	"""addSequenceFileType(type):
#	Adds the sequence file type to the list of possible types.
#	"""
#	FileIO.addSequenceFileType(type)

#addTrackFileType(GloDBTrack())
#addTrackFileType(GenBankTrack())
addTrackFileType(FASTATrack())
addTrackFileType(GFFTrack())

addSequenceFileType(FASTASequence())

#def saveDataGenomeBrowser(*args):
#	"""genomeBrowserData(data [, filename, overwrite]):
#	Posts 'data' to Genome Browser and saves the resulting web page.
#	"""
#	if len(args) == 0: HTMLFile.saveText(GenomeBrowser.post(args[0]))
#	else: HTMLFile.saveText(GenomeBrowser.post(args[0]), args[1], args[2])

#def viewDataGenomeBrowser(data):
#	"""genomeBrowserData(data):
#	Posts 'data' to Genome Browser and views the resulting web page.
#	"""
#	ViewHTML(GenomeBrowser.post(data))

#def saveTrackGenomeBrowser(*args):
#	"""genomeBrowserTrack([track [, filename, overwrite]]):
#	Posts 'track' to Genome Browser and save the resulting web page.
#	The track being viewed must have a valid source or the Genome
#	Browser won't be properly oriented.
#	"""
#	if (len(args) == 0) or (len(track) == 0): track = GUIUtils.trackSelector()
#	else: track = args[0]
#
#	if len(args) < 3: HTMLFile.saveText(GenomeBrowser.viewTrack(track))
#	else: HTMLFile.saveText(GenomeBrowser.post(track), args[1], args[2])

#def viewTrackGenomeBrowser(*args):
#	"""genomeBrowserTrack([track]):
#	Posts 'track' to Genome Browser and views the resulting web page.
#	The track being viewed must have a valid source or the Genome
#	Browser won't be properly oriented.
#	"""
#	if len(args) == 0:
#		track = GUIUtils.trackSelector()
#		if len(track) > 0: ViewHTML(GenomeBrowser.viewTrack(track))
#	else: ViewHTML(GenomeBrowser.viewTrack(args[0]))
	

	

#----------------------------------------------------------------------------
# edu.upenn.gloDB.gui functions
#----------------------------------------------------------------------------

def menu():
	"""menu():
	States the applications GUI.
	"""
	Root.show()

def trackBrowser(id=""):
	"""trackBrowser([id]):
	Displays the track browser.
	"""
	TrackBrowser.show(id)

def sequenceBrowser(id=""):
	"""sequenceBrowser([id]):
	Displays the sequence browser.
	"""
	SequenceBrowser.show(id)

def guiLoadTrack():
	"""guiLoadTrack():
	Loads a track from disk, using a dialog window to choose the
	track file.
	"""
	return GUITrackIO.loadTrack()

def guiLoadSequence():
	"""guiLoadSequence():
	Loads a sequence from disk, using a dialog window to choose the
	sequence file.
	"""
	return GUISequenceIO.loadSequence()

#----------------------------------------------------------------------------
# edu.upenn.gloDB.parser functions
#----------------------------------------------------------------------------

def compute(expr):
	"""compute(expr):
	Computes the output of the expression 'expr'.  The resulting Track
	will be returned.  'None' will be returned on error.  Example:
	   >>> compute("a = f1 AND f2")
	"""
	return ParserUtils.compute(expr)
"""
	p = Parser(expr)
	if id == "": id = expr
	try:
		ops = p.run()
	except:
		printMsg("Invalid expression, can not be solved.", ERROR)
		# [0] is exception obj, [1] is it's string value
		msg = "%s" % (sys.exc_info()[1])
		printMsg(msg, ERROR)
		return None
	return ParserUtils().solveOps(ops, id)
"""
		

#----------------------------------------------------------------------------
# load User-Defined Functions
#----------------------------------------------------------------------------

from cluster import *

#----------------------------------------------------------------------------
# Testing
#----------------------------------------------------------------------------

# set of routines that match the built-in routines and are used to
# test the routines
#import testComp
# Running tests from test.py.
#from test import *
#t1.addFeature(ExactFeature(5,13, sy))
#t1.addFeature(ExactFeature(15,17, sy))

#from scratch import *

# menu()
#s = loadSequence("data/x_genomic_3-1.fasta", FASTA)
#x = loadSequence("data/seq.fasta", FASTA, "X")
#setSequenceSourceFile("X", "data/test_seq.fasta", FASTA)
#loadTrack("data/test.gff",GFF)
#loadTrack("data/dexter.gff",GFF)
#tt = loadTrack("data/test_sm.gff",GFF)

#fc = loadSequence("data/dmel-4.fasta", FASTA, "4")
#ft = loadTrack("data/dmel-4.gff",GFF)
#ft.setID("dmel")
#ft2 = loadTrack("data/dmel2.gff",GFF)
#ft3 = loadTrack("data/dmel3.gff",GFF)

#t1 = loadTrack("data/t1.gff",GFF)
#t2 = loadTrack("data/t2.gff",GFF)

# snps = loadTrack("data/snps.gff", GFF)
#mce = loadTrack("data/mce.gff", GFF)
#compute("ceSNPs = ((snps AND mce) sAND snps)")
# tfbs = loadTrack("data/tfbs.gff", GFF)
#compute("ce_tfbs_snps = ceSNPs OR ((snps AND tfbs) sAND snps)")
#introns = loadTrack("data/introns.gff", GFF)
#compute("ce = ((snps AND mce) sAND mce)")
# compute("bindingSites = ((snps AND tfbs) sAND tfbs)")
#setSequenceSourceFile("chrX", "data\chrX.fasta", FASTA)

menu()
