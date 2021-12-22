set -ex
set -o pipefail

cd $(dirname $0)
pwd

gradle buildPlugin
cd build/distributions
for file in `ls`
do
  echo ${file}
  echo ${file%.*}-sum.md5
  md5sum ${file} > build/distribution/ide-plugin-1.0-SNAPSHOT
done
