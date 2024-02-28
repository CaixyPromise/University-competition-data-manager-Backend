package com.caixy.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户在团队内的信息VO
 *
 * @name: com.caixy.model.vo.user.UserTeamWorkVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-28 17:58
 **/
@Data
public class UserTeamWorkVO implements Serializable
{
    String userDepartment;
    String userMajor;
    String userName;
    String userAccount;
    String userEmail;
    String teamId;
    Integer teamUserRole;
    private static final long serialVersionUID = 1L;
}
