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
# header.py - standard definitions that should probably be loaded by all 
#             python modules.
#
# @author  Stephen Fisher
# @version $Id: header.py,v 1.5.2.8 2007/03/01 21:17:32 fisher Exp $


# load Java routines
import sys 
import java.lang
from java.util import Random

from edu.upenn.gloDB import *
from edu.upenn.gloDB.io import *
from edu.upenn.gloDB.parser import *
from edu.upenn.gloDB.gui import *

# these classes loaded above so we can access their methods, but
# apparently accessing their fields requires a different import
from edu.upenn.gloDB.GloDBMain import *
from edu.upenn.gloDB.GloDBUtils import *
from edu.upenn.gloDB.io.FileIO import *
FAS = FASTA
GB = GENBANK

# Track
#TRACK = GloDBUtils.TRACK
# Sequence
#SEQUENCE = GloDBUtils.SEQUENCE
# Feature
#FEATURE = GloDBUtils.FEATURE
# Binary/GloDB file
#GLODB = FileIO.GLODB
#BINARY = FileIO.GLODB
# FASTA file
#FAS = FileIO.FASTA
#FASTA = FileIO.FASTA
# GFF
#GFF = FileIO.GFF
# GenBank
#GB = FileIO.GENBANK
#GENBANK = FileIO.GENBANK

# Warning
#WARNING = GloDBUtils.WARNING
# Error
#ERROR = GloDBUtils.ERROR
