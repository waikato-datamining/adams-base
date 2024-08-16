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

#   Copyright (C) 2011-2017 University of Waikato, Hamilton, NZ

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`

. "$BASEDIR"/bin/env.sh

CLASSPATH="$RESOURCES":"$PRIORITY":"$REPO/*"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# check options
MEMORY=512m
MAIN=adams.flow.FlowRunner
ARGS=
OPTION=
HEADLESS=
WHITESPACE="[[:space:]]"
for ARG in "$@"
do
  if [ "$ARG" = "-memory" ] || [ "$ARG" = "-main" ] || [ "$ARG" = "-no-gui" ]
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
  elif [ "$OPTION" = "-no-gui" ]
  then
    HEADLESS="-Djava.awt.headless=true"
    OPTION=""
    continue
  elif [ "$OPTION" = "-venv" ]
  then
    echo "Activating Python virtual environment at: $ARG"
    . $ARG/bin/activate
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
"$JCMD" \
  -classpath "$CLASSPATH" \
  -Xmx$MEMORY \
  --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED \
  --add-exports=java.desktop/sun.awt.image=ALL-UNNAMED \
  --add-exports=java.desktop/com.sun.media.sound=ALL-UNNAMED \
  $HEADLESS \
  $MAIN \
  $ARGS
