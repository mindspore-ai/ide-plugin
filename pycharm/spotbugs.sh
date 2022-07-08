#!/bin/sh

csasd
cd "$(dirname $0)"; pwd

echo "start spotbugsMain"
gradle spotbugsMain
echo "end spotbugsMain"