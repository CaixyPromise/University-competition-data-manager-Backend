package com.caixy.model.vo.match;

import com.caixy.common.utils.JsonUtils;
import com.caixy.model.dto.match.properties.MatchAward;
import com.caixy.model.entity.MatchInfo;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 比赛信息分页查询VO
 *
 * @name: com.caixy.model.vo.match.MatchInfoQueryVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-12 01:58
 **/
@Data
public class MatchInfoQueryVO implements Serializable
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
     * expect:
     * {
     * "departmentId_1": [...majorId_1, major_2],
     * "departmentId_2": [...majorId_1, major_2]
     * }
     */
    private HashMap<Long, List<Long>> matchPermissionRule;


    /**
     * 比赛标签 json
     * expect:
     * {
     * "tagName_1": "tagDesc_1",
     * "tagName_2": "tagDesc_2"
     * }
     */
    private List<String> matchTags;


    /**
     * 比赛奖品 json
     * expect:
     * {
     * "awardName_1": "awardDesc_1",
     * "awardName_2": "awardDesc_2"
     * }
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



    public static MatchInfoQueryVO convertToAdminVO(MatchInfo matchInfo)
    {
        MatchInfoQueryVO vo = new MatchInfoQueryVO();
        vo.setId(matchInfo.getId());
        vo.setMatchName(matchInfo.getMatchName());
//        vo.setMatchDesc(matchInfo.getMatchDesc());
        vo.setMatchStatus(matchInfo.getMatchStatus());
        vo.setMatchPic(matchInfo.getMatchPic());
        vo.setMatchType(matchInfo.getMatchType());
        vo.setMatchLevel(matchInfo.getMatchLevel());
        vo.setMatchRule(matchInfo.getMatchRule());
//        vo.setTeamSize(matchInfo.getTeamSize());
        vo.setStartTime(matchInfo.getStartTime());
        vo.setEndTime(matchInfo.getEndTime());
        vo.setSignUpStartTime(matchInfo.getSignUpStartTime());
        vo.setSignUpEndTime(matchInfo.getSignUpEndTime());
        // 使用 jsonToObject 方法转换 JSON 字符串到指定类型

        vo.setMatchPermissionRule(JsonUtils.jsonToObject(matchInfo.getMatchPermissionRule(),
                new TypeToken<HashMap<Long, List<Long>>>()
                {
                }.getType()));

        List<String> tags = JsonUtils.jsonToList(matchInfo.getMatchTags());
        vo.setMatchTags(tags);

        List<MatchAward> award = JsonUtils.jsonToList(matchInfo.getMatchAward());
        vo.setMatchAward(award);
        return vo;
    }


    public static MatchInfoQueryVO convertToVO(MatchInfo matchInfo)
    {
        MatchInfoQueryVO vo = new MatchInfoQueryVO();
        vo.setId(matchInfo.getId());
        vo.setMatchName(matchInfo.getMatchName());
        vo.setMatchDesc(matchInfo.getMatchDesc());
        vo.setMatchStatus(matchInfo.getMatchStatus());
        vo.setMatchPic(matchInfo.getMatchPic());
        vo.setMatchType(matchInfo.getMatchType());
        vo.setMatchLevel(matchInfo.getMatchLevel());
        vo.setMaxTeamSize(matchInfo.getMaxTeamSize());
        vo.setMinTeamSize(matchInfo.getMinTeamSize());
        vo.setStartTime(matchInfo.getStartTime());
        vo.setEndTime(matchInfo.getEndTime());
        vo.setSignUpStartTime(matchInfo.getSignUpStartTime());
        vo.setSignUpEndTime(matchInfo.getSignUpEndTime());
        // 使用 jsonToObject 方法转换 JSON 字符串到指定类型

        vo.setMatchPermissionRule(JsonUtils.jsonToObject(matchInfo.getMatchPermissionRule(),
                new TypeToken<HashMap<Long, List<Long>>>()
                {
                }.getType()));

        List<String> tags = JsonUtils.jsonToList(matchInfo.getMatchTags());
        vo.setMatchTags(tags);

//        List<MatchAward> award = JsonUtils.jsonToList(matchInfo.getMatchAward());
//        vo.setMatchAward(award);
        return vo;
    }

    private static final long serialVersionUID = 1L;
}
