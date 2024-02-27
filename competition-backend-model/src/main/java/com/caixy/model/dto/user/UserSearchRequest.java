package com.caixy.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建队伍时的请求体
 *
 * @name: com.caixy.model.dto.user.UserSearchRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 21:34
 **/
@Data
public class UserSearchRequest implements Serializable
{
    /**
     * 用户名称
     */
    private String useKeyword;

    /**
     * 用户受限的部门/院系id(学院) 列表
     */
    private List<Long> userPermissionIds;

    /**
     * 用户角色
     */
    private String userRole;
    private static final long serialVersionUID = 1L;
}
