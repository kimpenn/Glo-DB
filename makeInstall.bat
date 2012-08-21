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
REM
REM copy the necessary files to the install directory
REM
ECHO ON


REM "Coping the application files"

xcopy documentation install\gloDB\documentation /E /Q /Y
xcopy icons\*.png install\gloDB\icons /Q /Y

xcopy LICENSE.txt install\gloDB /Q /Y
xcopy JYTHON-LICENSE.txt install\gloDB /Q /Y
xcopy "User Guide.pdf" install\gloDB /Q /Y
xcopy README install\gloDB /Q /Y

xcopy gloDB.jar install\gloDB /Q /Y
xcopy jython.jar install\gloDB /Q /Y

xcopy gloDB.bat install\gloDB /Q /Y
xcopy gloDB.sh install\gloDB /Q /Y

xcopy header.py install\gloDB /Q /Y
xcopy startup.py install\gloDB /Q /Y
xcopy test.py install\gloDB /Q /Y
xcopy testComp.py install\gloDB /Q /Y
xcopy cluster.py install\gloDB /Q /Y



REM "Coping the source files"

xcopy edu install\src\edu /E /Q /Y
xcopy icons\*.png install\src\icons /Q /Y

xcopy docs.em install\src /Q /Y

xcopy gloDB.jar install\src /Q /Y
xcopy jython.jar install\src /Q /Y

xcopy LICENSE.txt install\gloDB /Q /Y
xcopy JYTHON-LICENSE.txt install\gloDB /Q /Y
xcopy "User Guilde.doc" install\src /Q /Y
xcopy README install\src /Q /Y

xcopy gloDB.bat install\src /Q /Y
xcopy make.bat install\src /Q /Y
xcopy makeDoc.bat install\src /Q /Y
xcopy gloDB.sh install\src /Q /Y
xcopy make.sh install\src /Q /Y

xcopy header.py install\src /Q /Y
xcopy startup.py install\src /Q /Y
xcopy test.py install\src /Q /Y
xcopy testComp.py install\src /Q /Y
xcopy cluster.py install\src /Q /Y

