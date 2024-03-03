package com.caixy.teamservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.exception.BusinessException;
import com.caixy.model.dto.team.*;
import com.caixy.model.entity.TeamInfo;
import com.caixy.model.entity.User;
import com.caixy.model.entity.UserTeam;
import com.caixy.model.enums.team.TeamRoleEnum;
import com.caixy.model.vo.team.TeamInfoPageVO;
import com.caixy.model.vo.team.TeamInfoVO;
import com.caixy.model.vo.team.TeamUserVO;
import com.caixy.serviceclient.service.UserFeignClient;
import com.caixy.teamservice.service.TeamInfoService;
import com.caixy.teamservice.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 队伍信息接口控制器
 *
 * @name: com.caixy.teamservice.controller.TeamController
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 15:20
 **/
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController
{
    @Resource
    private TeamInfoService teamService;

    @Resource
    private UserFeignClient userService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request)
    {
        if (teamAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long teamId = teamService.addTeam(teamAddRequest, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request)
    {
        if (teamUpdateRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request)
    {
        if (teamQuery == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try
        {
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        }
        catch (Exception e)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "服务端异常");
        }
        // 3、查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 队伍 id => 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList =
                userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

    // todo 查询分页
    @GetMapping("/list/page")
    public BaseResponse<Page<TeamInfoPageVO>> listTeamsByPage(TeamQuery teamQuery)
    {
        if (teamQuery == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        return ResultUtils.success(teamService.listByPage(teamQuery, true));
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request)
    {
        if (teamJoinRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/join/resolve")
    public BaseResponse<Boolean> handleResolveJoinTeam(@RequestBody ResolveAndRejectRequest teamJoinRequest,
                                                       HttpServletRequest request)
    {
        if (teamJoinRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.resolveJoinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/join/reject")
    public BaseResponse<Boolean> handleRejectJoinTeam(@RequestBody
                                                      @Valid
                                                      ResolveAndRejectRequest teamJoinRequest,
                                                      HttpServletRequest request)
    {
        if (teamJoinRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.rejectJoinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }


    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody
                                          @Valid
                                          TeamQuitRequest teamQuitRequest,
                                          HttpServletRequest request)
    {
        if (teamQuitRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamInfoVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request)
    {
        if (teamQuery == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        QueryWrapper<TeamInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<TeamInfo> list = teamService.list(queryWrapper);
        // 把list里的id提取出来，并去除重复的
        List<Long> teamIdList = list.stream().map(TeamInfo::getId).distinct().collect(Collectors.toList());
        if (teamIdList.isEmpty())
        {
            return ResultUtils.success(Collections.emptyList());
        }
        List<TeamInfoVO> infoVOByIds = teamService.getTeamInfoVOByIds(teamIdList);
        return ResultUtils.success(infoVOByIds);
    }


    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamInfoVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request)
    {
        if (teamQuery == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        if (teamQuery.getUserRole() != null)
        {
            TeamRoleEnum roleEnum = TeamRoleEnum.getEnumByCode(teamQuery.getUserRole());
            if (roleEnum == null)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户角色不合法");
            }
            queryWrapper.eq("userRole", roleEnum.getCode());
        }
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        if (idList.isEmpty())
        {
            return ResultUtils.success(Collections.emptyList());
        }
        List<TeamInfoVO> teamInfoVOByIds = teamService.getTeamInfoVOByIds(idList);
        return ResultUtils.success(teamInfoVOByIds);
    }

    @GetMapping("/get/info")
    public BaseResponse<TeamInfoVO> getTeamById(@RequestParam("teamId") Long teamId, HttpServletRequest request)
    {
        if (teamId == null || teamId < 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.getTeamAndRaceInfoById(teamId, loginUser, true, false));
    }


}
