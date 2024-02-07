package com.caixy.userservice.controller.inner;

import com.caixy.model.entity.User;
import com.caixy.serviceclient.service.UserFeignClient;
import com.caixy.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 用户服务内部实现类(内部调用)
 *
 * @name: com.caixy.userservice.controller.inner.UserServiceInnerController
 * @author: CAIXYPROMISE
 * @since: 2024-02-07 00:25
 **/
@RestController
@RequestMapping("/inner")
public class UserServiceInnerController implements UserFeignClient
{
    @Resource
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam long userId)
    {
        return userService.getById(userId);
    }

    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList)
    {
        return userService.listByIds(idList);
    }

}
