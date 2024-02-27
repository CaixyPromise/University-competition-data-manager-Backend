package com.caixy.model.dto.team;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建团队请求体
 *
 * @name: com.caixy.model.dto.team.TeamAddRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 15:23
 **/
@Data
public class TeamAddRequest implements Serializable
{
    /**
     * 报名的比赛id
     */
    private Long matchId;

    /**
     * 比赛的大项id
     */
    private String matchCategoryId;

    /**
     * 比赛的小项id
     */
    private String matchEventId;

    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 团队描述
     */
    private String teamDescription;

    /**
     * 团队最大人数
     */
    private Integer teamMaxSize;

    /**
     * 团队成员Id
     */
    private List<Long> teammates;

    /**
     * 团队指导老师id
     */
    private List<Long> teachers;

    /**
     * 团队密码
     */
    private String teamPassword;

    /**
     * 团队状态
     */
    private Integer teamStatus;

    private static final long serialVersionUID = 1L;

}
