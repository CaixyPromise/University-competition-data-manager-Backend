package com.caixy.model.vo.registration;

import lombok.Data;

import java.io.Serializable;

/**
 * 报名比赛队伍信息
 *
 * @name: com.caixy.model.vo.registration.RegistrationInfoVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 03:44
 **/
@Data
public class RegistrationInfoVO implements Serializable
{
    /**
     * 团队报名的大项名称
     */
    private String categoryName;
    /**
     * 团队报名的小项名称
     */
    private String eventName;

    private static final long serialVersionUID = 1L;
}
