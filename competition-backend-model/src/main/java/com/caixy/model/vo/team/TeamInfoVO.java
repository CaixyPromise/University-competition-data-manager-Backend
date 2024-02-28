package com.caixy.model.vo.team;

import com.caixy.model.vo.user.UserTeamWorkVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 团队信息详情页的VO
 *
 * @name: com.caixy.model.vo.team.TeamInfoVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-28 17:14
 **/
@Data
public class TeamInfoVO implements Serializable
{
    /**
     * 比赛名称
     */
    private String teamName;
    /**
     * 团队id
     */
    private String teamId;
    /**
     * 比赛参赛id
     */
    private String raceId;
    /**
     * 比赛名称
     */
    private String raceName;
    /**
     * 队长信息
     */
    private UserTeamWorkVO leaderInfo;
    /**
     * 团队成员信息
     */
    private List<UserTeamWorkVO> userList;
    /**
     * 指导老师信息
     */
    private List<UserTeamWorkVO> teacherList;
    /**
     * 团队设定的最大人数
     */
    private Integer teamMaxNum;
    /**
     * 团队当前人数
     */
    private Integer teamCurrentNum;
    /**
     * 比赛规定的最少人数
     */
    private Integer raceMinNum;
    /**
     * 比赛规定的最大人数
     */
    private Integer raceMaxNum;
    /**
     * 是否需要密码
     */
    private Boolean needPassword;
    /**
     * 团队描述
     */
    private String teamDesc;
    /**
     * 团队标签
     */
    private List<String> teamTags;
    /**
     * 团队报名的大项名称
     */
    private String categoryName;
    /**
     * 团队报名的小项名称
     */
    private String eventName;

    /**
     * 报名结束时间
     */
    private Date signUpEndTime;

    /**
     * 比赛级别
     */
    private String matchLevel;

    /**
     * 比赛类型
     */
    private String matchType;

    /**
     * 当前用户是否是队长
     */
    private Boolean isLeader;
    /**
     * 当前用户是否已经在报名状态
     */
    private Boolean isApply;
    private Boolean isMember;
    /**
     * 当前正在申请的用户信息
     */
    private List<UserTeamWorkVO> applyList;

    private static final long serialVersionUID = -1L;
}
