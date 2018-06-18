#!/bin/bash
# ----------------------------------------------------------------------------
#  Copyright 2001-2006 The Apache Software Foundation.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
# ----------------------------------------------------------------------------

#   Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
#   reserved.

#   Copyright (C) 2011-2016 University of Waikato, Hamilton, NZ

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`

. "$BASEDIR"/bin/env.sh

CLASSPATH="$RESOURCES":"$REPO/*"
AGENT="$REPO/sizeofag-1.0.3.jar"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$AGENT" ] && AGENT=`cygpath --path --windows "$AGENT"`
fi

# check options
MEMORY=512m
MAIN=adams.gui.Main
JVM=
CPA=
ENVVARS=
ARGS=
OPTION=
PRIORITY="-priority $ADAMS_PRIORITY"
COLLAPSE="-collapse"
WHITESPACE="[[:space:]]"
for ARG in "$@"
do
  if [ "$ARG" = "-memory" ] || [ "$ARG" = "-main" ] || [ "$ARG" = "-jvm" ] || [ "$ARG" = "-cpa" ] || [ "$ARG" = "-priority" ] || [ "$ARG" = "-env" ]
  then
  	OPTION=$ARG
  	continue
  fi

  if [ "$OPTION" = "-memory" ]
  then
    MEMORY=$ARG
    OPTION=""
    continue
  elif [ "$OPTION" = "-main" ]
  then
    MAIN=$ARG
    OPTION=""
    continue
  elif [ "$OPTION" = "-nocollapse" ]
  then
    COLLAPSE=""
    continue
  elif [ "$OPTION" = "-jvm" ]
  then
    if [[ $ARG =~ $WHITESPACE ]]
    then
      JVM="$JVM -jvm \"$ARG\""
    else
      JVM="$JVM -jvm $ARG"
    fi
    OPTION=""
    continue
  elif [ "$OPTION" = "-cpa" ]
  then
    if [[ $ARG =~ $WHITESPACE ]]
    then
      CPA="-cpa \"$ARG\""
    else
      CPA="-cpa $ARG"
    fi
    OPTION=""
    continue
  elif [ "$OPTION" = "-priority" ]
  then
    PRIORITY="-priority \"$ARG\""
    OPTION=""
    continue
  elif [ "$OPTION" = "-env" ]
  then
    if [[ $ARG =~ $WHITESPACE ]]
    then
      ENVVARS="-env \"$ARG\""
    else
      ENVVARS="-env $ARG"
    fi
    OPTION=""
    continue
  fi

  if [[ $ARG =~ $WHITESPACE ]]
  then
    ARGS="$ARGS \"$ARG\""
  else
    ARGS="$ARGS $ARG"
  fi
done

# launch class
"$JCMD" $JAVA_OPTS \
  -classpath "$CLASSPATH" \
  -Dbasedir="$BASEDIR" \
  adams.core.management.Launcher \
  -memory $MEMORY \
  -javaagent "$AGENT" \
  $JVM \
  $CPA \
  $PRIORITY \
  $ENVVARS \
  $COLLAPSE \
  -main $MAIN \
  -doc-dir "$BASEDIR/docs" \
  $ARGS
