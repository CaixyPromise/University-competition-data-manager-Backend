package com.caixy.model.dto.market;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发布需求请求封装类
 *
 * @name: com.caixy.model.dto.market.DemandAddRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 18:02
 **/
@Data
public class DemandAddRequest implements Serializable
{
    /**
     * 需求标题
     */
    private String title;

    /**
     * 需求描述
     */
    private String description;

    /**
     * 报酬
     */
    private BigDecimal reward;

    /**
     * 截止日期
     */
    private Date deadline;

    private static final long serialVersionUID = 1L;

}
