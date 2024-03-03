package com.caixy.model.vo.match;

import com.caixy.model.entity.MatchInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 我创建的比赛VO信息
 *
 * @name: com.caixy.model.vo.match.MyCreateRaceVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 04:02
 **/
@Data
public class MyCreateRaceVO implements Serializable
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
     * 比赛状态: 0-报名中; 1-已开始; 2-已结束;
     */
    private Integer matchStatus;

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
     * 目前报名团队数量
     */
    private Integer hasRegistrationNum;


    public static MyCreateRaceVO of(MatchInfo matchInfo)
    {
        MyCreateRaceVO myCreateRaceVO = new MyCreateRaceVO();
        BeanUtils.copyProperties(matchInfo, myCreateRaceVO);

        return myCreateRaceVO;
    }

    private static final long serialVersionUID = 1L;

}
