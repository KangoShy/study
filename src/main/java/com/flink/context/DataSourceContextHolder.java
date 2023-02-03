package com.flink.context;


import com.flink.constant.DBTypeEnum;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DataSourceContextHolder {

    private static final ThreadLocal<DBTypeEnum> CONTEXT_HOLDER = new ThreadLocal<>();

    private static final AtomicInteger COUNTER = new AtomicInteger(-1);

    public static void set(DBTypeEnum dbType) {
        CONTEXT_HOLDER.set(dbType);
    }

    public static DBTypeEnum get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
        LoggerFactory.getLogger(DataSourceContextHolder.class).info("切换到Master~");
    }

    /**
     * 多slave可以采取轮询方式
     */
    public static void slave() {
        set(DBTypeEnum.SLAVE);
        LoggerFactory.getLogger(DataSourceContextHolder.class).info("切换到Slave~");
        //  轮询
        /*int index = COUNTER.getAndIncrement() % 2;
        if (COUNTER.get() > 9999) {
            COUNTER.set(-1);
        }
        if (index == 0) {
            set(DBTypeEnum.SLAVE1);
            System.out.println("切换到slave1");
        } else {
            set(DBTypeEnum.SLAVE2);
            System.out.println("切换到slave2");
        }*/
    }

}
