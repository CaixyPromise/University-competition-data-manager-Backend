package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户钱包信息
 * @TableName user_wallet
 */
@TableName(value ="user_wallet")
@Data
public class UserWallet implements Serializable {
    /**
     * 钱包id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 已冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 支付密码
     */
    private String payPassword;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}