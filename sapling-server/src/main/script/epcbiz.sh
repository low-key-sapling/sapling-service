#!/bin/bash
##############################
## 名称: epcbiz.sh
## 描述: epcbiz
## 参数: 暂无
## 作者: xby
## 日期: 2025-07-29
## 版本：V1.0
## 备注: root用户执行
##############################

current_path=$(cd `dirname $0`; pwd)
JAVA_OPTS=$(cat $current_path/../bin/jvm.options)
mbwsjvm_path=$current_path/../../../zfmbwsjdk.option

epcbiz_home_path=$current_path/..
logfile=$epcbiz_home_path/logs/epcbiz_run.log
prefix=$(cd $current_path/../../../../.. ; pwd)
sysinfo=$(uname -a)
touch $logfile
echo "epcbiz_home_path:$epcbiz_home_path" >> ${logfile} 2>&1
echo "prefix:$prefix" >> ${logfile} 2>&1
source $mbwsjvm_path
COMMON_JVM=$(cat $current_path/../../../zfmbwsjvm.option)
#Jar包的路径
APP_NAME=${epcbiz_home_path}/epc-biz.jar

# 其余无需修改，直接用即可
#使用说明，用来提示输入参数

usage() {

echo "Usage: sh startup.sh [start|stop|restart|status]"

exit 1

}

#检查程序是否在运行

is_exist(){

pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}'`


#如果不存在返回1，存在返回0

if [ -z "${pid}" ]; then

 return 1

else

 return 0

fi

}

#启动方法,启动watchDog

start(){

sudo chmod a+x "$prefix/appstore/programfiles/securityHarden.sh"
source "$prefix/appstore/programfiles/securityHarden.sh" $prefix

is_exist

if [ $? -eq 0 ]; then

 echo "${APP_NAME} is already running. pid=${pid}" >> ${logfile}  2>&1 

else
 
 cd $epcbiz_home_path
 nohup $JAVA/bin/java  ${JAVA_OPTS} ${COMMON_JVM}  -Dcustom.srvname=mbws_epcbiz -Dloader.path=./lib -jar ${APP_NAME} >/dev/null 2>&1 &
sleep 2

pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}'`

echo "${APP_NAME} is started.  pid=${pid} " >> ${logfile}  2>&1


echo "--------------------------- epcbiz STARTED  $(date "+%Y-%m-%d %H:%M:%S")-----------------------------------------" >> ${logfile}  2>&1

if [ -f $epcbiz_home_path/bin/epcbiz-watch.sh ]; then

	watchCount=`ps -ef|grep epcbiz-watch.sh|grep -v grep|wc -l`
	if [ $watchCount -le 0 ]; then
	    cd $epcbiz_home_path/bin
		nohup $epcbiz_home_path/bin/epcbiz-watch.sh >/dev/null 2>&1 &
		echo "--------------------------- epcbiz  WATCH DOG NOT Exists , Now STARTED  $(date "+%Y-%m-%d %H:%M:%S")-----------------------------------------" >> ${logfile}  2>&1
	else
	       echo "--------------------------- epcbiz  WATCH DOG  Exists ,$(date "+%Y-%m-%d %H:%M:%S") ,watchCount : $watchCount---------------------------------" >> ${logfile}  2>&1
	fi
fi

fi
}

#停止方法,停止watchDog

stop(){

	ps -ef | grep "epcbiz-watch" | grep -v grep | {
	while read uid pid ppid c stime tty time cmd;
	do
		kill -9 $pid
	    echo "--------------------------- epcbiz  WATCH DOG  KILLED  $(date "+%Y-%m-%d %H:%M:%S")-----------------------------------------" >> ${logfile}  2>&1
	done
	}
	i=1
	while [ $i -le 5 ]
	do
		number=`ps -ef | grep "epcbiz-watch" | grep -v grep | head -n 1 | wc -l`
		if [ $number -le 0 ];then
			break
		fi
		i=$(($i+1))
		sleep 1s
	done

is_exist

if [ $? -eq 0 ]; then
   kill -9 $pid
   echo "kill epcbiz PID: $pid " >> ${logfile}  2>&1 
   echo "--------------------------- epcbiz KILLED  $(date "+%Y-%m-%d %H:%M:%S")-----------------------------------------" >> ${logfile}  2>&1 

else
   echo "--------------------------- epcbiz PID $pid  NOT RUNNING   $(date "+%Y-%m-%d %H:%M:%S") -------------------------" >> ${logfile}  2>&1 
fi

}

#输出运行状态

status(){

is_exist

if [ $? -eq 0 ]; then

echo "${APP_NAME} is running. Pid is ${pid}"

else

echo "${APP_NAME} is not running."

fi

}

#重启

restart(){

stop

sleep 1

start

}

#根据输入参数，选择执行对应方法，不输入则执行使用说明

case "$1" in

"start")

start

;;

"stop")

stop

;;

"status")

status

;;

"restart")

restart

;;

*)

usage

;;

esac
