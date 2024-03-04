package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 需求市场表
 * @TableName demands
 */
@TableName(value ="demands")
@Data
public class Demands implements Serializable
{
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 需求标题
     */
    private String title;

    /**
     * 需求描述
     */
    private String description;

    /**
     * 需求状态
     */
    private Integer status;

    /**
     * 报酬
     */
    private BigDecimal reward;

    /**
     * 需求发布者ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 截止日期
     */
    private Date deadline;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}