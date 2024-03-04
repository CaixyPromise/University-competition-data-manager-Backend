package com.caixy.model.dto.market;

import lombok.Data;

import java.io.Serializable;

/**
 * 需求状态变更请求
 *
 * @name: com.caixy.model.dto.market.UpdateDemandStatusRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-04 00:00
 **/
@Data
public class UpdateDemandStatusRequest implements Serializable
{
    /**
     * id
     */
    private Long id;

    private Long targetUser;

    private static final long serialVersionUID = 1L;
}
