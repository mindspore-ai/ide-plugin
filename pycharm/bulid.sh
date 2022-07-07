#!/bin/bash

set -e
BASEPATH=$(cd "$(dirname $0)"; pwd)

echo $BASEPATH

echo $GRADLE_USER_HOME

ls $GRADLE_USER_HOME -all


#rm -rf $GRADLE_USER_HOME/caches/

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
#unzip -qq gradle.zip -d /home/jenkins/agent-working-dir
echo "end unzip gradle.zip"

ls $GRADLE_USER_HOME -all
echo "start copy gradle.zip"
cp -rf .gradle/caches $GRADLE_USER_HOME 
echo "end copy gradle.zip"
#mkdir -p $GRADLE_USER_HOME/caches/module-2/files-2.1/com.google.protobuf/protoc/3.7
# .1/bf162385553faf7da54f895e42ef0e94a01f02a7
#cp -f libs/protoc-3.7.1-linux-x86_64.exe $GRADLE_USER_HOME/caches/module-2/files-2.1/com.google.protobuf/protoc/3.7
# .1/bf162385553faf7da54f895e42ef0e94a01f02a7

#ls -all

#chmod +x gradlew

echo "start buildPlugin"
gradle buildPlugin
echo "end buildPlugin"

echo "workdir"
#ls -R .gradle/caches/module-2/files-2.1/com.google.protobuf
echo "globaldir"
#ls -R $GRADLE_USER_HOME/caches/module-2/files-2.1/com.google.protobuf

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