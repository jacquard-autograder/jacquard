#!/usr/bin/env bash

CHECKSTYLE_URL_BASE="http://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.1/"
CHECKSTYLE_JAR="checkstyle-10.12.1-all.jar"
DIR="lib"

# This installs checkstyle, which is often used by Gradescope.
if [ ! -f "$DIR/$CHECKSTYLE_JAR" ]; then
  mkdir -p lib
  cd lib
  curl -sSLO "$CHECKSTYLE_URL_BASE/$CHECKSTYLE_JAR" -o "$DIR/$CHECKSTYLE_JAR"
  cd ..
fi
