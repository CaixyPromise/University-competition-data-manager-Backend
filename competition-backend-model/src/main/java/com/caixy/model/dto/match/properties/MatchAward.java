package com.caixy.model.dto.match.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 比赛奖项设置描述实体类
 *
 * @name: com.caixy.model.dto.match.properties.MatchAward
 * @author: CAIXYPROMISE
 * @since: 2024-02-22 00:35
 **/
@Data
public class MatchAward implements Serializable
{
    private String awardName;
    private String awardContent;
    private String awardDesc;
    private Integer awardCount;
    private static final long serialVersionUID = -1L;
}
