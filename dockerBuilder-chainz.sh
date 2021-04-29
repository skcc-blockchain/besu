
IMG_NAME='chainz/besu'

#BESU_VERSION='20.10.2'
BESU_VERSION=`cat gradle.properties | cut -d'=' -f2`
echo 'build version:' $BESU_VERSION

IMG_TAG=$IMG_NAME':'$BESU_VERSION

#DATE='2021-04-21T18:26Z'
DATE=$(date +%Y-%m-%dT%H:%MZ)
echo 'build date:' $DATE

GIT_REVISION=$(git rev-parse HEAD)
REV=${GIT_REVISION:0:8}
echo 'vcs(github):' $REV

# 이미지 생성
echo 'Docker build'
docker build -t $IMG_TAG -f ./Dockerfile-chainz . --build-arg VERSION=$BESU_VERSION --build-arg BUILD_DATE=$DATE --build-arg VCS_REF=$REV
