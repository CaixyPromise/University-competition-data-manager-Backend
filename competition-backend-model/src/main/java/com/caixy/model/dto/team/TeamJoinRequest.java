package com.caixy.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * 加入团队请求体
 *
 * @name: com.caixy.model.dto.team.TeamJoinRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 15:25
 **/
@Data
public class TeamJoinRequest implements Serializable
{

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}