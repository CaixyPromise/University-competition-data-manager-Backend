package com.caixy.teamservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.utils.EncryptionUtils;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.model.dto.team.*;
import com.caixy.model.entity.TeamInfo;
import com.caixy.model.entity.User;
import com.caixy.model.entity.UserTeam;
import com.caixy.model.enums.TeamStatusEnum;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.model.vo.team.TeamUserVO;
import com.caixy.model.vo.user.UserVO;
import com.caixy.serviceclient.service.CompetitionFeignClient;
import com.caixy.serviceclient.service.UserFeignClient;
import com.caixy.teamservice.mapper.TeamInfoMapper;
import com.caixy.teamservice.service.TeamInfoService;
import com.caixy.teamservice.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author CAIXYPROMISE
 * @description 针对表【team_info(报名信息表)】的数据库操作Service实现
 * @createDate 2024-02-06 23:38:52
 */
@Service
@Slf4j
public class TeamInfoServiceImpl extends ServiceImpl<TeamInfoMapper, TeamInfo>
        implements TeamInfoService
{
    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserFeignClient userService;

    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private CompetitionFeignClient matchService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(TeamAddRequest teamAddRequest, User loginUser)
    {
        // 1. 请求参数是否为空？
        if (teamAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if (loginUser == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        if (teamAddRequest.getMatchId() == null || teamAddRequest.getMatchId() < 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛id不能为空或非法");
        }
        log.info("teamAddRequest: {}", teamAddRequest);
        // 3. 获取比赛信息
        final MatchInfoProfileVO matchInfo = matchService.getMatchInfo(teamAddRequest.getMatchId());
        if (matchInfo == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不存在");
        }
        final long userId = loginUser.getId();
        final Long raceId = matchInfo.getId();
        boolean isJoin = userTeamService.checkIsJoin(userId, raceId);
        if (isJoin)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经创建过或是已经存在队伍啦~");
        }
        TeamInfo teamInfo = new TeamInfo();
        teamInfo.setUserId(userId); // 设置队长id
        // 3. 校验信息
        //   1. 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(teamAddRequest.getTeamMaxSize()).orElse(0);
        if (maxNum > matchInfo.getMaxTeamSize())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求, 最大: " + matchInfo.getMaxTeamSize() + "人");
        }
        // 检查目前队员和指导老师人数是否大于规定人数
        List<Long> teammateLists = teamAddRequest.getTeammates();
        // + 1是包括创建人本身
        if (!teammateLists.isEmpty() && teammateLists.size() + 1 > matchInfo.getMaxTeamSize())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求, 最大: " + matchInfo.getMaxTeamSize() + "人");
        }
        List<Long> teachersLists = teamAddRequest.getTeachers();
        if (!teachersLists.isEmpty() && teachersLists.size() > matchInfo.getMaxTeacherSize())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "指导老师人数不满足要求, 最大: " + matchInfo.getMaxTeacherSize() + "人");
        }

        teamInfo.setMaxNum(maxNum); // 设置队伍人数最大值

        //   2. 队伍标题 <= 20
        String name = teamAddRequest.getTeamName();
        if (StringUtils.isBlank(name) || name.length() > 20)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        teamInfo.setName(name);// 设置队伍标题
        //   3. 描述 <= 512
        String description = teamAddRequest.getTeamDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 100)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        teamInfo.setDescription(description); // 设置队伍描述
        //   4. status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(teamAddRequest.getTeamStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 设置队伍公开状态
        teamInfo.setStatus(status);
        //   5. 如果是加密的一定有密码（不为空），密码 <= 32
        String password = teamAddRequest.getTeamPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && !StringUtils.isAnyBlank(password))
        {
            if (password.length() > 32)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
            password = EncryptionUtils.encodePassword(password);
        }
        teamInfo.setPassword(password); // 设置团队密码
        // 6. 插入队伍信息到队伍表
        log.info("teamInfo: {}", teamInfo);
        // 设置比赛id
        teamInfo.setRaceId(teamAddRequest.getMatchId());
        teamInfo.setCategoryId(Long.valueOf(teamAddRequest.getMatchCategoryId()));
        teamInfo.setEventId(Long.valueOf(teamAddRequest.getMatchEventId()));
        teamInfo.setExpireTime(matchInfo.getSignUpEndTime());
        teamInfo.setIsPublic(status == 1 ? 1 : 0);

