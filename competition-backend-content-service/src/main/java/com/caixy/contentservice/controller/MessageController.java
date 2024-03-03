package com.caixy.contentservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.common.*;
import com.caixy.common.exception.BusinessException;
import com.caixy.contentservice.service.MessageService;
import com.caixy.model.entity.Message;
import com.caixy.model.entity.User;
import com.caixy.model.enums.message.MessageUserRoleEnum;
import com.caixy.model.vo.message.MessageVO;
import com.caixy.serviceclient.service.UserFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内信消息通知外部接口控制器
 *
 * @name: com.caixy.contentservice.controller.MessageController
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 00:38
 **/
@RestController
@RequestMapping("/message")
public class MessageController
{
    @Resource
    private MessageService messageService;

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 分页获取站内信
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 00:59
     */
    @GetMapping("/get")
    public BaseResponse<Page<MessageVO>> getMessageInfo(PageRequest pageRequest, HttpServletRequest request)
    {
        User loginUser = userFeignClient.getLoginUser(request);
        final int currentSize = pageRequest.getCurrent();
        final int pageSize = pageRequest.getPageSize();

        // 查询站内信消息列表
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forUser", loginUser.getId());
        Page<Message> messagePage = messageService.page(new Page<>(currentSize, pageSize), queryWrapper);

        List<MessageVO> messageVOList = messagePage.getRecords().stream().map(item ->
        {
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(item, messageVO);
            if (item.getFromUser() <= 0)
            {
                MessageUserRoleEnum roleEnum = MessageUserRoleEnum.getEnumByCode(item.getFromUser().intValue());
                messageVO.setFromUser(roleEnum.getDesc());
            }
            else
            {
                User byId = userFeignClient.getById(item.getFromUser());
                messageVO.setFromUser(byId.getUserName());
            }
            return messageVO;
        }).collect(Collectors.toList());
        // 封装成Page
        Page<MessageVO> finalMessagePage = new Page<>(currentSize, pageSize);
        finalMessagePage.setRecords(messageVOList);
        finalMessagePage.setTotal(messagePage.getTotal());
        return ResultUtils.success(finalMessagePage);
    }

    /**
     * 删除站内信
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 01:00
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMessageById(DeleteRequest deleteRequest, HttpServletRequest request)
    {

        if (deleteRequest == null || deleteRequest.getId() == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数异常");
        }
        User loginUser = userFeignClient.getLoginUser(request);
        Message message = messageService.getById(deleteRequest.getId());
        if (message == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "消息不存在");
        }
        Long forUser = message.getForUser();
        if (!forUser.equals(loginUser.getId()))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除");
        }
        boolean result = messageService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

}
