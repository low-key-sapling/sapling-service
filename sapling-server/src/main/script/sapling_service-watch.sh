#!/bin/bash

sapling_service_home_path=`pwd`/..
logfile=$sapling_service_home_path/logs/watch.log
#修改2： 自己Jar包的路径
APP_NAME=sapling_service.jar
touch $logfile
while [ 1 -eq 1 ]
do
        AdminProcNum=`ps -ef | grep $APP_NAME | grep -v grep | wc -l`
	echo "---------------------------  $(date "+%Y-%m-%d %H:%M:%S") sapling_serviceProcNum: $AdminProcNum  -----------------------------------------" >> ${logfile}  2>&1
        if [ $AdminProcNum -lt 1 ]
        then
	    cd $sapling_service_home_path/bin
            ./sapling_service.sh  start
           echo "--------------------------- sapling_service Started By Watch Dog  $(date "+%Y-%m-%d %H:%M:%S")----------------------------------------" >> ${logfile}  2>&1
        fi
        sleep 10
done
