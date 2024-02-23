package com.caixy.model.dto.match;

import com.caixy.model.dto.match.properties.MatchAward;
import com.caixy.model.dto.match.properties.MatchPermission;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 比赛信息添加包装类
 *
 * @name: com.caixy.model.dto.match.MatchInfoAddRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-11 14:01
 * @version: 2.0
 * @lastUpdate: 2024-02-20
 **/
@Data
public class MatchInfoAddRequest implements Serializable
{
    /**
     * 比赛名称
     */
    @NotBlank
    @Size(max = 80, min = 1)
    private String matchName;

    /**
     * 比赛描述
     */
    @NotBlank
    @Size(max = 1024, min = 20)
    private String matchDesc;

    /**
     * 比赛状态: 0-报名中; 1-开始报名; 2-已开始; 3-已结束;
     */
    @NotNull
    @Max(4)
    @Min(0)
    private Integer matchStatus;

    /**
     * json
     * 比赛类型: A类, B类, C类
     */
    @NotBlank
    @Size(max = 3)
    private String matchType;

    /**
     * 比赛等级: 国家级, 省级
     */
    @NotBlank
    @Size(max = 5)
    private String matchLevel;

    /**
     * 比赛规则
     */
    @Size(max = 1024, min = 20)
    private String matchRule;

    /**
     * 比赛所允许的分组(学院/部门): 默认为全部学院/专业专业可以参加 json
     * expect:
     * {
     * "departmentId_1": [...majorId_1, major_2],
     * "departmentId_2": [...majorId_1, major_2]
     * }
     */
    // 调整为嵌套列表以匹配前端结构
    @NotEmpty
    private List<List<MatchPermission>> matchPermissionRule;


    @NotEmpty
    @Size(max = 2)
    private List<Date> signupDate; // [开始日期, 结束日期]

    @NotEmpty
    @Size(max = 2)
    private List<Date> matchDate; // [开始日期, 结束日期]

    /**
     * 比赛标签 json
     */
    @Size(max = 10)
    private List<String> matchTags;


    /**
     * 比赛奖品 json
     * expect:
     * {
     * "awardName_1": "awardDesc_1",
     * "awardName_2": "awardDesc_2"
     * }
     */
    @Size(min = 1, max = 100)
    private List<MatchAward> matchAward;

    /**
     * 最大团队人数
     */
    @Min(2)
    @Max(100)
    private Integer maxTeamSize;
    /**
     * 最小团队人数
     */
    @Min(1)
    @Max(100)
    private Integer minTeamSize;

    /**
     * 是否需要提交附件文件列表
     */
    private List<String> fileList;




    private static final long serialVersionUID = 1L;
}
