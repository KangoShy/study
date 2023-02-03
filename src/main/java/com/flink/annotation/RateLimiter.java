package com.flink.annotation;

import java.lang.annotation.*;

/**
 * 如下：每60秒内允许100次接口访问
 */

@Documented
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 接口id
     */
    String id() default "rate_limit:";

    /**
     * 次数限制
     */
    int limit() default 100;

    /**
     * 每时间段（单位秒）
     */
    int duration() default 60;

}
