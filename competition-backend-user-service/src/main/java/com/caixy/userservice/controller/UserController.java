package com.caixy.userservice.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.annotation.AuthCheck;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.model.dto.user.*;
import com.caixy.model.entity.User;
import com.caixy.model.enums.UserRoleEnum;
import com.caixy.model.vo.department.UserDepartmentMajorVO;
import com.caixy.model.vo.user.AboutMeVO;
import com.caixy.model.vo.user.LoginUserVO;
import com.caixy.model.vo.user.SearchUserVO;
import com.caixy.model.vo.user.UserVO;
import com.caixy.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/")
@Slf4j
public class UserController
{
    @Resource
    private UserService userService;

    @Resource
    private RedisOperatorService redisOperatorService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest)
    {
        if (userRegisterRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request)
    {
        if (userLoginRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUserVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(userService.getLoginUserVO(loginUserVO));
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request)
    {
        if (request == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request)
    {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request)
    {
        // 检查请求信息
        if (userAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 用户账号不能为空（学号/工号）
        if (userAddRequest.getUserAccount() == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不能为空");
        }
        // 检查身份合法性
        final UserRoleEnum userRoleCode = UserRoleEnum.getEnumByCode(userAddRequest.getUserRole());
        if (userRoleCode == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户身份不合法");
        }
        user.setUserRole(userRoleCode.getValue());
        // 1. 管理员添加用户信息时，使用默认默认密码 as123 + account
        String defaultPassword = "as123" + userAddRequest.getUserAccount() + "..";
        user.setUserPassword(defaultPassword);
        // 2. 校验用户信息合法性
//        userService.validateUserInfo(user);
        // 3. 创建
        Long resultId = userService.makeRegister(user);
        return ResultUtils.success(resultId);
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request)
    {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request)
    {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户信息, 返回数据如下: {@link UserDepartmentMajorVO}
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/10 01:23
     */
    @PostMapping("/list/page/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserDepartmentMajorVO>> listUserDetailsByPage(@RequestBody
                                                                           UserDetailsQueryRequest userDetailsQueryRequest,
                                                                           HttpServletRequest request)
    {
        long current = userDetailsQueryRequest.getCurrent();
        long size = userDetailsQueryRequest.getPageSize();
        // 创建Page对象
        Page<UserDepartmentMajorVO> listResult = userService.listUserWithDepartmentMajor(current, size);
        return ResultUtils.success(listResult);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
//    @Deprecated
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody
                                                   UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request)
    {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        List<User> userList =
                userPage.getRecords().stream().peek(user -> user.setUserPassword(null)).collect(Collectors.toList());

        userPage.setRecords(userList);
        return ResultUtils.success(userPage);
    }


    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
//    @Deprecated
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody
                                                       UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request)
    {
        if (userQueryRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    @PostMapping("/search/team/user")
    public BaseResponse<List<SearchUserVO>> searchUserByUserNameAndAccount(@RequestBody UserSearchRequest payload,
                                                                           HttpServletRequest request)
    {
        // 需要登录才可以查询
        userService.getLoginUser(request);
        if (payload == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        List<SearchUserVO> searchUserVOS = userService.listSearchUserVO(payload)
                .stream().peek(searchUserVO ->
                {
                    HashMap<String, String> majorAndDepartMap =
                            redisOperatorService.getHash(RedisConstant.ACADEMY_MAJOR,
                                    searchUserVO.getUserDepartment(),
                                    String.class, String.class);
                    String departmentName = majorAndDepartMap.get("_name").replaceAll("^\"|\"$", "");
                    String majorName = majorAndDepartMap.get(searchUserVO.getUserMajor()).replaceAll("^\"|\"$", "");
                    searchUserVO.setUserDepartment(departmentName);
                    searchUserVO.setUserMajor(majorName);
                }).collect(Collectors.toList());
        return ResultUtils.success(searchUserVOS);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody
                                              UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request)
    {
        if (userUpdateMyRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get/me")
    public BaseResponse<AboutMeVO> getMeByRequest(HttpServletRequest request)
    {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getAboutMe(loginUser.getId()));
    }

    @GetMapping("add/wallet")
    public BaseResponse<Boolean> updateWallet(@RequestParam("add") Long addMoney, HttpServletRequest request)
    {
        User loginUser = userService.getLoginUser(request);
        Boolean updateWallet = userService.updateWallet(addMoney, loginUser.getId());

        return ResultUtils.success(updateWallet);
    }
}
