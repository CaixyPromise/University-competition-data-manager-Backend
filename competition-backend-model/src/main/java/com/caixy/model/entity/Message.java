package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 站内消息信息表
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable
{
    /**
     * 消息id
     */
    @TableId
    private Long id;

    /**
     * 消息主题
     */
    private String subject;
    /**
     * 消息内容
     */
    private String content;

    /**
     * 跳转链接
     */
    private String targetUrl;

    /**
     * 发信人id
     */
    private Long fromUser;

    /**
     * 接受消息人id
     */
    private Long forUser;

    /**
     * 关联的信息id
     */
    private Long relationshipId;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 添加时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}