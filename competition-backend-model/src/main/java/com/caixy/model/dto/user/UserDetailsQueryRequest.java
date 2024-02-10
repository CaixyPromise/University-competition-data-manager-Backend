package com.caixy.model.dto.user;

import com.caixy.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户详细信息分页请求体
 *
 * @name: com.caixy.model.dto.user.UserDetailsQueryRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 01:26
 **/

public class UserDetailsQueryRequest extends PageRequest implements Serializable
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
    private Date createTime;
    private Date updateTime;
    private String departmentName; // 学院名称
    private String majorName;      // 专业名称

    private static final long serialVersionUID = 1L;
}
