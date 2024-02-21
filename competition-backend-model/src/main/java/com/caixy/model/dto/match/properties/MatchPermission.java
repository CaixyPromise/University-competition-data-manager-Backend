package com.caixy.model.dto.match.properties;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 比赛参数学院权限配置类
 *
 * @name: com.caixy.model.dto.match.properties.MatchPermission
 * @author: CAIXYPROMISE
 * @since: 2024-02-22 00:36
 **/
@Data
public class MatchPermission implements Serializable
{
    private String label;
    private String value;
    private List<MatchPermission> children;
    private static final  long serialVersionUID = -1L;
}
