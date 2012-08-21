#!/bin/bash

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

#######
# the subdirectory 'classes' must exist.
#######

# the parser is created using javacc.  to make changes to the parser,
# the files Parser.jj and ParserIO.java are the only files that should
# be edited.  All other files are recreated by javacc from Parser.jj.  
# After Parser.jj is edited, makeParser.bat should be run.  This will 
# update the relevant parser java files.  After makeParser is run, then
# this file should be run to add the parser changes into gloDB.jar.

echo "Coping the necessary files to the install directory"

echo "Coping application files..."

mkdir -p install/GloDB/docs
mkdir -p install/GloDB/classes
mkdir -p install/GloDB/icons

cp -a documentation/* install/GloDB/docs
cp -a classes/* install/GloDB/classes
cp -p icons/*.png install/GloDB/icons

cp -p LICENSE.txt install/GloDB
cp -p JYTHON-LICENSE.txt install/GloDB
cp -p User\ Guide.pdf install/GloDB
cp -p README install/GloDB

cp -p gloDB.jar install/GloDB
cp -p jython.jar install/GloDB

cp -p gloDB.bat install/GloDB
cp -p gloDB.sh install/GloDB

cp -p header.py install/GloDB
cp -p startup.py install/GloDB
cp -p test.py install/GloDB
cp -p testComp.py install/GloDB
cp -p cluster.py install/GloDB



echo "Coping source files..."

mkdir -p install/src/edu
mkdir -p install/src/icons

cp -a edu/* install/src/edu
cp -p icons/*.png install/src/icons

cp -p docs.em install/src

cp -p gloDB.jar install/src
cp -p jython.jar install/src

cp -p LICENSE.txt install/src
cp -p JYTHON-LICENSE.txt install/src
cp -p User\ Guide.doc install/src
cp -p README install/src

cp -p gloDB.bat install/src
cp -p make.bat install/src
cp -p makeDoc.bat install/src
cp -p gloDB.sh install/src
cp -p make.sh install/src

cp -p header.py install/src
cp -p startup.py install/src
cp -p test.py install/src
cp -p testComp.py install/src
cp -p cluster.py install/src

