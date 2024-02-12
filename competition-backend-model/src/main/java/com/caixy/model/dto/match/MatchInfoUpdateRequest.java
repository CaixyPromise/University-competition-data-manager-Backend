package com.caixy.model.dto.match;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 比赛信息更新请求体
 *
 * @name: com.caixy.model.dto.match.MatchInfoUpdateRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-13 01:10
 **/
@Data
public class MatchInfoUpdateRequest implements Serializable
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
    private MatchInfoAddRequest.MatchPermissionRule matchPermissionRule;

    @Data
    public static class MatchPermissionRule
    {
        private Map<Long, List<Long>> permissions;
    }


    /**
     * 比赛标签 json
     * expect:
     * {
     * "tagName_1": "tagDesc_1",
     * "tagName_2": "tagDesc_2"
     * }
     */
    private Map<String, String> matchTags;


    /**
     * 比赛奖品 json
     * expect:
     * {
     * "awardName_1": "awardDesc_1",
     * "awardName_2": "awardDesc_2"
     * }
     */
    private Map<String, String> matchAward;

    /**
     * 比赛团队大小
     */
    private Integer teamSize;

    /**
     * 比赛开始时间
     */
    private Date startTime;

    /**
     * 比赛结束时间
     */
    private Date endTime;

    private static final long serialVersionUID = 1L;
}
