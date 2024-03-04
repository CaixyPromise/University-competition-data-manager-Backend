package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 需求承接表
 * @TableName demand_takes
 */
@TableName(value ="demand_takes")
@Data
public class DemandTakes implements Serializable {
    /**
     * 承接id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 需求ID
     */
    private Long demandId;

    /**
     * 承接者ID
     */
    private Long userId;

    /**
     * 承接时间
     */
    private Date takeTime;

    /**
     * 承接状态
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}