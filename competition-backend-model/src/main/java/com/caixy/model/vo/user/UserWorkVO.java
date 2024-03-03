package com.caixy.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户专业+学院工作信息
 *
 * @name: com.caixy.model.vo.user.UserWorkVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-23 21:53
 **/
@Data
public class UserWorkVO implements Serializable
{
    String userDepartment;
    String userMajor;
    String userName;
    String userAccount;
    String userEmail;
    Long userId;

    private static final long serialVersionUID = 1L;
}
