package com.caixy.model.vo.department;

import lombok.Data;

import java.io.Serializable;

/**
 * 学院信息VO
 *
 * @name: com.caixy.model.vo.DepartmentInfoVO.DepartmentInfoVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 01:56
 **/
@Data
public class DepartmentInfoVO implements Serializable
{
    private String name;
    private static final long serialVersionUID = 1L;
}
