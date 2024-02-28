package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍信息表
 *
 * @TableName team_info
 */
@TableName(value = "team_info")
@Data
public class TeamInfo implements Serializable
{
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 比赛id
     */
    private Long raceId;

    /**
     * 队长id
     */
    private Long userId;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 团队标签
     */
    private String teamTags;

    /**
     * 最大人数：创建时设置，避免满了还被申请加入
     */
    private Integer maxNum;


    /**
     * 团队报名大项的id
     */
    private Long categoryId;

    /**
     * 团队报名小项的id
     */
    private Long eventId;

    /**
     * 过期时间，为比赛报名结束时间
     */
    private Date expireTime;




    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer isPublic;

    /**
     * 0 - 团队组建中; 1 - 报名成功; 2 - 已解散
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

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