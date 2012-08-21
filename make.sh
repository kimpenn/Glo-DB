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

echo "Setup the environment."

CLASSPATH=".:classes:jython.jar"
GLODB="edu/upenn/gloDB/*.java"
IO="edu/upenn/gloDB/io/*.java"
PARSER="edu/upenn/gloDB/parser/*.java"
GUI="edu/upenn/gloDB/gui/*.java"

##########
echo "Compile the java files:"
echo "  javac -classpath $CLASSPATH -d classes $GLODB $IO $PARSER $GUI"
# javac -g -O -classpath $CLASSPATH -d classes $GLODB $IO $PARSER $GUI
javac -classpath $CLASSPATH -d classes $GLODB $IO $PARSER $GUI

##########
echo "Create the jar file:"
echo "  jar cf gloDB.jar -C classes ."
jar cf gloDB.jar -C classes .

##########
#echo "Update the jar file."
# jar uf gloDB.jar -C classes .
