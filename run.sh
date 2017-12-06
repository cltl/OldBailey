#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
#LIB=$ROOT/naf2sem/lib
LIB="/target/"
XML="$1"
TRIG="$2"
# first argument is the folder with the old bailey xml files
# second argument is the folder with the trig files
# program generates modified trig files with the extension .meta

java -Xmx4000m -cp $LIB/OldBaily-v3.1.2-jar-with-dependencies.jar OldBaileyXml --xml $XML --trig $TRIG --extension ".trig"
