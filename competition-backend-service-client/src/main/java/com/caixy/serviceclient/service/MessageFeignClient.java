package com.caixy.serviceclient.service;

import com.caixy.common.common.DeleteRequest;
import com.caixy.model.dto.message.SendMessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Name: com.caixy.serviceclient.service.MessageFeignClient
 * @Description: 站内信消息通知内部调用接口
 * @Author: CAIXYPROMISE
 * @Date: 2024-03-03 00:37
 **/
@FeignClient(name = "competition-backend-content-service",
        path = "/api/content/inner/message")
public interface MessageFeignClient
{
    @PostMapping("/send")
    Boolean sendById(@RequestBody SendMessageDTO sendMessageDTO);

    @PostMapping("/delete")
    Boolean deleteByRelationId(@RequestBody DeleteRequest deleteRequest);
}
