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

@echo off

REM
echo Create the java documentation.
REM

javadoc @docs.em

REM
echo Create the javacc documentation.
REM

cd edu\upenn\gloDB\parser
java -classpath "javacc.jar" jjdoc Parser.jj
copy Parser.html  ..\..\..\..\parser_jj.html
cd ..\..\..\..

