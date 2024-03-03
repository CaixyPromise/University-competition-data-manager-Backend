package com.caixy.contentservice.service;

import com.caixy.model.entity.ReplyInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【reply_info(比赛讨论回复信息表)】的数据库操作Service
* @createDate 2024-03-01 21:45:28
*/
public interface ReplyInfoService extends IService<ReplyInfo> {

    List<ReplyInfo> getReplyComment(List<Long> commentIds);

    ReplyInfo getReplyInfo(Long replyId);
}
