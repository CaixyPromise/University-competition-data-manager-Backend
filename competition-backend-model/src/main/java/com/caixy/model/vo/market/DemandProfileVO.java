package com.caixy.model.vo.market;

import com.caixy.model.vo.market.properties.TakerProfileVO;
import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 需求详细信息VO
 *
 * @name: com.caixy.model.vo.market.DemandProfileVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-04 16:15
 **/
@Data
public class DemandProfileVO implements Serializable
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

    /**
     * 创建人信息
     */
    private UserWorkVO creator;

    /**
     * 请求用户是否是这个需求的所有者
     */
    private Boolean isOwner;

    /**
     * 如果是需求创建人，则返回申请人信息
     */
    private List<TakerProfileVO> userList;

    /**
     * 是否已经申请
     */
    private Boolean isApplied;

    private static final long serialVersionUID = 1L;
}
