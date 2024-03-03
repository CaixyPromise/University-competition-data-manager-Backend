package com.caixy.model.dto.comment;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论分页查询配置
 *
 * @name: com.caixy.model.dto.comment.CommentPageDTO
 * @author: CAIXYPROMISE
 * @since: 2024-03-01 22:29
 **/
@Data
public class CommentPageDTO implements Serializable
{
    private Long id;
    private Long userId;
    private Long raceId;
    private String content;
    private Date createTime;
    private List<ReplyVO> replies;
    private static final long serialVersionUID = 1L;

    @Data
    public static class ReplyVO implements Serializable
    {
        private Long id;
        private Long parentId;
        private Long userId;
        private String content;
        private Date createTime;
        private static final long serialVersionUID = 1L;
    }
}
