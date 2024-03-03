package com.caixy.contentservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.dto.comment.AddCommentRequest;
import com.caixy.model.dto.comment.ListCommentByIdPage;
import com.caixy.model.dto.comment.ReplyCommentRequest;
import com.caixy.model.entity.CommentInfo;
import com.caixy.model.entity.User;

/**
* @author CAIXYPROMISE
* @description 针对表【comment_info(比赛提问与回答信息表)】的数据库操作Service
* @createDate 2024-03-01 02:59:25
*/
public interface CommentInfoService extends IService<CommentInfo> 
{

    Page<CommentInfo> selectMainCommentsByRaceId(ListCommentByIdPage listCommentByIdPage);

    Boolean addComment(AddCommentRequest commentInfo, User loginUser);

    Boolean replyComment(ReplyCommentRequest replyCommentRequest, User loginUser);

    boolean deleteComment(Long id);
}