//        return 1;
        boolean result = this.save(teamInfo);
        Long teamId = teamInfo.getId();
        if (!result || teamId == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
//        Long teamId = teamAddRequest.getId();
//        if (!result || teamId == null)
//        {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
//        }
//        // 7. 插入用户  => 队伍关系到关系表
        List<UserTeam> userTeamList = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        userTeamList.add(UserTeam.createUser(teamId, raceId, userId, 0));
        if (!teammateLists.isEmpty())
        {
            for (Long teamUserId : teammateLists)
            {
                userIds.add(teamUserId);
                userTeamList.add(UserTeam.createUser(teamId, raceId, teamUserId, 1));
            }
        }
        if (!teachersLists.isEmpty())
        {
            for (Long teacherId : teachersLists)
            {
                userIds.add(teacherId);
                userTeamList.add(UserTeam.createUser(teamId, raceId, teacherId, 2));
            }
        }
        Boolean allUserIsExist = userService.validateUsers(userIds);
        if (!allUserIsExist)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队员用户信息不存在，请检查重试");
        }
        boolean checkUserIsJoin = userTeamService.batchCheckIsJoin(userIds, raceId);
        if (checkUserIsJoin)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队员已加入其他队伍，请检查重试");
        }
        result = userTeamService.saveBatch(userTeamList);
        if (!result)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin)
    {
        QueryWrapper<TeamInfo> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null)
        {
            Long id = teamQuery.getId();
            if (id != null && id > 0)
            {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList))
            {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText))
            {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name))
            {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description))
            {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0)
            {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0)
            {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null)
            {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE))
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "无权限查询私有队伍信息");
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<TeamInfo> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList))
        {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (TeamInfo team : teamList)
        {
            Long userId = team.getUserId();
            if (userId == null)
            {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null)
            {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser)
    {
        if (teamUpdateRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TeamInfo oldTeam = this.getById(id);
        if (oldTeam == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "队伍不存在");
        }
        // 只有管理员或者队伍的创建者可以修改
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser))
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET))
        {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword()))
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        TeamInfo updateTeam = new TeamInfo();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser)
    {
        if (teamJoinRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        TeamInfo team = getTeamById(teamId);
//        Date expireTime = team.getExpireTime();
//        if (expireTime != null && expireTime.before(new Date())) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
//        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum))
        {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword()))
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 该用户已加入的队伍数量
        long userId = loginUser.getId();
        // 只有一个线程能获取到锁
        if (redisOperatorService.tryGetDistributedLock(
                RedisConstant.JOIN_TEAM_LOCK,
                String.valueOf(userId), RedisOperatorService.UNLIMITED_RETRY_TIMES))
        {
            try
            {
                // 抢到锁并执行{
                System.out.println("getLock: " + Thread.currentThread().getId());
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("userId", userId);
                long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                if (hasJoinNum > 5)
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
                }
                // 不能重复加入已加入的队伍
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("userId", userId);
                userTeamQueryWrapper.eq("teamId", teamId);
                long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                if (hasUserJoinTeam > 0)
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                }
                // 已加入队伍的人数
                long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                if (teamHasJoinNum >= team.getMaxNum())
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                }
                // 修改队伍信息
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                return userTeamService.save(userTeam);
            }
            catch (Exception e)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
            }
            finally
            {
                // 只能释放自己的锁
                redisOperatorService.releaseDistributedLock(RedisConstant.JOIN_TEAM_LOCK, String.valueOf(userId));
            }
        }
        else
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser)
    {
        if (teamQuitRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        TeamInfo team = getTeamById(teamId);
        long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        // 队伍只剩一人，解散
        if (teamHasJoinNum == 1)
        {
            // 删除队伍
            this.removeById(teamId);
        }
        else
        {
            // 队伍还剩至少两人
            // 是队长
            if (team.getUserId() == userId)
            {
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1)
                {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                TeamInfo updateTeam = new TeamInfo();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result)
                {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 移除关系
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser)
    {
        // 校验队伍是否存在
        TeamInfo team = getTeamById(id);
        long teamId = team.getId();
        // 校验你是不是队伍的队长
        if (team.getUserId() != loginUser.getId())
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无访问权限");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }


    /**
     * 根据 id 获取队伍信息
     *
     * @param teamId
     * @return
     */
    private TeamInfo getTeamById(Long teamId)
    {
        if (teamId == null || teamId <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TeamInfo team = this.getById(teamId);
        if (team == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "队伍不存在");
        }
        return team;
    }

    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId)
    {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}




