package com.flink.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品库存表
 */
@Getter
@Setter
@TableName(value = "shop_stock")
public class ShopStock {

    /**
     * 主键
     */
    @TableId(value = "stock_id", type = IdType.AUTO)
    @TableField("stock_id")
    private Long stockId;

    /**
     * 商品ID
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 商品名称
     */
    @TableField("shop_name")
    private String shopName;

    /**
     * 库存量
     */
    @TableField("stock")
    private Long stock;
}
