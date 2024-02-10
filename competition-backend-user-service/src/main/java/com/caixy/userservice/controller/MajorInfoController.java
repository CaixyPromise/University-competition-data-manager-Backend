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
import com.caixy.model.dto.major.MajorInfoAddRequest;
import com.caixy.model.dto.major.MajorInfoQueryRequest;
import com.caixy.model.dto.major.MajorInfoUpdateRequest;
import com.caixy.model.entity.MajorInfo;
import com.caixy.model.entity.User;
import com.caixy.model.enums.UserRoleEnum;
import com.caixy.model.vo.major.MajorInfoVO;
import com.caixy.model.vo.major.MajorInfoWithDepartmentQueryVO;
import com.caixy.model.vo.major.MajorWithDepartmentVO;
import com.caixy.userservice.service.DepartmentInfoService;
import com.caixy.userservice.service.MajorInfoService;
import com.caixy.userservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 专业信息接口控制器
 *
 * @name: com.caixy.userservice.controller.MajorInfoController
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 23:51
 **/
@RestController
@RequestMapping("/major")
public class MajorInfoController
{
    @Resource
    private MajorInfoService majorInfoService;

    @Resource
    private UserService userService;

    @Resource
    private DepartmentInfoService departmentInfoService;

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
    public BaseResponse<Long> addMajorInfo(@RequestBody MajorInfoAddRequest postAddRequest, HttpServletRequest request)
    {
        if (postAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MajorInfo post = new MajorInfo();
        BeanUtils.copyProperties(postAddRequest, post);

        User loginUser = userService.getLoginUser(request);
        Long departmentId = postAddRequest.getDepartmentId();

        if (departmentId == null || departmentId <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择学院");
        }
        // 检查学院id是否合法
        boolean departmentExistById = departmentInfoService.departmentExistById(departmentId);
        if (!departmentExistById)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学院不存在");
        }
        // 根据学院id查询学院下是否创建该专业信息
        boolean majorExistByName = majorInfoService.majorExistByName(post.getName(), departmentId);
        if (majorExistByName)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "专业已存在");
        }
        post.setDepartId(departmentId);
        post.setCreateUserId(loginUser.getId());

        boolean result = majorInfoService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newMajorInfoId = post.getId();
        return ResultUtils.success(newMajorInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMajorInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        MajorInfo oldMajorInfo = majorInfoService.getById(id);
        ThrowUtils.throwIf(oldMajorInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldMajorInfo.getCreateUserId().equals(user.getId()) && !userService.isAdmin(request))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = majorInfoService.removeById(id);
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
    public BaseResponse<Boolean> updateMajorInfo(@RequestBody MajorInfoUpdateRequest postUpdateRequest)
    {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MajorInfo post = new MajorInfo();
        BeanUtils.copyProperties(postUpdateRequest, post);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        MajorInfo oldMajorInfo = majorInfoService.getById(id);
        ThrowUtils.throwIf(oldMajorInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = majorInfoService.updateById(post);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取 vo
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<MajorInfoVO> getMajorInfoVOById(long id, HttpServletRequest request)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MajorWithDepartmentVO post = majorInfoService.getMajorWithDepartmentById(id);
        if (post == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        MajorInfoVO majorInfoVO = new MajorInfoVO();
//        majorInfoVO.setName(post.get());
        BeanUtils.copyProperties(post, majorInfoVO);
        return ResultUtils.success(majorInfoVO);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<MajorInfoWithDepartmentQueryVO>> listMajorInfoByPage(@RequestBody MajorInfoQueryRequest postQueryRequest)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<MajorInfoWithDepartmentQueryVO> postPage = majorInfoService.listMajorInfoWithDepartment(current, size);
        return ResultUtils.success(postPage);
    }

    // endregion
}
