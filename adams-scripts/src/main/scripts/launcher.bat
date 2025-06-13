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
@REM Copyright (C) 2011-2025 University of Waikato, Hamilton, NZ
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
set ENVVARS=
set PRIORITY="-priority %REPO%\java-cup-11b-20160615.jar"
set COLLAPSE=-collapse
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
  if "%~1"=="-priority" (
    set OPTION=%~1
    goto next
  )
  if "%~1"=="-env" (
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
  if "%OPTION%"=="-nocollapse" (
    set COLLAPSE=" "
    goto next
  )
  if "%OPTION%"=="-jvm" (
    set JVM=%JVM% -jvm %~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-cpa" (
    set CPA=%CPA% -cpa %~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-priority" (
    set PRIORITY=%PRIORITY% -priority %~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-env" (
    set ENVVARS=%ENVVARS% -env %~1
    set OPTION=
    goto next
  )
  if "%OPTION%"=="-venv" (
    echo "Activating Python virtual environment"
    %~1\Scripts\activate
    goto next
  )

  set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1

:next
  shift
  goto Loop

:AssembleCmd
set JCMD=java
where /q "java"
if %ERRORLEVEL% NEQ 0 set JCMD=""

if defined JAVA_HOME if not EXIST "%JAVA_HOME%\bin\java.exe" echo JAVA_HOME variable is incorrect: %JAVA_HOME% & goto javaerror
if not "%JAVA_HOME%"=="" set JCMD="%JAVA_HOME%\bin\java"

if defined JAVACMD if not EXIST "%JAVACMD%" echo JAVACMD variable is incorrect: %JAVACMD% & goto javaerror
if not "%JAVACMD%"=="" set JCMD="%JAVACMD%"

echo Using: %JCMD%


set REPO=%BASEDIR%\lib
set RESOURCES=%BASEDIR%\resources
set CLASSPATH="%RESOURCES%";"%REPO%\*"
set AGENT=%REPO%\sizeofag-1.1.0.jar
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JCMD% %JAVA_OPTS%^
 -classpath %CLASSPATH_PREFIX%;%CLASSPATH%^
 -Dbasedir="%BASEDIR%"^
 adams.core.management.Launcher^
 -memory %MEMORY%^
 -javaagent %AGENT%^
 %JVM% %CPA% %PRIORITY% %ENVVARS% %COLLAPSE%^
 -jvm --add-opens=java.desktop/sun.awt.shell=ALL-UNNAMED^
 -jvm --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED^
 -jvm --add-exports=java.desktop/sun.awt.image=ALL-UNNAMED^
 -jvm --add-exports=java.desktop/com.sun.media.sound=ALL-UNNAMED^
 -jvm --add-exports=java.base/sun.nio.cs=ALL-UNNAMED^
 -main %MAIN%^
 -doc-dir "%BASEDIR%\docs"^
 %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:javaerror
echo You can download Java from:
echo   https://adoptium.net/
echo And configure the JAVA_HOME environment variable to point to your Java installation
echo (use the directory above the "bin" directory for the environment variable):
echo   https://www.alphr.com/environment-variables-windows-10/
echo   https://www.alphr.com/set-environment-variables-windows-11/
echo Alternatively, you can also set the JAVACMD environment variable that points to the
echo actual java.exe.
pause
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1
goto end

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
