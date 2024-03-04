package com.caixy.notificationservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.model.entity.Message;
import com.caixy.notificationservice.mapper.MessageMapper;
import com.caixy.notificationservice.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @author CAIXYPROMISE
 * @description 针对表【message(站内消息信息表)】的数据库操作Service实现
 * @createDate 2024-03-03 00:36:14
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService
{
    /**
     * 根据关系id删除，只给内部调用接口使用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 01:19
     */
    @Override
    public Boolean removeByRelationId(Long relationId)
    {
        if (relationId == null || relationId <= 0)
        {
            return false;
        }
        QueryWrapper<Message> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("relationshipId", relationId);
        return this.remove(deleteWrapper);
    }
}




