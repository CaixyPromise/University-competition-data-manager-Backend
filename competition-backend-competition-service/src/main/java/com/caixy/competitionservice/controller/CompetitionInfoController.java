package com.caixy.competitionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.annotation.AuthCheck;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.dto.match.MatchInfoAddRequest;
import com.caixy.model.dto.match.MatchInfoQueryRequest;
import com.caixy.model.dto.match.MatchInfoUpdateRequest;
import com.caixy.model.entity.MatchInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.model.vo.match.MatchInfoQueryVO;
import com.caixy.model.vo.match.MatchRegistrationVO;
import com.caixy.serviceclient.service.UserFeignClient;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 比赛信息接口控制器
 *
 * @name: com.caixy.competitionservice.controller.CompetitionInfoController
 * @author: CAIXYPROMISE
 * @since: 2024-02-11 13:43
 **/

// todo: 根据是否有文件列表上传来生成JWT Token，以此来根据token来上传
@RestController
@RequestMapping("/Competition")
@Slf4j
public class CompetitionInfoController
{
    @Resource
    private MatchInfoService matchInfoService;

    @Resource
    private UserFeignClient userService;




    private static final int COPY_PROPERTIES_ADD = 1;

    private static final int COPY_PROPERTIES_UPDATE = 2;




    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "添加信息", notes = "添加信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", value = "业务数据", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "__file", paramType = "form")
    })
    public BaseResponse<String> addMatchInfo(@RequestPart("file") MultipartFile logoFile,
                                             @RequestPart("data") @Valid MatchInfoAddRequest postAddRequest,
                                             HttpServletRequest request)
    {
        if (postAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("postAddRequest: {}", postAddRequest);
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(matchInfoService.addMatchInfo(postAddRequest, logoFile, loginUser));
    }

    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateMatchInfo(@RequestBody MatchInfoUpdateRequest postUpdateRequest)
    {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MatchInfo post = copyProperties(postUpdateRequest, null, COPY_PROPERTIES_UPDATE);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        MatchInfo oldMatchInfo = matchInfoService.getById(id);
        ThrowUtils.throwIf(oldMatchInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = matchInfoService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 获取比赛信息，并且根据用户身份转VO;
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/23 19:05
     */
    @GetMapping("/get/profile")
    public BaseResponse<MatchInfoProfileVO> getMatchInfo(@RequestParam(value = "id", required = true) Long id,
                                                         HttpServletRequest request)
    {
        if (id == null || id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前用户，可以不需要登录
        User loginUser = userService.getLoginUser(request, false);
        //  判断是否是管理员
        boolean canAdmin = userService.isAdmin(loginUser);
        MatchInfoProfileVO profileVO = matchInfoService.getMatchInfo(id, canAdmin);
        log.info("profileVO: {}", profileVO);
        return ResultUtils.success(profileVO);
    }

    @PostMapping("/get/registration")
    public BaseResponse<MatchRegistrationVO> getRegistrationInfo(@RequestBody long matchId, HttpServletRequest request)
    {
        if (matchId <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo: 校验当前用户是否已经加入团队或者已经创建团队
        // 获取当前用户id，检测登录状态
        User loginUser = userService.getLoginUser(request);
        // 获取比赛信息
        MatchInfo matchInfo = matchInfoService.getById(matchId);
        if (matchInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }
        boolean isAlive = matchIsAlive(matchInfo);
        if (!isAlive)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛已结束或未到开始报名时间");
        }
        // 返回注册信息
        MatchRegistrationVO registrationVO = MatchRegistrationVO.EntityToVO(matchInfo);

        return ResultUtils.success(registrationVO);
    }


    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteMatchInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        MatchInfo oldMatchInfo = matchInfoService.getById(id);
        ThrowUtils.throwIf(oldMatchInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldMatchInfo.getCreatedUser().equals(user.getId()) && !userService.isAdmin(user))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = matchInfoService.removeById(id);
        return ResultUtils.success(b);
    }


    /**
     * 分页获取列表（仅管理员）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<MatchInfoQueryVO>> listMatchInfoByPage(@RequestBody MatchInfoQueryRequest postQueryRequest)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<MatchInfo> postPage = matchInfoService.page(new Page<>(current, size));
        Page<MatchInfoQueryVO> voPage = new Page<>(current, size);
        voPage.setTotal(postPage.getTotal());
        List<MatchInfoQueryVO> voList =
                postPage.getRecords().stream().map(MatchInfoQueryVO::convertToAdminVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    /**
     * 分页获取列表（全部用户：首页列表）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<MatchInfoQueryVO>> listMatchInfoByVoPage(@RequestBody MatchInfoQueryRequest postQueryRequest)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<MatchInfo> postPage = matchInfoService.page(new Page<>(current, size));
        Page<MatchInfoQueryVO> voPage = new Page<>(current, size);
        voPage.setTotal(postPage.getTotal());
        List<MatchInfoQueryVO> voList =
                postPage.getRecords().stream().map(MatchInfoQueryVO::convertToPageVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    // endregion


    // 使用泛型和反射优化属性复制方法
    private <T> MatchInfo copyProperties(T sourceItem, Long loginUserId, int type)
    {
        MatchInfo post = new MatchInfo();
        BeanUtils.copyProperties(sourceItem, post);

        // 通用处理，根据类型设置创建用户和处理JSON字段
        if (type == COPY_PROPERTIES_ADD && loginUserId != null)
        {
            post.setCreatedUser(loginUserId);
        }

        return post;
    }

    /**
     * 判断比赛是否活跃，可报名状态；
     * 1. 如果报名时间还没到，不允许报名
     * 2. 如果报名时间已过，不允许报名
     * 3. 如果比赛正在进行中，不允许报名
     * 4. 如果比赛已经结束，不允许报名
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/26 17:34
     */
    private static boolean matchIsAlive(MatchInfo matchInfo)
    {
        int status = matchInfo.getMatchStatus();
        // 比赛状态：0 - 报名准备中; 1 - 比赛报名中; 2 - 报名已结束; 3 - 比赛进行中; 4 - 比赛已结束
        // 不是报名状态不能报名
        if (status != 1)
        {
            return false;
        }
        Date now = new Date(); // 获取当前时间
        Date signUpStartTime = matchInfo.getSignUpStartTime();
        Date signUpEndTime = matchInfo.getSignUpEndTime();
        // 如果当前时间早于报名开始时间或晚于报名结束时间，则不允许报名
        // 如果当前时间不在报名开始时间和报名结束时间之间，则不允许报名
        return now.after(signUpStartTime) && now.before(signUpEndTime);
    }

}
