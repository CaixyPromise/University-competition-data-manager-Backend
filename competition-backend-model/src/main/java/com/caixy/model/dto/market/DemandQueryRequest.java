package com.caixy.model.dto.market;

import com.caixy.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @name: com.caixy.model.dto.market.DemandQueryRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 18:47
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class DemandQueryRequest extends PageRequest implements Serializable
{
    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String description;


    /**
     * 创建用户 id
     */
    private Long creatorId;

    private Integer status;

    private static final long serialVersionUID = 1L;
}
