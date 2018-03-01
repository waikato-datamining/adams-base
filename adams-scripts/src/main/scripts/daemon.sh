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

#   Copyright (C) 2011-2018 University of Waikato, Hamilton, NZ

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`

. "$BASEDIR"/bin/env.sh

CLASSPATH="$RESOURCES":"$PRIORITY":"$REPO/*"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

if [ "$1" = "start" ] && [ $# = 4 ]
then
  unset DISPLAY
  "$JCMD" \
    -classpath "$CLASSPATH" \
    -Xmx$2 \
    adams.flow.FlowRunner \
    -input "$3" \
    -remote-scripting-engine-cmdline "adams.scripting.engine.DefaultScriptingEngine -port $4" \
    -headless true \
    -non-interactive true \
    -register true \
    -clean-up true \
    &
elif [ "$1" = "stop" ] && [ $# = 2 ]
then
  "$JCMD" \
    -classpath "$CLASSPATH" \
    adams.scripting.CommandRunner \
    -start-local-engine false \
    -connection "adams.scripting.connection.DefaultConnection -host 127.0.0.1 -port $2" \
    -command adams.scripting.command.basic.Stop
else
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
  exit 2
fi
