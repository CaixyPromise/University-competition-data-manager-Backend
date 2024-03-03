package com.caixy.contentservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.exception.BusinessException;
import com.caixy.contentservice.mapper.CommentInfoMapper;
import com.caixy.contentservice.mapper.ReplyInfoMapper;
import com.caixy.contentservice.service.CommentInfoService;
import com.caixy.model.dto.comment.AddCommentRequest;
import com.caixy.model.dto.comment.ListCommentByIdPage;
import com.caixy.model.dto.comment.ReplyCommentRequest;
import com.caixy.model.entity.CommentInfo;
import com.caixy.model.entity.ReplyInfo;
import com.caixy.model.entity.User;
import com.caixy.serviceclient.service.CompetitionFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author CAIXYPROMISE
 * @description 针对表【comment_info(比赛提问与回答信息表)】的数据库操作Service实现
 * @createDate 2024-03-01 02:59:25
 */
@Service
public class CommentInfoServiceImpl extends ServiceImpl<CommentInfoMapper, CommentInfo>
        implements CommentInfoService
{
    @Resource
    private CompetitionFeignClient competitionFeignClient;

    @Resource
    private ReplyInfoMapper replyInfoMapper;

    @Override
    public Page<CommentInfo> selectMainCommentsByRaceId(ListCommentByIdPage listCommentByIdPage)
    {
        Page<CommentInfo> page = new Page<>(listCommentByIdPage.getCurrent(), listCommentByIdPage.getPageSize());
        QueryWrapper<CommentInfo> queryWrapper = new QueryWrapper<>();
        String sortField = listCommentByIdPage.getSortField();
        if (sortField == null || sortField.trim().isEmpty())
        {
            sortField = "createTime"; // 默认排序字段，"createTime"
        }
        queryWrapper.eq("raceId", listCommentByIdPage.getRaceId())
//                .eq("isReply", 0)
//                .eq("isDelete", 0)
                .orderBy(true, "ASC".equals(listCommentByIdPage.getSortOrder()), sortField);

        return this.page(page, queryWrapper);
    }


    /**
     * 添加评论
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 03:33
     */
    @Override
    public Boolean addComment(AddCommentRequest commentInfo, User loginUser)
    {
        // 检查比赛是否存在
        checkRaceIsExist(commentInfo.getRaceId());
        // 保存评论
        CommentInfo newCommentInfo = new CommentInfo();
        newCommentInfo.setRaceId(commentInfo.getRaceId());
        newCommentInfo.setUserId(loginUser.getId());
        newCommentInfo.setContent(commentInfo.getContent());
        return this.save(newCommentInfo);
    }

    /**
     * 回复评论
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 03:46
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean replyComment(ReplyCommentRequest replyCommentRequest, User loginUser)
    {
        // 检查评论是否存在
        checkRaceIsExist(replyCommentRequest.getRaceId());
        // 检查回复的评论是否存在
        QueryWrapper<CommentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", replyCommentRequest.getCommentId());
        boolean commentIsExist = this.count(queryWrapper) > 0;
        if (!commentIsExist)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }
        // 保存回复
        ReplyInfo newCommentInfo = new ReplyInfo();
        newCommentInfo.setUserId(loginUser.getId());
        newCommentInfo.setContent(replyCommentRequest.getContent());
        newCommentInfo.setParentId(replyCommentRequest.getCommentId());
        int row = replyInfoMapper.insert(newCommentInfo);
        UpdateWrapper<CommentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", replyCommentRequest.getCommentId());
        updateWrapper.setSql("replyCount = replyCount + 1");
        boolean updated = this.update(updateWrapper);
        return row > 0 && updated;
    }



    @Override
    public boolean deleteComment(Long id)
    {
        QueryWrapper<CommentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .or()
                .eq("parentId", id);// 删除评论下的回复删除评论
        return this.remove(queryWrapper);
    }

    private void checkRaceIsExist(Long raceId)
    {
        Boolean isExist = competitionFeignClient.isExistById(raceId);
        if (!isExist)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不存在");
        }
    }

}




