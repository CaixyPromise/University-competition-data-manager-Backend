package com.caixy.model.vo.major;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @name: com.caixy.model.vo.major.MajorInfoVO
 * @decription: MajorInfo
 * @author: CAIXYPROMISE
 * @since: 2024-02-10 23:56
 **/
@Data
public class MajorInfoVO implements Serializable
{
    private String name;
    private String departmentName;
    private static final long serialVersionUID = 1L;
}
