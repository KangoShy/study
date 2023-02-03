package com.flink.aop;

import com.flink.annotation.RateLimiter;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 接口限流切面类
 */
@Aspect
@Component
public class CurrentLimitAspect {

    /**
     * 使用@Autowired注解报错：@Autowired是根据类型注入的，注入时泛型也会被考虑进去，要么泛型与bean中保持一致，要么通过@Resource按照name注入
     */
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedisScript<Long> limitScript;


    @Before("@annotation(annotation)")
    public void share(RateLimiter annotation) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        limit(request, annotation);
    }


    public void limit(HttpServletRequest request, RateLimiter annotation) {
        String combineKey = getCombineKey(request, annotation);
        List<Object> keys = Collections.singletonList(combineKey);
        try {
            Long number = redisTemplate.execute(limitScript, keys, annotation.limit(), annotation.duration());
            if (number == null || number.intValue() > annotation.limit()) {
                throw new RuntimeException("接口访问频繁，请稍后再试！");
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(CurrentLimitAspect.class).warn("服务器限流异常，请稍候再试");
            throw e;
        }
    }

    public String getCombineKey(HttpServletRequest request, RateLimiter rateLimiter) {

        // make current limit key [ip + interface's id]
        String clientIP = getClientIP(request);

        return clientIP.concat("-").concat(rateLimiter.id());
    }

    public static String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(clientIP)) {
            return clientIP;
        }

        clientIP = request.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(clientIP)) {
            return clientIP;
        }

        return request.getRemoteAddr();
    }

}
