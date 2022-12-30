package com.flink.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("pf_bank_user")
public class PFBankUser {

    @TableId(type = IdType.AUTO)
    @TableField("user_id")
    private String userId;

    @TableField("user_name")
    private String userName;

    /**
     * 账户余额
     */
    @TableField("balance")
    private BigDecimal balance;
}
