#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

echo $BASEPATH

ls -all
#gradle --no-daemon
#echo "start clean"
#gradle clean
#echo "end clean"
#gradle wrapper
#:wrapper
#gradle showMeCache
echo "start download gradle.zip"
wget 'https://tools.mindspore.cn/libs/ide_plugin_dependencies/pycharm/gradle.zip' --no-check-certificate
echo "end download gradle.zip"

ls -all

echo "start unzip gradle.zip"
unzip -qq gradle.zip
echo "end unzip gradle.zip"


ls -all

#chmod +x gradlew

echo "start buildPlugin"
gradle --offline buildPlugin
echo "end buildPlugin"

ls -all

#ls $BASEPATH/.gradle/caches/module-2/files-2.1

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
