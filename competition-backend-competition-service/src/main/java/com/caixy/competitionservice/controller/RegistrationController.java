package com.caixy.competitionservice.controller;

import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.exception.BusinessException;
import com.caixy.competitionservice.service.RegistrationInfoService;
import com.caixy.model.dto.RegistrationRaceRequest;
import com.caixy.model.entity.User;
import com.caixy.serviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 比赛报名接口控制器
 *
 * @name: com.caixy.competitionservice.controller.RegistrationController
 * @author: CAIXYPROMISE
 * @since: 2024-02-29 16:32
 **/
@RestController
@RequestMapping("/registration")
@Slf4j
public class RegistrationController
{
    @Resource
    private RegistrationInfoService registrationInfoService;

    @Resource
    private UserFeignClient userService;




    @PostMapping("/sign")
    public BaseResponse<Boolean> signUpRace(@RequestBody @Valid
                                            RegistrationRaceRequest registrationRaceRequest,
                                            HttpServletRequest request)
    {
        if (registrationRaceRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(registrationInfoService.saveRegistrationInfo(registrationRaceRequest, loginUser));
    }
}
