package com.caixy.teamservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.entity.UserTeam;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【user_team(用户队伍关系)】的数据库操作Service
 * @createDate 2024-02-26 15:13:49
 */
public interface UserTeamService extends IService<UserTeam>
{
    boolean checkIsJoin(Long userId, Long teamId);

    boolean batchCheckIsJoin(List<Long> userId, Long raceId);
}
