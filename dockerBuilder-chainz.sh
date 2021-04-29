
IMG_NAME='chainz/besu'

#BESU_VERSION='20.10.2'
BESU_VERSION=`cat gradle.properties | cut -d'=' -f2`
echo 'build version:' $BESU_VERSION

IMG_TAG=$IMG_NAME':'$BESU_VERSION

#BUILD_DATE='2021-04-21T18:26Z'
BUILD_DATE=$(date +%Y-%m-%dT%H:%MZ)
echo 'build date:' $BUILD_DATE

# 이미지 생성
echo 'Docker build'
docker build -t $IMG_TAG -f ./Dockerfile-chainz . --build-arg VERSION=$BESU_VERSION --build-arg BUILD_DATE=$BUILD_DATE
