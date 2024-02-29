package com.caixy.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.dto.user.UserSearchRequest;
import com.caixy.model.vo.department.UserDepartmentMajorVO;
import com.caixy.model.dto.user.UserLoginRequest;
import com.caixy.model.dto.user.UserQueryRequest;
import com.caixy.model.entity.User;
import com.caixy.model.vo.user.LoginUserVO;
import com.caixy.model.vo.user.SearchUserVO;
import com.caixy.model.vo.user.UserVO;
import com.caixy.model.vo.user.UserWorkVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-02-06 23:22:54
 */
public interface UserService extends IService<User>
{
    /**
     * 批量校验用户Account是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/27 22:02
     */
    Boolean validateUserByIds(List<Long> userIds);

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户请求信息
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    Long makeRegister(String userAccount, String userPassword);

    Long makeRegister(User user);

    Page<UserDepartmentMajorVO> listUserWithDepartmentMajor(long current, long size);

    UserWorkVO getUserWorkVO(long userId);

    List<SearchUserVO> listSearchUserVO(UserSearchRequest payload);

    User getByAccount(String userAccount);

    List<User> getByAccounts(List<String> userAccount);
}
