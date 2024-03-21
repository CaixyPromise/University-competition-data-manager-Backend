package com.caixy.competitionservice.controller;

import com.caixy.common.annotation.AuthCheck;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.competitionservice.service.RegistrationInfoService;
import com.caixy.model.dto.registration.RegistrationRaceRequest;
import com.caixy.model.entity.User;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.model.vo.team.TeamInfoVO;
import com.caixy.serviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
    private MatchInfoService matchInfoService;

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

    /**
     * 获取已经报名的队伍列表
     *
     * @author CAIXYPROMISE
     * @since 2024/3/2 03:43
     * @version 1.0
     */
    @GetMapping("/teamList")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<TeamInfoVO>> getRegisterTeamListByRaceId(@RequestParam("id") Long raceId,
                                                                      HttpServletRequest request)
    {
        if (raceId == null || raceId <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 获取登录用户
        User loginUSer = userService.getLoginUser(request);
        boolean canAdmin = userService.isAdmin(loginUSer);
        if (!canAdmin)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        return ResultUtils.success(registrationInfoService.getJoinedList(raceId));
    }
}
