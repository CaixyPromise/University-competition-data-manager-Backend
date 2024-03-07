package com.caixy.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data
public class UserRegisterRequest implements Serializable
{

    private static final long serialVersionUID = -1L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 学院id
     */
    private Long userDepartment;
    /**
     * 专业id
     */
    private Long userMajor;
    /**
     * 用户性别：1-女；2-男；0；未知
     */
    private Integer userSex;
    /**
     * 用户邮箱
     */
    private String userEmail;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户确认的密码
     */
    private String checkPassword;
}
