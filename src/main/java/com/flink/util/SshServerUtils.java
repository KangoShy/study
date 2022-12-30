package com.flink.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by joe强 on 2018/10/23 14:58
 */
public class SshServerUtils {

    private static final Logger logger = LoggerFactory.getLogger(SshServerUtils.class);

    private static Session session;

    private static boolean err =  false;

    //连接服务器
    private static void connect(String username, String passwd, String host, int port) {
        try {
            JSch jsch = new JSch();
            //获取sshSession
            session = jsch.getSession(username, host, port);
            //添加密码
            session.setPassword(passwd);
            Properties sshConfig = new Properties();
            //严格主机密钥检查x
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            //开启sshSession连接
            session.setTimeout(600000);
            session.connect();
            logger.info("Server connection successful.");
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public static String getCommandResult(String command) {
        return getCommandResult(null, null, null, 22, command);
    }


    public static String getCommandResult(String username, String passwd, String hostname, int port, String command) {
        // 创建连接
        if (session == null || !session.isConnected()) {
            connect(username, passwd, hostname, port);
            System.err.println("session已失效！再次获取");
        }
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try{
            Channel exec = session.openChannel("exec");
            ((ChannelExec) exec).setCommand(command);
            exec.connect();

            InputStream in = exec.getInputStream();

            reader = new BufferedReader(new InputStreamReader(in));

            String buf;
            while ((buf = reader.readLine()) != null) {
                sb.append(buf).append("\n");
            }
        }catch (Exception e) {
            logger.error("任务失败！");
            e.printStackTrace();
            err = true;
        } finally {
            try {
                if (reader != null) reader.close();
            }catch (Exception ignore) {
            }
            // closeSession();
        }
        return sb.toString();
    }


    private static void closeSession() {
        if (session.isConnected())
            session.disconnect();
        logger.info("session已关闭");
    }

    public static void main(String[] args) throws InterruptedException {

        // 获取session
        connect("root", "KangoShyVpn500", "114.132.201.144", 22);


        Scanner scanner = new Scanner(System.in);
        System.err.print("USA-JZ#：");
        while (scanner.hasNext()) {
            String next = scanner.next();
            String commandResult = getCommandResult(next);
            System.err.println(commandResult);
        }
    }

}