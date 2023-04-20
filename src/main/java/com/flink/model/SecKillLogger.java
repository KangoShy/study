/*
-- auto-generated definition
create table seckill_logger
(
    logger_id   bigint auto_increment
        primary key,
    status      tinyint     null,
    shop_id     varchar(50) null,
    stock       bigint      null,
    create_time timestamp   null,
    constraint seckill_logger_logger_id_uindex
        unique (logger_id)
);
 */
package com.flink.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("seckill_logger")
public class SecKillLogger {

    @TableId(type = IdType.AUTO, value = "logger_id")
    private Long loggerId;

    /**
     * 商品ID
     */
    @TableField("shop_id")
    private String shopId;

    /**
     * status = -1：初始化 1：扣减成功 0：扣减失败
     */
    @TableField("status")
    private int status;

    /**
     * 扣减数量
     */
    @TableField("stock")
    private int stock;

    /**
     * 发生时间
     */
    @TableField("create_time")
    private Date createTime;

}
