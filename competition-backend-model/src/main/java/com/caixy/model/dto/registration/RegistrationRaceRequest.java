package com.caixy.model.dto.registration;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 报名比赛请求
 *
 * @name: com.caixy.model.dto.registration.RegistrationRaceRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-29 16:56
 **/
@Data
public class RegistrationRaceRequest implements Serializable
{
    /**
     * 报名的团队id
     */
    @NotNull
    private Long teamId;

    /**
     * 报名的比赛id
     */
    @NotNull
    private Long raceId;
    private static final long serialVersionUID = -1L;
}
