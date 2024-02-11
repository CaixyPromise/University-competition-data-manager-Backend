package com.caixy.model.dto.match;

import com.caixy.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 学院信息分页请求体
 *
 * @name: com.caixy.model.dto.department.DepartmentInfoQueryRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 02:00
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchInfoQueryRequest extends PageRequest implements Serializable
{
    /**
     * 比赛名称
     */
    private String matchName;
    private static final long serialVersionUID = 1L;
}
