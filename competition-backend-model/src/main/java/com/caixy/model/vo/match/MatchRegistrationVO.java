package com.caixy.model.vo.match;

import com.caixy.common.utils.JsonUtils;
import com.caixy.model.dto.match.properties.GroupDataItem;
import com.caixy.model.entity.MatchInfo;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 比赛注册信息参数
 *
 * @name: com.caixy.model.vo.match.MatchRegistrationVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 17:25
 **/
@Data
public class MatchRegistrationVO implements Serializable
{
    private String id;
    private String matchName;

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
     * 分组信息
     */
    private List<GroupDataItem> groupData;

    public static MatchRegistrationVO EntityToVO(MatchInfo matchInfo)
    {
        MatchRegistrationVO vo = new MatchRegistrationVO();
        vo.setId(String.valueOf(matchInfo.getId()));
        vo.setMatchName(matchInfo.getMatchName());

        List<Double> teamSize = JsonUtils.jsonToList(matchInfo.getTeamSize());
        vo.setMinTeamSize(teamSize.get(0).intValue());
        vo.setMaxTeamSize(teamSize.get(1).intValue());
        List<Double> teacherSize = JsonUtils.jsonToList(matchInfo.getTeacherSize());
        vo.setMinTeacherSize(teacherSize.get(0).intValue());
        vo.setMaxTeacherSize(teacherSize.get(1).intValue());
        List<GroupDataItem> groupData = JsonUtils.jsonToObject(matchInfo.getMatchGroup(),
                new TypeToken<List<GroupDataItem>>(){}.getType());
        vo.setGroupData(groupData);
        return vo;
    }

    private static final long serialVersionUID = -1L;
}
