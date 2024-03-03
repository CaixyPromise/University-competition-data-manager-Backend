package com.caixy.contentservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.contentservice.service.ReplyInfoService;
import com.caixy.model.entity.ReplyInfo;
import com.caixy.contentservice.mapper.ReplyInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【reply_info(比赛讨论回复信息表)】的数据库操作Service实现
* @createDate 2024-03-01 21:45:28
*/
@Service
public class ReplyInfoServiceImpl extends ServiceImpl<ReplyInfoMapper, ReplyInfo>
    implements ReplyInfoService
{
    /**
     * 获取回复的评论
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 04:25
     */
    @Override
    public List<ReplyInfo> getReplyComment(List<Long> commentIds)
    {
        QueryWrapper<ReplyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parentId", commentIds);
        return this.list(queryWrapper);
    }

    @Override
    public ReplyInfo getReplyInfo(Long replyId)
    {
        QueryWrapper<ReplyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parentId", replyId);
        return this.getOne(queryWrapper);
    }
}




