package com.caixy.contentservice.controller.inner;

import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.exception.BusinessException;
import com.caixy.contentservice.service.MessageService;
import com.caixy.model.dto.message.SendMessageDTO;
import com.caixy.model.entity.Message;
import com.caixy.serviceclient.service.MessageFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 站内信内部调用接口控制器
 *
 * @name: com.caixy.contentservice.controller.inner.MessageInnerController
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 01:04
 **/
@RestController
@RequestMapping("/inner/message")
public class MessageInnerController implements MessageFeignClient
{
    @Resource
    private MessageService messageService;

    /**
     * 内部发送站内信
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 01:21
     */
    @Override
    @PostMapping("/send")
    public Boolean sendById(@RequestBody SendMessageDTO sendMessageDTO)
    {
        if (sendMessageDTO == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (sendMessageDTO.getContent().isEmpty() || sendMessageDTO.getContent().length() > 1024)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容不能为空或长度不能超过1024");
        }
        if (!sendMessageDTO.getTargetUrl().isEmpty() && sendMessageDTO.getTargetUrl().length() > 512)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标地址长度不能超过512");
        }
        Message message = new Message();
        BeanUtils.copyProperties(sendMessageDTO, message);
        messageService.save(message);
        return true;
    }

    /**
     * 根据关联id删除站内信
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 01:21
     */
    @Override
    @PostMapping("/delete")
    public Boolean deleteByRelationId(@RequestBody DeleteRequest deleteRequest)
    {
        return messageService.removeByRelationId(deleteRequest.getId());
    }

}
