package com.caixy.teamservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.dto.team.*;
import com.caixy.model.entity.TeamInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.team.TeamInfoPageVO;
import com.caixy.model.vo.team.TeamInfoVO;
import com.caixy.model.vo.team.TeamUserVO;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【team_info(报名信息表)】的数据库操作Service
 * @createDate 2024-02-06 23:38:52
 */
public interface TeamInfoService extends IService<TeamInfo>
{
    /**
     * 创建队伍
     *
     * @param teamAddRequest
     * @param loginUser
     * @return
     */
    long addTeam(TeamAddRequest teamAddRequest, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
    /**
     * 删除（解散）队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);

    Page<TeamInfoPageVO> listByPage(TeamQuery teamQuery, boolean isAdmin);

    TeamInfoVO getTeamInfoById(Long teamId, User loginUser, boolean needRole);
}
