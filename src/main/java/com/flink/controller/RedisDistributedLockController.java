package com.flink.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.flink.mapper.RedisDistributedLockMapper;
import com.flink.model.ShopStock;
import com.flink.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.UUID;

/**
 * redis 分布式锁实践
 *
 * @see com.flink.config.RedisConfig
 * @see RedisDistributedLockMapper
 * lock.lua
 * unLock.lua
 */
@RestController
public class RedisDistributedLockController {

    @Autowired
    private RedisTemplate<String, Object> redisClient;

    @Autowired
    private RedisDistributedLockMapper mapper;

    private static final Long ZERO = 0L;

    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLockController.class);

    /**
     * Snap up
     *
     * @param shopId
     * @return Snap up result.
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/snapUp", method = RequestMethod.GET)
    public Result<?> snapUp(@RequestParam String shopId) {

        ShopStock shopStock = mapper.selectOne(Wrappers.<ShopStock>lambdaQuery().eq(ShopStock::getShopId, shopId));
        if (shopStock == null) {
            return Result.fail("Nonexistence of commodity!");
        }

        Integer stock = shopStock.getStock();
        if (stock <= ZERO) {
            return Result.fail("UnderStock!");
        }

        // 加锁
        String uuid = UUID.randomUUID().toString();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/lock.lua")));
        redisScript.setResultType(Long.class);
        Long lockExecute = redisClient.execute(redisScript, Collections.singletonList(shopId), uuid, 5);
        if (lockExecute == null || lockExecute == 0) {
            return Result.fail("System error! Please try again later~");
        }

        // 获得锁，扣减库存
        try {
            LambdaUpdateWrapper<ShopStock> updateWrapper = Wrappers.<ShopStock>lambdaUpdate().set(ShopStock::getStock, stock - 1)
                    .eq(ShopStock::getShopId, shopId);
            if (mapper.update(null, updateWrapper) > 0) {
                return Result.success("Snap up success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("库存扣减失败，商品id = {}，商品名称 = {}", shopId, shopStock.getShopName());
        }
        // 释放锁
        finally {
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/unLock.lua")));
            Long unLockExecute = redisClient.execute(redisScript, Collections.singletonList(shopId), uuid);
            if (unLockExecute == null || unLockExecute == 0) {
                logger.error("UnLock fail");
            }
        }
        return Result.fail("System error!");
    }
}
