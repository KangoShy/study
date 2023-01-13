#!/bin/bash
################################
#校验访问权限
################################

HOST="114.132.201.144"
PORT="3306"
DB="vpn"
DBUSER="root"
DBPASS="KangoShyVpn500"
DBTABLE="user_center"
user=`echo ${username}|sed "s#'\|;\|=\|%##g"`
MYSQL="mysql -h${HOST} -p${PORT} -u${DBUSER} -p${DBPASS} "