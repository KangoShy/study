package com.flink.aop;

import com.flink.context.DataSourceContextHolder;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Aspect
//@Order(1)
//@Component
public class DataSourceAspect {
    /**
     * 需要读的方法,切面
     */
    @Pointcut("!@annotation(com.flink.annotation.SelectDataSource)" +
            "&& (execution(* com.flink.service..*.select*(..)) " +
            "|| execution(* com.flink.service..*.get*(..)))")
    public void readPointcut() {

    }

    /**
     * 写切面
     */
    @Pointcut("@annotation(com.flink.annotation.SelectDataSource) " +
            "|| execution(* com.flink.service..*.insert*(..))" +
            "|| execution(* com.flink.service..*.save*(..))" +
            "|| execution(* com.flink.service..*.add*(..))" +
            "|| execution(* com.flink.service..*.update*(..))" +
            "|| execution(* com.flink.service..*.edit*(..))" +
            "|| execution(* com.flink.service..*.delete*(..))" +
            "|| execution(* com.flink.service..*.remove*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        DataSourceContextHolder.slave();
    }

    @Before("writePointcut()")
    public void write() {
        DataSourceContextHolder.master();
    }

    @After("readPointcut()")
    public void readAfter() {
        DataSourceContextHolder.clear();
    }

    @After("writePointcut()")
    public void writeAfter() {
        DataSourceContextHolder.clear();
    }
}
