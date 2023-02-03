package com.flink.controller;

import com.flink.annotation.RateLimiter;
import com.flink.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口限流实践
 *
 * @see com.flink.config.RedisConfig
 * @see com.flink.aop.CurrentLimitAspect
 * @see RateLimiter
 * limit.lua
 */
@RestController
public class InterFaceCurrentLimitingController {

    /**
     * 限流 一分钟只能访问10次
     *
     * @return Result<?>
     */
    @RequestMapping(value = "frequentVisit", method = RequestMethod.GET)
    @RateLimiter(id = "frequentVisit", limit = 10)
    public Result<?> frequentVisit() {

        return Result.success("Pass~");
    }

}
