@REM ----------------------------------------------------------------------------
@REM Copyright 2001-2004 The Apache Software Foundation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM ----------------------------------------------------------------------------
@REM

@REM ----------------------------------------------------------------------------
@REM Copyright (C) 2011-2018 University of Waikato, Hamilton, NZ
@REM ----------------------------------------------------------------------------

@echo off

set ERROR_CODE=0

:AssembleCmd
set JCMD=java
if not "%JAVA_HOME%"=="" set JCMD="%JAVA_HOME%\bin\java"
if not "%JAVACMD%"=="" set JCMD=%JAVACMD%

set REPO=%BASEDIR%\lib
set RESOURCES=%BASEDIR%\resources
set CLASSPATH="%RESOURCES%";"%REPO%\java-cup-11b-2015.03.26.jar";"%REPO%\*"
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

if "%1" == "start" goto startdaemon
if "%1" == "stop" goto stopdaemon
goto wrongcmd

@REM Start the daemon
:startdaemon
%JCMD% -classpath %CLASSPATH% -Xmx%2 adams.flow.FlowRunner^
 -input "%BASEDIR%\flows\%3"^
 -remote-scripting-engine-cmdline "adams.scripting.engine.DefaultScriptingEngine -port %4"^
 -shutdown-hook "adams.core.shutdown.StopFlows -time-out 5000"^
 -headless true^
 -non-interactive true^
 -register true^
 -clean-up true
if ERRORLEVEL 1 goto error
goto end

@REM Stop the daemon
:stopdaemon
%JCMD% -classpath %CLASSPATH% adams.scripting.CommandRunner^
 -start-local-engine false^
 -connection "adams.scripting.connection.DefaultConnection -host 127.0.0.1 -port %2"^
 -command adams.scripting.command.basic.Stop
if ERRORLEVEL 1 goto error
goto end

:wrongcmd
echo
echo "Incorrect parameter(s)!"
echo
echo "- starting a daemon:"
echo "    start <memory> <flow> <port>"
echo "  <flow> -- below the ${FLOWS} directory"
echo "  <memory> -- eg 1024m or 2g"
echo
echo "- stopping a daemon:"
echo "    stop <port>"
echo
set ERROR_CODE=2
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
goto postExec

:endNT
@endlocal

:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
