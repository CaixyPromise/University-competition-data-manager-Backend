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
import com.caixy.common.utils.JsonUtils;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.dto.department.DepartAndMajorValidationResponse;
import com.caixy.model.dto.match.MatchInfoAddRequest;
import com.caixy.model.dto.match.MatchInfoQueryRequest;
import com.caixy.model.dto.match.MatchInfoUpdateRequest;
import com.caixy.model.entity.MatchInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.match.MatchInfoQueryVO;
import com.caixy.serviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 比赛信息接口控制器
 *
 * @name: com.caixy.competitionservice.controller.CompetitionInfoController
 * @author: CAIXYPROMISE
 * @since: 2024-02-11 13:43
 **/
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

    private static final int TYPE_LENGTH = 20;

    private static final int LEVEL_LENGTH = 20;


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
    public BaseResponse<Long> addMatchInfo(@RequestBody MatchInfoAddRequest postAddRequest, HttpServletRequest request)
    {
        if (postAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        MatchInfo post = copyProperties(postAddRequest, loginUser.getId(), COPY_PROPERTIES_ADD);

        boolean result = matchInfoService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newMatchInfoId = post.getId();
        return ResultUtils.success(newMatchInfoId);
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
//        MatchInfo post = new MatchInfo();
        MatchInfo post = copyProperties(postUpdateRequest, null, COPY_PROPERTIES_UPDATE);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        MatchInfo oldMatchInfo = matchInfoService.getById(id);
        ThrowUtils.throwIf(oldMatchInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = matchInfoService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
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
                postPage.getRecords().stream().map(MatchInfoQueryVO::EntityToVO).collect(Collectors.toList());
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

        // 提取和验证权限规则
        Map<Long, List<Long>> permissions;
        if (sourceItem instanceof MatchInfoAddRequest)
        {
            permissions = ((MatchInfoAddRequest) sourceItem).getMatchPermissionRule() != null ?
                          ((MatchInfoAddRequest) sourceItem).getMatchPermissionRule().getPermissions() : null;
        }
        else
        {
            permissions = ((MatchInfoUpdateRequest) sourceItem).getMatchPermissionRule() != null ?
                          ((MatchInfoUpdateRequest) sourceItem).getMatchPermissionRule().getPermissions() : null;
        }
        validateAndProcessPermissions(permissions);

        // 处理JSON格式的字段
        processJsonFields(sourceItem, post);

        return post;
    }

    private void validateAndProcessPermissions(Map<Long, List<Long>> permissions)
    {
        if (permissions == null || permissions.isEmpty())
        {
            permissions = new HashMap<Long, List<Long>>()
            {{
                put(-1L, Collections.singletonList(-1L));
            }};
        }
        else
        {
            // 执行校验
            DepartAndMajorValidationResponse validated = userService.validateDepartmentsAndMajors(permissions);
            if (!validated.getIsValid())
            {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学院ID或专业ID无效或不匹配");
            }
        }
    }

    private void processJsonFields(Object sourceItem, MatchInfo post)
    {
        try
        {
            Map<Long, List<Long>> permissions = new HashMap<>();
            if (sourceItem instanceof MatchInfoAddRequest)
            {
                int levelLength = ((MatchInfoAddRequest) sourceItem).getMatchLevel().length();
                if (levelLength > 20)
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛级别长度不能超过20");
                }
                int typeLength = ((MatchInfoAddRequest) sourceItem).getMatchType().length();
                if (typeLength > 20)
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛类型长度不能超过20");
                }

                permissions = ((MatchInfoAddRequest) sourceItem).getMatchPermissionRule().getPermissions();
            }
            else if (sourceItem instanceof MatchInfoUpdateRequest)
            {
                int levelLength = ((MatchInfoUpdateRequest) sourceItem).getMatchLevel().length();
                if (levelLength > LEVEL_LENGTH)
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛级别长度不能超过20");
                }
                int typeLength = ((MatchInfoUpdateRequest) sourceItem).getMatchType().length();
                if (typeLength > TYPE_LENGTH)
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛类型长度不能超过20");
                }
                permissions = ((MatchInfoUpdateRequest) sourceItem).getMatchPermissionRule().getPermissions();
            }
            String matchPermissionRuleJson = JsonUtils.objectToString(permissions);
            post.setMatchPermissionRule(matchPermissionRuleJson);

            String matchTagsJson = JsonUtils.objectToString(sourceItem instanceof MatchInfoAddRequest ?
                                                            ((MatchInfoAddRequest) sourceItem).getMatchTags() :
                                                            ((MatchInfoUpdateRequest) sourceItem).getMatchTags());
            post.setMatchTags(matchTagsJson);

            String matchAwardJson = JsonUtils.objectToString(sourceItem instanceof MatchInfoAddRequest ?
                                                             ((MatchInfoAddRequest) sourceItem).getMatchAward() :
                                                             ((MatchInfoUpdateRequest) sourceItem).getMatchAward());
            post.setMatchAward(matchAwardJson);
        }
        catch (Exception e)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
    }
}
