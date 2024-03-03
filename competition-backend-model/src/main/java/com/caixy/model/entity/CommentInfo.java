package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 比赛提问与回答信息表
 * @TableName comment_info
 */
@TableName(value ="comment_info")
@Data
public class CommentInfo implements Serializable
{
    /**
     * 评论id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 评论创建人
     */
    private Long userId;

    /**
     * 比赛id
     */
    private Long raceId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 回复数量
     */
    private Integer replyCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否被删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}