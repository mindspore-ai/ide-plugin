#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

echo "start download gradle.zip"
wget -q 'https://tools.mindspore.cn/libs/ide_plugin_dependencies/pycharm/gradle.zip' --no-check-certificate
echo "end download gradle.zip"

echo "start unzip gradle.zip"
unzip -qq gradle.zip
echo "end unzip gradle.zip"

echo "start copy gradle.zip"
cp -rf .gradle/caches $GRADLE_USER_HOME 
echo "end copy gradle.zip"

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