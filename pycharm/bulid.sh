cd $(dirname $0)
pwd

gradle buildPlugin

mkdir output
mv build/distributions/* output/
cd output

for file in `ls`
do
  #echo ${file}
  #echo ${file%}.md5
  md5sum ${file} > ${file}.md5
done
