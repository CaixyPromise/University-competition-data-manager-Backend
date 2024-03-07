package com.caixy.model.dto.user;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserAddRequest implements Serializable
{

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 学院id
     */
    private Long userDepartment;
    /**
     * 用户身份
     */
    private Integer userRole;

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

    private static final long serialVersionUID = 1L;
}