package com.caixy.model.vo.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 站内信信息返回封装类
 *
 * @name: com.caixy.model.vo.message.MessageVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 00:40
 **/
@Data
public class MessageVO implements Serializable
{
    /**
     * 站内信标题
     */
    private String subject;
    /**
     * 站内信id
     */
    private Long id;

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
    private String fromUser;

    /**
     * 添加时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
