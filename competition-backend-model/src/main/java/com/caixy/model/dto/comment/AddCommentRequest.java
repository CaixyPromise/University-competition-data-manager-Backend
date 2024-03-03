package com.caixy.model.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 发送评论提问请求
 *
 * @name: com.caixy.model.dto.comment.AddCommentRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-01 03:14
 **/
@Data
public class AddCommentRequest implements Serializable
{
    /**
     * 评论正文
     */
    @NotNull
    @Size(max = 512, message = "评论正文长度不能超过512")
    private String content;

    /**
     * 关联的比赛id
     */
    @NotNull
    private Long raceId;


    private static final long serialVersionUID = 1L;
}
