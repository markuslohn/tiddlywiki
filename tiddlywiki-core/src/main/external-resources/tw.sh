#!/bin/sh

BASENAMECMD=`which basename`
DIRNAMECMD=`which dirname`

BASEDIR="`${DIRNAMECMD} $0`/.."
BASEDIR="`cd \"${BASEDIR}\" && pwd`"

LIBDIR="$BASEDIR"/lib

CLASSPATH="$BASEDIR"/config

for i in "$LIBDIR"/*.jar
  do
    CLASSPATH="$CLASSPATH":"$i"
  done

#echo $CLASSPATH

java -Xmx512m -classpath "$CLASSPATH" de.bimalo.tiddlywiki.fs.TiddlyWikiGenerator "$@"
