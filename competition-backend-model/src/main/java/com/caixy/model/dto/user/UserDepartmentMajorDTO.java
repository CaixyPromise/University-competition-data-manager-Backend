package com.caixy.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用于在查询用户时同时查询该用户的学院+专业的数据传输对象
 *
 * @name: com.caixy.model.dto.user.UserDepartmentMajorDTO
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 01:10
 **/
@Data
public class UserDepartmentMajorDTO implements Serializable
{
    private Long id;
    private String userAccount;
    private String userEmail;
    private String userPhone;
    private String userName;
//    private String userTags;
    private String userAvatar;
//    private String userProfile;
    private String userRole;
//    private Integer userRoleLevel;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String departmentName; // 学院名称
    private String majorName;      // 专业名称

    private static final long serialVersionUID = 1L;
}
