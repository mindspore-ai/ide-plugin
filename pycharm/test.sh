set -ex
set -o pipefail

cd $(dirname $0)
pwd

gradle test