package com.flink.controller;

import com.flink.service.ReadAndWriteSeparationService;
import com.flink.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 读写分离多数据源实践
 * 两种实现方式(default 2)
 * <p>
 * （1）、Aop切面方式
 * 拦截service层方法方法{@link com.flink.aop.DataSourceAspect}
 * <p>
 * （2）、注解方式实现
 * 通过环绕通知在操作之前切换数据源 操作完后销毁 look{@link com.flink.annotation.SelectDataSource} and {@link com.flink.aop.SelectDataSourceAspect}
 * <p>
 * PS：如果已经开启了Aop切面方式，就不需要注解，否则会导致主从分配失效
 *
 * @see com.flink.config.DataSourceConfig
 * @see com.flink.config.MyBatisConfig
 * @see com.flink.constant.DBTypeEnum
 * @see com.flink.context.DataSourceContextHolder
 * @see com.flink.context.MyRoutingDataSource
 */
@RestController
public class ReadAndWriteSeparationController {

    @Resource
    ReadAndWriteSeparationService service;

    @RequestMapping(value = "selectMock", method = RequestMethod.GET)
    public Result<?> select() {
        service.select();
        return null;
    }

    @RequestMapping(value = "updateMock", method = RequestMethod.POST)
    public Result<?> update() {
        service.update();
        return null;
    }

}
