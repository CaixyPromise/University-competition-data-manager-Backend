package com.caixy.teamservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.model.entity.TeamInfo;
import com.caixy.model.vo.user.UserTeamWorkVO;
import org.apache.ibatis.annotations.Param;

import java.util.Collections;
import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【team_info(报名信息表)】的数据库操作Mapper
 * @createDate 2024-02-06 23:38:52
 * @Entity com.caixy.model.entity.TeamInfo
 */
public interface TeamInfoMapper extends BaseMapper<TeamInfo>
{
    /**
     * 根据团队ID批量查询团队成员信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/28 18:11
     */
    List<UserTeamWorkVO> listUserWorkVOByTeamIds(@Param("teamIds") List<Long> teamIds);

    default List<UserTeamWorkVO> selectUserWorkVOByTeamId(@Param("teamId") Long teamId)
    {
        return listUserWorkVOByTeamIds(Collections.singletonList(teamId));
    }
}




