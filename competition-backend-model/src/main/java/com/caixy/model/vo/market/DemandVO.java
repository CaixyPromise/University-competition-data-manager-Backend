package com.caixy.model.vo.market;

import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 需求分页详细信息VO
 *
 * @name: com.caixy.model.vo.market.DemandVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 18:21
 **/
@Data
public class DemandVO implements Serializable
{
    /**
     * id
     */
    private Long id;

    /**
     * 需求标题
     */
    private String title;

    /**
     * 需求描述
     */
    private String description;

    /**
     * 需求状态
     */
    private Integer status;

    /**
     * 报酬
     */
    private BigDecimal reward;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 截止日期
     */
    private Date deadline;

    private UserWorkVO creator;

    private static final long serialVersionUID = 1L;
}
