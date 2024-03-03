package com.caixy.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取回复评论请求体
 *
 * @name: com.caixy.model.dto.comment.GetReplyRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 02:28
 **/
@Data
public class GetReplyRequest implements Serializable
{
    /**
     * 回复评论id
     */
    private Long commentId;
    private static final long serialVersionUID = 1L;
}
