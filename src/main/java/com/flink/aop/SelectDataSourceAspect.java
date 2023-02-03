package com.flink.aop;

import com.flink.annotation.SelectDataSource;
import com.flink.context.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(1)
@Component
public class SelectDataSourceAspect {

    /**
     * 定义切点Pointcut
     */
    @Pointcut("@annotation(com.flink.annotation.SelectDataSource)")
    public void executeService() {

    }

    @Around("executeService()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        SelectDataSource dataSource = method.getAnnotation(SelectDataSource.class);
        if (dataSource != null) {
            log.info("切换到{}~", dataSource.value().name());
            DataSourceContextHolder.set(dataSource.value());
        }
        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DataSourceContextHolder.clear();
        }
    }

}
