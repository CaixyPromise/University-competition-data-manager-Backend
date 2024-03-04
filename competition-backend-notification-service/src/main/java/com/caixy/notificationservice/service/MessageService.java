package com.caixy.notificationservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.entity.Message;

/**
 * @author CAIXYPROMISE
 * @description 针对表【message(站内消息信息表)】的数据库操作Service
 * @createDate 2024-03-03 00:36:14
 */
public interface MessageService extends IService<Message>
{

    Boolean removeByRelationId(Long relationId);
}
