REM Copyright 2007, 2012 Stephen Fisher and Junhyong Kim, University of
REM Pennsylvania.
REM
REM This file is part of Glo-DB.
REM 
REM Glo-DB is free software: you can redistribute it and/or modify it
REM under the terms of the GNU General Public License as published by
REM the Free Software Foundation, either version 3 of the License, or
REM (at your option) any later version.
REM 
REM Glo-DB is distributed in the hope that it will be useful, but
REM WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
REM General Public License for more details.
REM 
REM You should have received a copy of the GNU General Public License
REM along with Glo-DB. If not, see <http://www.gnu.org/licenses/>.

ECHO OFF

REM the subdirectory 'classes' must exist.

REM the parser is created using javacc.  to make changes to the parser,
REM the files Parser.jj and ParserIO.java are the only files that should
REM be edited.  All other files are recreated by javacc from Parser.jj.  
REM After Parser.jj is edited, makeParser.bat should be run.  This will 
REM update the relevant parser java files.  After makeParser is run, then
REM this file should be run to add the parser changes into gloDB.jar.

REM
echo Setup the environment.
REM

set CLASSPATH=.;classes;jython.jar
set GLODB=edu\upenn\gloDB\*.java
set IO=edu\upenn\gloDB\io\*.java
set PARSER=edu\upenn\gloDB\PARSER\*.java
set GUI=edu\upenn\gloDB\gui\*.java

REM
echo Compile the java files.
REM

rem javac -classpath %CLASSPATH% -d classes Main.java %GLODB% %IO% %PARSER% %GUI%
javac -classpath %CLASSPATH% -d classes %GLODB% %IO% %PARSER% %GUI%

REM
echo Create the jar file.
REM

jar cf gloDB.jar -C classes .

REM
echo Update the jar file.
REM

rem jar uf gloDB.jar -C classes .
