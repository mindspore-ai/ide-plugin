#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)
#gradle --no-daemon
#echo "start clean"
#gradle clean
#echo "end clean"
#gradle wrapper
#:wrapper

echo "start buildPlugin"
./gradlew buildPlugin
echo "end buildPlugin"

#echo "start spotbugsMain"
#gradle spotbugsMain
#echo "end spotbugsMain"

mkdir output
mv $BASEPATH/build/distributions/* $BASEPATH/output/
cd $BASEPATH/output

for file in ./*
do
  #echo ${file}
  #echo ${file%}.md5
  sha256sum -b ${file} > ${file}.sha256
done
