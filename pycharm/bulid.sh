#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

echo "start spotbugsMain"
gradle spotbugsMain
echo "end spotbugsMain"

echo "start buildPlugin"
gradle buildPlugin --debug
echo "end buildPlugin"


mkdir output
mv $BASEPATH/build/distributions/* $BASEPATH/output/
cd $BASEPATH/output

for file in ./*
do
  #echo ${file}
  #echo ${file%}.md5
  sha256sum -b ${file} > ${file}.sha256
done
