ECHO OFF

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

REM
REM setup the environment.
REM

rem set CLASSPATH=classes;jython.jar
set CLASSPATH=gloDB.jar;jython.jar

REM
REM run the application passing the command line args to the application.
REM

REM jdb -classpath gloDB.jar;jython.jar edu.upenn.gloDB.GloDBMain

REM set the max heap size to 128MB, the default is 64MB
rem java -XX:+PrintGCDetails -verbose:gc -Xmx512m -cp %CLASSPATH% edu.upenn.gloDB.GloDbMain %1
java -Xmx2048m -cp %CLASSPATH% edu.upenn.gloDB.GloDBMain %1 %2 %3 %4 %5 %6 %7 %8 %9

REM for java debugger
rem jdb -classpath %CLASSPATH% edu.upenn.gloDB.GloDBMain

REM for profiling with YourKit Java Profiler
rem set PATH=c:\Program Files\YourKit Java Profiler 6.0 EAP build 1030\bin\win32;%PATH%
REM for JVMPI
rem java -Xmx1024m -Xrunyjpagent -cp %CLASSPATH% edu.upenn.gloDB.GloDbMain %1 %2 %3 %4 %5 %6 %7 %8 %9
REM for JVMTI
rem java -Xmx1024m -agentlib:yjpagent -cp %CLASSPATH% edu.upenn.gloDB.GloDbMain %1 %2 %3 %4 %5 %6 %7 %8 %9

REM -Dcom.sun.management.jmxremote allows for the use of jconsole to monitor the process
rem java -Dcom.sun.management.jmxremote -Xmx1024m -cp %CLASSPATH% edu.upenn.gloDB.GloDbMain %1 %2 %3 %4 %5 %6 %7 %8 %9

