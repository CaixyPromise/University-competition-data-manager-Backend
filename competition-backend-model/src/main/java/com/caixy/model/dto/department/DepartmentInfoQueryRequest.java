package com.caixy.model.dto.department;

import com.caixy.common.common.PageRequest;

import java.io.Serializable;
import java.util.Date;

/**
 * 学院信息分页请求体
 *
 * @name: com.caixy.model.dto.department.DepartmentInfoQueryRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 02:00
 **/
public class DepartmentInfoQueryRequest extends PageRequest implements Serializable
{
    /**
     * 学院名称
     */
    private String name;
    /**
     * 添加人id
     * */
    private Long addUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
