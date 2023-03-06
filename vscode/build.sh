#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

npm install
npm install vsce@1.99.0

echo "start buildPlugin"
node_modules/.bin/vsce package
echo "end buildPlugin"

mkdir output
mv $BASEPATH/*.vsix $BASEPATH/output/

cd $BASEPATH/output

for file in ./*
do
  sha256sum -b ${file} > ${file}.sha256
done