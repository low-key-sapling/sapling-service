#!/bin/bash

datacenter_home_path=`pwd`/..
logfile=$datacenter_home_path/logs/watch.log
#修改2： 自己Jar包的路径
APP_NAME=epc-biz.jar
touch $logfile
while [ 1 -eq 1 ]
do
        AdminProcNum=`ps -ef | grep $APP_NAME | grep -v grep | wc -l`
	echo "---------------------------  $(date "+%Y-%m-%d %H:%M:%S") epcbizProcNum: $AdminProcNum  -----------------------------------------" >> ${logfile}  2>&1
        if [ $AdminProcNum -lt 1 ]
        then
	    cd $datacenter_home_path/bin
            ./epcbiz.sh  start
           echo "--------------------------- epcbiz Started By Watch Dog  $(date "+%Y-%m-%d %H:%M:%S")----------------------------------------" >> ${logfile}  2>&1
        fi
        sleep 10
done
