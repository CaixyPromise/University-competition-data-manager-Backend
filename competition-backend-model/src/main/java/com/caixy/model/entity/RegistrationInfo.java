package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 报名信息表
 *
 * @TableName registration_info
 */
@TableName(value = "registration_info")
@Data
public class RegistrationInfo implements Serializable
{
    /**
     * 报名id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 报名的团队id
     */
    private Long teamId;

    /**
     * 报名的比赛id
     */
    private Long raceId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}