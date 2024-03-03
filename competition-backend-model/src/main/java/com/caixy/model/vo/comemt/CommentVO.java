package com.caixy.model.vo.comemt;

import com.caixy.model.entity.CommentInfo;
import com.caixy.model.entity.ReplyInfo;
import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论信息VO
 *
 * @name: com.caixy.model.vo.comemt.CommentVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-01 04:19
 **/
@Data
public class CommentVO implements Serializable
{
    /**
     * 评论id
     */
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
     * 评论正文
     */
    private String content;

    /**
     * 评论管理id
     */
    private Long parentId;

    /**
     * 创建人信息
     */
    private UserWorkVO createUserInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否有评论
     */
    private Boolean hasReply;

    private Integer replyCount;


    public static CommentVO of(CommentInfo comment)
    {
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        return commentVO;
    }

    public static CommentVO of(ReplyInfo comment)
    {
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        return commentVO;
    }


    private static final long serialVersionUID = 1L;
}