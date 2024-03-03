package com.caixy.model.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 发送消息数据封装类
 *
 * @name: com.caixy.model.dto.message.SendMessageDTO
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 01:07
 **/
@Data
public class SendMessageDTO implements Serializable
{
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
}
