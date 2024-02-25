package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 比赛信息表
 *
 * @TableName match_info
 */
@TableName(value = "match_info")
@Data
public class MatchInfo implements Serializable
{
    /**
     * 比赛ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 比赛名称
     */
    private String matchName;

    /**
     * 比赛描述
     */
    private String matchDesc;

    /**
     * 比赛状态
     */
    private Integer matchStatus;

    /**
     * 比赛宣传图片(logo)
     */
    private String matchPic;

    /**
     * 比赛类型: A类, B类, C类
     */
    private String matchType;

    /**
     * 比赛等级: 国家级, 省级
     */
    private String matchLevel;

    /**
     * 比赛规则
     */
    private String matchRule;

    /**
     * 比赛所允许的分组(学院/部门): 默认为全部学院/专业专业可以参加
     */
    private String matchPermissionRule;
    /**
     * 比赛分组
     */
    private String matchGroup;
    /**
     * 比赛标签
     */
    private String matchTags;

    /**
     * 比赛奖品
     */
    private String matchAward;

    /**
     * 比赛附件列表
     */
    private String matchFileList;

    /**
     * 比赛创建人id
     */
    private Long createdUser;

    /**
     * 团队成员大小配置【最小, 最大】
     */
    private String teamSize;

    /**
     * 团队指导老师数量配置【最小, 最大】
     */
    private String teacherSize;

    /**
     * 比赛报名开始时间
     */
    private Date signUpStartTime;

    /**
     * 比赛报名结束时间
     */
    private Date signUpEndTime;

    /**
     * 比赛开始时间
     */
    private Date startTime;

    /**
     * 比赛结束时间
     */
    private Date endTime;

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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}