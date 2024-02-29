package com.caixy.model.dto.team;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 审批用户入队请求
 *
 * @name: com.caixy.model.dto.team.ResolveAndRejectRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-29 04:15
 **/
@Data
public class ResolveAndRejectRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @NotNull
    private Long teamId;
    /**
     * 比赛id
     */
    @NotNull
    private Long raceId;
    /**
     * 比赛id
     */
    @NotNull
    @NotEmpty
    private String userAccount;
}
