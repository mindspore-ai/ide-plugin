csasd
cd $(dirname $0)
pwd

echo "spotbugs.sh start"
gradle spotbugsMain
echo "spotbugs.sh end"