package com.caixy.model.vo.team;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 团队信息VO
 *
 * @name: com.caixy.model.vo.team.TeamInfoPageVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-28 01:56
 **/
@Data
public class TeamInfoPageVO implements Serializable
{
    private Long id;
    /**
     * 比赛id
     */
    private Long raceId;

    private String raceName;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数：创建时设置，避免满了还被申请加入
     */
    private Integer maxNum;

    /**
     * 团队标签
     */
    private List<String> teamTags;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer isPublic;

    /**
     * 0 - 团队组建中; 1 - 报名成功; 2 - 已解散
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否需要密码
     */
    private Boolean needPassword;

    /**
     * 当前队员人数
     */
    private Integer currentUserNum;


    private static final long serialVersionUID = 1L;
}
