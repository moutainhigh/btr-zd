#!/bin/sh
APP_NAME=btr-zd-publish
#APP_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." && pwd )"
APP_HOME=/home/apps/bootstrap/current
#APP_NAME="$( echo "$APP_HOME" | sed -r 's@^/.*/([^/]+)/?@\1@g' )"
APP_PID_FILE=/home/logs/${APP_NAME}/$APP_NAME.pid

#chown -R nobody:nobody /home/logs/${APP_NAME}/ $APP_HOME


JAVA_HOME="/usr/local/jdk/"
JAVA_OPTS="-server -Xms2560m -Xmx2560m -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSFullGCsBeforeCompaction=1 -XX:CMSInitiatingOccupancyFraction=75 -XX:+HeapDumpBeforeFullGC -XX:+HeapDumpOnOutOfMemoryError -XX:+CMSClassUnloadingEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Djava.io.tmpdir=/home/logs/$APP_NAME/ -XX:HeapDumpPath=/mfsdata/javaHeapDump/btr-zd/ -Xloggc:/home/logs/$APP_NAME/gc.log -XX:ErrorFile=/home/logs/$APP_NAME/hs_err_pid%p.log"
#REMOTE_DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
if [ -f "/home/apps/bootstrap/current/JAVA_OPTS" ];then
  source /home/apps/bootstrap/current/JAVA_OPTS
fi

MAIN_CLASS=$APP_NAME*.war
if [ ${APP_NAME}x == 'bootstrapx'  ]; then
  MAIN_CLASS=*.war
fi

CLASSPATH=$APP_HOME/config:$APP_HOME/webapp:$APP_HOME/lib/*

###################################
#(函数)判断程序是否已启动
#
#说明：
# @RET  : 0 => 程序正在运行
#         1 => 程序未运行
#         2 => PID文件存在，但不存在程序进程号
###################################
checkStatus(){
    if [ -f "$APP_PID_FILE" ]; then
       if  [ -z "`cat $APP_PID_FILE`" ];then
        echo "ERROR: Pidfile '$APP_PID_FILE' exists but contains no pid"
        return 2
       fi

       PID=`cat $APP_PID_FILE`

       RET=`ps -f -p $PID|grep java`

       if [ -n "$RET" ];then
         return 0;
       else
         return 1;
       fi
    else
         return 1;
    fi
}


###################################
#(函数)启动程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示程序已启动
#3. 如果程序没有被启动，则执行启动命令行
#4. 启动命令执行后，再次调用checkpid函数
#5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
start(){
    if ( checkStatus );then
      PID=`cat $APP_PID_FILE`
      echo "INFO: process with pid '$PID' is already running"
      exit 0
    fi

    echo -n "starting $APP_NAME ..."

    nohup $JAVA_HOME/bin/java $JAVA_OPTS $REMOTE_DEBUG_OPTS -classpath $CLASSPATH -jar $APP_HOME/lib/$MAIN_CLASS > /home/logs/tomcat/catalina.out &

    echo $! > $APP_PID_FILE

    count=1
    while(($count<10))
    do
       checkStatus
       if [ "$?" ]; then
         break
       else
         echo -n "."
       fi
       let "count++"
       sleep 1s
    done

    if ( checkStatus );then
         echo "[OK]"
    else
         echo "[Failed]"
    fi
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
   if( checkStatus );then
      PID=`cat $APP_PID_FILE`

      echo -n "stopping $MAIN_CLASS ...($PID) "

      sudo kill -9  $PID

      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[FAILED]"
      fi

      if( checkStatus ); then
         stop
      fi
   fi

   rm -f $APP_PID_FILE >/dev/null 2>&1
}

###################################
#(函数)判断程序是否已启动
###################################
status(){
    checkStatus

    case "$?" in
    0)
      echo "$APP_NAME is running..."
      ;;
    1)
      echo "$APP_NAME is not running..."
      ;;
    2)
      echo "file $APP_NAME.pid is exists , not the pid is not exist"
      ;;
    *)
      echo "Unknow status"
   esac
}

###################################
#读取脚本的第二个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     start
     ;;
   'status')
     status
     ;;
   'info')
     info
     ;;
  *)
     echo "Usage: $0 {appName} {start|stop|restart|status|info}"
     exit 1
esac
exit 0