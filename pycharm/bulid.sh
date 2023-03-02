#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

echo "start buildPlugin"
gradle buildPlugin
echo "end buildPlugin"

mkdir output
mv $BASEPATH/build/distributions/* $BASEPATH/output/
cd $BASEPATH/output

for file in ./*
do
  sha256sum -b ${file} > ${file}.sha256
done