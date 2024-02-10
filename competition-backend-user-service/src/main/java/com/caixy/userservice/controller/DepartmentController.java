package com.caixy.userservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.annotation.AuthCheck;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.model.dto.department.DepartmentInfoAddRequest;
import com.caixy.model.dto.department.DepartmentInfoQueryRequest;
import com.caixy.model.dto.department.DepartmentInfoUpdateRequest;
import com.caixy.model.entity.DepartmentInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.department.DepartmentInfoVO;
import com.caixy.userservice.service.DepartmentInfoService;
import com.caixy.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 学院信息接口控制器
 *
 * @name: com.caixy.userservice.controller.DepartmentController
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 01:46
 **/
// todo 实现基于学院的专业管理，考虑把他们的业务合并在一个控制器，逻辑写在service里
@RestController
@RequestMapping("/department")
@Slf4j
public class DepartmentController
{
    @Resource
    private DepartmentInfoService departmentInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addDepartmentInfo(@RequestBody DepartmentInfoAddRequest postAddRequest, HttpServletRequest request)
    {
        if (postAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DepartmentInfo post = new DepartmentInfo();
        BeanUtils.copyProperties(postAddRequest, post);

        User loginUser = userService.getLoginUser(request);
        post.setAddUserId(loginUser.getId());

        boolean result = departmentInfoService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newDepartmentInfoId = post.getId();
        return ResultUtils.success(newDepartmentInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDepartmentInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        DepartmentInfo oldDepartmentInfo = departmentInfoService.getById(id);
        ThrowUtils.throwIf(oldDepartmentInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldDepartmentInfo.getAddUserId().equals(user.getId()) && !userService.isAdmin(request))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = departmentInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDepartmentInfo(@RequestBody DepartmentInfoUpdateRequest postUpdateRequest)
    {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DepartmentInfo post = new DepartmentInfo();
        BeanUtils.copyProperties(postUpdateRequest, post);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        DepartmentInfo oldDepartmentInfo = departmentInfoService.getById(id);
        ThrowUtils.throwIf(oldDepartmentInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = departmentInfoService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<DepartmentInfoVO> getDepartmentInfoVOById(long id, HttpServletRequest request)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DepartmentInfo post = departmentInfoService.getById(id);
        if (post == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        DepartmentInfoVO departmentInfoVO = new DepartmentInfoVO();
        BeanUtils.copyProperties(post, departmentInfoVO);
        return ResultUtils.success(departmentInfoVO);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DepartmentInfo>> listDepartmentInfoByPage(@RequestBody DepartmentInfoQueryRequest postQueryRequest)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<DepartmentInfo> postPage = departmentInfoService.page(new Page<>(current, size));
        return ResultUtils.success(postPage);
    }

    // endregion
}
