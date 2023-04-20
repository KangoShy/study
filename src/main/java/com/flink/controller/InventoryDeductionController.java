package com.flink.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.flink.mapper.RedisDistributedLockMapper;
import com.flink.mapper.SecKillLoggerMapper;
import com.flink.model.SecKillLogger;
import com.flink.model.ShopStock;
import com.flink.util.StringUtil;
import com.flink.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * 各种场景下的秒杀库存扣减业务实践
 */
@Slf4j
@RestController
@RequestMapping("inventory")
public class InventoryDeductionController {

    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    RedisDistributedLockMapper mapper;
    @Resource
    SecKillLoggerMapper secKillLoggerMapper;

    /**
     * redis过滤请求的方式
     * <p>
     * 优点：适合大并发量，库存少的情况下
     * 缺点：如果库存量过多，请求经过redis过滤后，仍然会有大量的请求同一时间打到DB上，对DB造成压力
     * <p>
     * 设计图：src/main/resources/static/B66D8EE9-EC42-4d0b-B788-1E51F946C352.png
     *
     * @return true is deduction success
     */
    @RequestMapping(value = "inventoryDeductionSceneOne", method = RequestMethod.GET)
    public boolean inventoryDeductionSceneOne(String shopId, int stock) {

        // redis是否有库存
        Integer redisStock = StringUtil.cast(redisTemplate.opsForValue().get(shopId));

        // 无库存，LUA加锁向DB获取库存，并放入redis
        if (redisStock == null || redisStock <= 0) {
            redisStock = getStock(shopId);
            redisTemplate.opsForValue().set(shopId, redisStock);
        }

        // 扣除redis库存
        if (redisStock - stock < 0) {
            throw new IllegalArgumentException("库存数量不足~");
        }
        redisTemplate.opsForValue().set(shopId, redisStock - stock);

        // redis库存扣除成功后，扣除DB库存
        LambdaUpdateWrapper<ShopStock> updateWrapper = Wrappers.<ShopStock>lambdaUpdate().eq(ShopStock::getShopId, shopId)
                .set(ShopStock::getStock, redisStock - stock);
        return mapper.update(null, updateWrapper) > 0;
    }

    /**
     * redis过滤请求 + 异步化
     * 在{@link #inventoryDeductionSceneOne(String, int)}的基础上
     * 增加了日志记录的操作，根据该日志确保数据一致性
     * <p>
     * 优点：主要使用redis的高性能，将DB操作异步化，适合大并发量，库存多的情况下
     * 缺点：数据最终一致性
     * <p>
     * 设计图：src/main/resources/static/181BBD39-1DA1-49ad-9641-26DD1B56C821.png
     *
     * @return true is deduction success
     */
    @RequestMapping(value = "inventoryDeductionSceneTwo", method = RequestMethod.GET)
    public boolean inventoryDeductionSceneTwo(String shopId, int stock) {

        // redis是否有库存
        Integer redisStock = StringUtil.cast(redisTemplate.opsForValue().get(shopId));

        // 无库存 & 日志表已经更新完毕，LUA加锁向DB获取库存，并放入redis
        if (redisStock == null || redisStock <= 0) {

            redisStock = getStock(shopId);
            redisTemplate.opsForValue().set(shopId, redisStock);
        }

        // 扣除redis库存
        if (redisStock - stock < 0) {
            throw new IllegalArgumentException("库存数量不足~");
        }

        // 记录扣减日志
        SecKillLogger buildLogger = SecKillLogger.builder().createTime(new Date()).status(-1).shopId(shopId).stock(stock).build();
        secKillLoggerMapper.insert(buildLogger);
        try {
            redisTemplate.opsForValue().set(shopId, redisStock - stock);
            buildLogger.setStatus(1);
            secKillLoggerMapper.updateById(buildLogger);
            return true;
        } catch (Exception e) {
            buildLogger.setStatus(2);
            secKillLoggerMapper.updateById(buildLogger);
            throw e;
        }
    }

    private Integer getStock(String shopId) {
        String uuid = UUID.randomUUID().toString();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/lock.lua")));
        redisScript.setResultType(Long.class);
        Long lockExecute = redisTemplate.execute(redisScript, Collections.singletonList(shopId + "_lock"), uuid, 5);
        // 加锁失败
        if (lockExecute == null || lockExecute == 0) {
            throw new RuntimeException("系统繁忙，请稍后再试~");
        }
        Integer dbStock;
        try {
            LambdaQueryWrapper<ShopStock> eq = Wrappers.<ShopStock>lambdaQuery().eq(ShopStock::getShopId, shopId);
            ShopStock shopStock = mapper.selectOne(eq);
            if (shopStock == null) {
                redisTemplate.delete(shopId);
                throw new RuntimeException("该商品可能已被下架，请刷新页面重试~");
            }
            dbStock = shopStock.getStock();
            if (dbStock == null || dbStock <= 0) {
                redisTemplate.delete(shopId);
                throw new RuntimeException("商品已经被抢完啦~");
            }
        }
        // 释放锁
        finally {
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/unLock.lua")));
            Long unLockExecute = redisTemplate.execute(redisScript, Collections.singletonList(shopId + "_lock"), uuid);
            if (unLockExecute == null || unLockExecute == 0) log.error("锁释放失败~");
        }
        return dbStock;
    }

}
