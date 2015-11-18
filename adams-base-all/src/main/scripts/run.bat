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
@REM Copyright (C) 2011-2015 University of Waikato, Hamilton, NZ
@REM ----------------------------------------------------------------------------

@echo off

set ERROR_CODE=0

@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
set MEMORY=512m
set MAIN=adams.gui.Main
set JVM=
set CPA=
set OPTION=
set BASEDIR=%~dp0\..
:Loop
  if "%~1"=="" goto AssembleCmd

  if "%~1"=="-memory" (
    set OPTION=%~1
    goto next
  )
  if "%~1"=="-main" (
    set OPTION=%~1
    goto next
  )
  if "%~1"=="-jvm" (
    set OPTION=%~1
    goto next
  )
  if "%~1"=="-cpa" (
    set OPTION=%~1
    goto next
  )

  if "%OPTION%"=="-memory" (
    set MEMORY=%~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-main" (
    set MAIN=%~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-jvm" (
    set JVM=%JVM% -jvm %~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-cpa" (
    set CPA=-cpa %~1
    set OPTION=
    goto next
  )

  set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1

:next
  shift
  goto Loop

:AssembleCmd
set JCMD=java
if not "%JAVA_HOME%"=="" set JCMD="%JAVA_HOME%\bin\java"
if not "%JAVACMD%"=="" set JCMD=%JAVACMD%

set REPO=%BASEDIR%\lib
set RESOURCES=%BASEDIR%\resources
set CLASSPATH="%REPO%\java-cup-11b-2015.03.26.jar";"%RESOURCES%";"%REPO%\*"
set AGENT=%REPO%\sizeofag-1.0.0.jar
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JCMD% %JAVA_OPTS% -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dbasedir="%BASEDIR%" adams.core.management.Launcher -memory %MEMORY% -javaagent %AGENT% %JVM% %CPA% -main %MAIN% -doc-dir "%BASEDIR%\docs" %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal

:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
