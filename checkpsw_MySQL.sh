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
# 账户密码加密  key：4%YkW!@g5LGcf9Ut
result=`$MYSQL << EOF |tail -n +2
select count(1) from ${DB}.${DBTABLE} WHERE deleted = 0 AND user_password= TO_BASE64(AES_ENCRYPT('${password}', '4%YkW!@g5LGcf9Ut'))
AND user_account=TO_BASE64(AES_ENCRYPT('${user}', '4%YkW!@g5LGcf9Ut'));
EOF`
if [ $result -eq 1 ];then
exit 0;
else
exit 1;
fi