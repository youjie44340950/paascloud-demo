#!/bin/sh
#说明
show_usage="args: [--OPTIONS=, --IMAGE=]"
#参数
# 指令
OPTIONS=""

# 镜像
IMAGE=""


GETOPT_ARGS=`getopt -o : -al OPTIONS:,IMAGE: -- "$@"`
eval set -- "$GETOPT_ARGS"
#获取参数
while [ -n "$1" ]
do
        case "$1" in
                -l|--OPTIONS) OPTIONS=$2; shift 2;;
                -r|--IMAGE) IMAGE=$2; shift 2;;
                -b|--backup-dir) opt_backupdir=$2; shift 2;;
                -w|--webdir) opt_webdir=$2; shift 2;;
                --) break ;;
                *) echo $1,$2,$show_usage; break ;;
        esac
done

if [[ -z $OPTIONS || -z $IMAGE  ]]; then
        echo $show_usage
        echo "OPTIONS: $OPTIONS , IMAGE: $IMAGE "
fi
CID_RUN=`docker ps | grep ${IMAGE} | awk '{print $1}'`
if [ ! -z ${CID_RUN} ]; then
   echo "停止${IMAGE}容器 CID=${CID_RUN}"
   docker stop ${CID_RUN}
fi

CID=`docker ps -a| grep ${IMAGE} | awk '{print $1}'`
if [ ! -z ${CID} ]; then
   echo "删除${IMAGE}容器 CID=${CID_RUN}"
   docker rm -f ${CID}
fi

docker pull ${IMAGE}
docker images|grep none|awk '{print $3}'|xargs docker rmi 
docker run  -itd $OPTIONS  $IMAGE 





