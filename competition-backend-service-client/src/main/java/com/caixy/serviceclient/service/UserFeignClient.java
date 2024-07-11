package com.caixy.serviceclient.service;

import com.caixy.common.common.ErrorCode;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.model.dto.department.DepartAndMajorValidationResponse;
import com.caixy.model.entity.User;
import com.caixy.model.entity.UserWallet;
import com.caixy.model.enums.UserRoleEnum;
import com.caixy.model.vo.user.UserVO;
import com.caixy.model.vo.user.UserWorkVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户接口远程调用类
 *
 * @name: com.caixy.serviceclient.service.UserFeignClient
 * @author: CAIXYPROMISE
 * @since: 2024-02-07 00:16
 **/
@FeignClient(name = "competition-backend-user-service",
            path = "/api/user/inner")
public interface UserFeignClient
{


    @PostMapping("/update/departemtCache")
    void getDepartmentInfoByIdsAndUpdateCache();

    /**
     * 判断列表内的用户id是否合法且存在
     *
     * @author CAIXYPROMISE
     * @since 2024/2/27 22:01
     * @version 1.0
     */
    @PostMapping("/validate/users")
    Boolean validateUsers(@RequestBody List<Long> userIds);

    /**
     * 批量查询学院与专业的是否合法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/12 00:54
     */
    @PostMapping("/validate/departments-and-majors")
    DepartAndMajorValidationResponse validateDepartmentsAndMajors(
            @RequestBody Map<Long, List<Long>> permissions);

    /**
     * 根据 id 获取用户
     *
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);

    /**
     * 根据 学号/工号 获取用户
     *
     * @param userAccount
     * @return
     */
    @GetMapping("/get/account")
    User getByAccount(@RequestParam("userId") String userAccount);

    @GetMapping("/get/account/list")
    List<User> listUserByAccount(@RequestBody List<String> userAccount);

    @GetMapping("/get/id/workVO")
    UserWorkVO getUserWorkVO(@RequestParam("userId") Long userId);

    @PostMapping("/get/ids/workVO")
    List<UserWorkVO> getUserWorksByIds(@RequestBody List<Long> userIds);

    /**
     * 根据 id 获取用户列表
     *
     * @param idList
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request)
    {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 可以考虑在这里做全局权限校验
        return currentUser;
    }

    /**
     * 获取当前登录用户，如果需要检查登录且未登录则抛出异常
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/23 19:02
     */
    default User getLoginUser(HttpServletRequest request, boolean check)
    {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (check && currentUser == null || currentUser.getId() == null)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 可以考虑在这里做全局权限校验
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user)
    {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user)
    {
        if (user == null)
        {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @GetMapping("/get/wallet")
    UserWallet getUserWallet(@RequestParam("userId") Long userId);

    @PostMapping("/get/both/wallet")
    Map<Long, UserWallet> getBothUserWallet(@RequestBody List<Long> userIds);

    @PostMapping("update/wallet")
    Boolean updateUserWallet(@RequestBody UserWallet userWallet);
}