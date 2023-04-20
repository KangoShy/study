package com.flink.function;

/**
 * 消息发送函数接口
 */
public interface SendMessageFunction {

    void send(String val);

    default boolean checkPermission() {
        return true;
    }

}
