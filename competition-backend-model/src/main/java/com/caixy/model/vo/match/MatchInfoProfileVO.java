package com.caixy.model.vo.match;

import com.caixy.model.dto.match.properties.GroupDataItem;
import com.caixy.model.dto.match.properties.MatchAward;
import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 比赛详细信息，用于比赛详情页
 *
 * @name: com.caixy.model.vo.match.MatchInfoProfileVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-24 00:28
 **/
@Data
public class MatchInfoProfileVO implements Serializable
{
    /**
     * 比赛id
     */
    private Long id;

    /**
     * 比赛名称
     */
    private String matchName;

    /**
     * 比赛描述
     */
    private String matchDesc;

    /**
     * 比赛状态: 0-报名中; 1-已开始; 2-已结束;
     */
    private Integer matchStatus;

    /**
     * 比赛宣传图片(logo)
     */
    private String matchPic;

    /**
     * json
     * 比赛类型: A类, B类, C类
     */
    private String matchType;

    /**
     * 比赛等级: 国家级, 省级
     */
    private String matchLevel;

    /**
     * 比赛规则
     */
    private String matchRule;

    /**
     * 比赛所允许的分组(学院/部门): 默认为全部学院/专业专业可以参加 json
     */
    private HashMap<Long, HashMap<String, String>> matchPermissionRule;


    /**
     * 比赛标签 json
     */
    private List<String> matchTags;


    /**
     * 比赛奖品 json
     */
    private List<MatchAward> matchAward;

    /**
     * 最大团队人数
     */

    private Integer maxTeamSize;
    /**
     * 最小团队人数
     */
    private Integer minTeamSize;
    /**
     * 最大团队指导老师人数
     */
    private Integer maxTeacherSize;
    /**
     * 最小团队指导老师人数
     */
    private Integer minTeacherSize;
    /**
     * 比赛开始时间
     */
    private Date startTime;

    /**
     * 比赛结束时间
     */
    private Date endTime;

    /**
     * 报名开始日期
     */
    private Date signUpStartTime;

    /**
     * 报名截止时间
     */
    private Date signUpEndTime;

    /**
     * 比赛创建人信息
     */
    private UserWorkVO createUserInfo;

    /**
     * 分组信息
     */
    private List<GroupDataItem> groupData;


    private static final long serialVersionUID = 1L;
}
