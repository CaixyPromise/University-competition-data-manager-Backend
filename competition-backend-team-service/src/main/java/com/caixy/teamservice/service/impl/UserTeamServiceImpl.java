package com.caixy.teamservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.model.entity.UserTeam;
import com.caixy.model.enums.team.TeamRoleEnum;
import com.caixy.teamservice.mapper.UserTeamMapper;
import com.caixy.teamservice.service.UserTeamService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-02-26 15:13:49
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService
{
    /**
     * 检查当前用户是否已经加入或创建过这个比赛的其他队伍了
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/27 22:31
     */
    @Override
    public boolean checkIsJoin(Long userId, Long raceId)
    {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("raceId", raceId);
        // 排除掉审核中或拒绝的队伍
        queryWrapper.notIn("userRole", TeamRoleEnum.REJECT.getCode(), TeamRoleEnum.APPLYING.getCode());
        return this.count(queryWrapper) > 0;
    }

    /**
     * 检查队员是否已经加入其他队伍
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/27 22:37
     */
    @Override
    public boolean batchCheckIsJoin(List<Long> userId, Long raceId)
    {
       QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("raceId", raceId);
       queryWrapper.notIn("userId", userId);
       return this.count(queryWrapper) > 0;
    }

}




