package com.flink.annotation;

import com.flink.constant.DBTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelectDataSource {

    DBTypeEnum value() default DBTypeEnum.MASTER;

}
