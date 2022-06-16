#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

#echo "start clean"
#gradle clean
#echo "end clean"

echo "start buildPlugin"
gradle buildPlugin
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
