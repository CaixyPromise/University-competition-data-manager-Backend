package com.caixy.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @name: com.caixy.model.dto.team.TeamQuitRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 15:26
 **/
@Data
public class TeamQuitRequest implements Serializable
{
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long teamId;

}
