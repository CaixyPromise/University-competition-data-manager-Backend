package com.caixy.teamservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.model.entity.UserTeam;
import org.apache.ibatis.annotations.Param;

/**
 * @author CAIXYPROMISE
 * @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
 * @createDate 2024-02-26 15:13:49
 * @Entity generator.domain.UserTeam
 */
public interface UserTeamMapper extends BaseMapper<UserTeam>
{
    /**
     * 判断一个用户是否在这个比赛的队伍中
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 00:17
     */
    int checkUserInTeamOrRace(@Param("userId") Long userId,
                              @Param("teamId") Long teamId,
                              @Param("raceId") Long raceId);
}




