package com.caixy.teamservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.utils.EncryptionUtils;
import com.caixy.common.utils.JsonUtils;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.model.dto.match.properties.GroupDataItem;
import com.caixy.model.dto.message.MessageTemplate;
import com.caixy.model.dto.message.SendMessageDTO;
import com.caixy.model.dto.team.*;
import com.caixy.model.entity.TeamInfo;
import com.caixy.model.entity.User;
import com.caixy.model.entity.UserTeam;
import com.caixy.model.enums.team.TeamRoleEnum;
import com.caixy.model.enums.team.TeamStatusEnum;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.model.vo.team.TeamInfoPageVO;
import com.caixy.model.vo.team.TeamInfoVO;
import com.caixy.model.vo.team.TeamUserVO;
import com.caixy.model.vo.user.UserTeamWorkVO;
import com.caixy.model.vo.user.UserVO;
import com.caixy.serviceclient.service.CompetitionFeignClient;
import com.caixy.serviceclient.service.MessageFeignClient;
import com.caixy.serviceclient.service.UserFeignClient;
import com.caixy.teamservice.mapper.TeamInfoMapper;
import com.caixy.teamservice.mapper.UserTeamMapper;
import com.caixy.teamservice.service.TeamInfoService;
import com.caixy.teamservice.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private UserTeamMapper userTeamMapper;

    @Resource
    private UserFeignClient userService;

    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private CompetitionFeignClient matchService;

    @Resource
    private MessageFeignClient messageFeignClient;

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
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经创建过或是已经加入过这个比赛的队伍啦~");
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
        // 校验队长的信息是否能创建这个比赛的队伍
        checkPermission(matchInfo, loginUser);
        // 6. 插入队伍信息到队伍表
        log.info("teamInfo: {}", teamInfo);
        // 设置比赛id
        teamInfo.setTeamTags(JsonUtils.objectToString(teamAddRequest.getTeamTags()));
        teamInfo.setRaceId(teamAddRequest.getMatchId());
        teamInfo.setCategoryId(Long.valueOf(teamAddRequest.getMatchCategoryId()));
        teamInfo.setEventId(Long.valueOf(teamAddRequest.getMatchEventId()));
        teamInfo.setExpireTime(matchInfo.getSignUpEndTime());
        teamInfo.setIsPublic(status == 1 ? 1 : 0);

        boolean result = this.save(teamInfo);
        Long teamId = teamInfo.getId();
        if (!result || teamId == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }

        // 7. 插入用户  => 队伍关系到关系表
        List<UserTeam> userTeamList = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        userTeamList.add(UserTeam.createUser(teamId, raceId, userId, TeamRoleEnum.LEADER.getCode()));
        if (!teammateLists.isEmpty())
        {
            for (Long teamUserId : teammateLists)
            {
                userIds.add(teamUserId);
                userTeamList.add(UserTeam.createUser(teamId, raceId, teamUserId, TeamRoleEnum.MEMBER.getCode()));
            }
        }
        if (!teachersLists.isEmpty())
        {
            for (Long teacherId : teachersLists)
            {
                userIds.add(teacherId);
                userTeamList.add(UserTeam.createUser(teamId, raceId, teacherId, TeamRoleEnum.TEACHER.getCode()));
            }
        }
        // 校验用户是否存在
        List<User> allUserIsExist = userService.listByIds(userIds);
        if (allUserIsExist.size() != userIds.size() ||
                allUserIsExist.isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队员用户信息不存在，请检查重试");
        }
        // 校验队员是否有权限参加比赛
        allUserIsExist.forEach(item -> checkPermission(matchInfo, item));
        // 校验用户是否加入其他队伍
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

    /**
     * 用户加入队伍：发起申请加入
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 01:58
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser)
    {
        if (teamJoinRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final Long teamId = teamJoinRequest.getTeamId();
        TeamInfo team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        // 检查状态，如果队伍是私密的，需要密码
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum))
        {
            if (StringUtils.isBlank(password) || !EncryptionUtils.matches(password, team.getPassword()))
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        final Long matchId = teamJoinRequest.getRaceId();
        MatchInfoProfileVO matchInfoProfileVO = getMatchInfoProfileVO(matchId);
        // 该用户已加入的队伍数量
        // 检查用户是否已经加入这个比赛的任何队伍
        long userId = loginUser.getId();

        // 只有一个线程能获取到锁
        if (redisOperatorService.tryGetDistributedLock(
                RedisConstant.JOIN_TEAM_LOCK,
                String.valueOf(teamId),
                String.valueOf(userId),
                RedisOperatorService.UNLIMITED_RETRY_TIMES))
        {
            try
            {
                // 抢到锁并执行
                log.info("getLock: " + Thread.currentThread().getId());
                checkPermission(matchInfoProfileVO, loginUser);

                // 不能重复加入已加入的队伍，
                // 检查用户是否已经加入这个队伍
                checkUserIsJoinTeam(userId, teamId, team.getRaceId());

                // 已加入队伍的人数
                long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                if (teamHasJoinNum >= team.getMaxNum() || teamHasJoinNum >= matchInfoProfileVO.getMaxTeamSize())
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满或达到比赛要求上限");
                }
                // 修改队伍信息
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setRaceId(matchId);
                userTeam.setUserRole(TeamRoleEnum.APPLYING.getCode());
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                SendMessageDTO applyTeamMessage = MessageTemplate.applyTeam(
                        loginUser.getUserName(),
                        matchInfoProfileVO.getMatchName(),
                        team.getName(),
                        team.getId(),
                        team.getUserId(),
                        matchInfoProfileVO.getId(),
                        matchInfoProfileVO.getSignUpEndTime()
                );
                messageFeignClient.sendById(applyTeamMessage);
                return userTeamService.save(userTeam);
            }
            catch (Exception e)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
            }
            finally
            {
                // 只能释放自己的锁
                redisOperatorService.releaseDistributedLock(RedisConstant.JOIN_TEAM_LOCK,
                        String.valueOf(teamId), String.valueOf(userId));
            }
        }
        else
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加队伍失败");
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
        if (!Objects.equals(team.getUserId(), loginUser.getId()))
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
     * 遍历队伍信息页面
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 20:27
     */
    @Override
    public Page<TeamInfoPageVO> listByPage(TeamQuery teamQuery, boolean isAdmin)
    {
        TeamInfo team = new TeamInfo();
        BeanUtils.copyProperties(teamQuery, team);
        Page<TeamInfo> page = new Page<>(teamQuery.getCurrent(), teamQuery.getPageSize());
        QueryWrapper<TeamInfo> queryWrapper = new QueryWrapper<>(team);
        // 排除私有、已注册、不公开的团队信息
        queryWrapper.notIn("status", TeamStatusEnum.PRIVATE.getValue(),
                TeamStatusEnum.REGISTED.getValue());
        queryWrapper.ne("isPublic", 0);
        Page<TeamInfo> resultPage = this.page(page, queryWrapper);
        // 如果查找的数据不是为空
        if (resultPage.getRecords().isEmpty())
        {
            Page<TeamInfoPageVO> result = new Page<>(teamQuery.getCurrent(), teamQuery.getPageSize());
            result.setTotal(0);
            return result.setRecords(Collections.emptyList());
        }
        final List<Long> matchIds =
                resultPage.getRecords().stream().map(TeamInfo::getRaceId).collect(Collectors.toList());
        final List<Long> teamIds = resultPage.getRecords().stream().map(TeamInfo::getId).collect(Collectors.toList());
//        userTeamService.batchCount
        if (matchIds.isEmpty())
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取关联信息失败");
        }
        // 获取比赛信息
        HashMap<Long, MatchInfoProfileVO> matchInfos = matchService.getMatchInfoByIds(matchIds);
        // 获取队员信息

        List<TeamInfoPageVO> teamInfoPageVOS = resultPage.getRecords().stream().flatMap(item ->
        {
            TeamInfoPageVO pageVO = new TeamInfoPageVO();
            BeanUtils.copyProperties(item, pageVO);
            MatchInfoProfileVO matchInfoProfileVO = matchInfos.get(item.getRaceId());
            if (matchInfoProfileVO == null)
            {
                return Stream.empty();
            }
            pageVO.setRaceName(matchInfoProfileVO.getMatchName());
            pageVO.setMaxNum(matchInfoProfileVO.getMaxTeamSize());
            pageVO.setTeamTags(JsonUtils.jsonToList(item.getTeamTags()));
            pageVO.setNeedPassword(item.getStatus().equals(2) && !item.getPassword().isEmpty());
            if (item.getIsPublic().equals(0))
            {
                return Stream.empty();
            }
            if (!matchInfoProfileVO.getSignUpEndTime().after(new Date()))
            {
                return Stream.empty();
            }
            return Stream.of(pageVO);
        }).collect(Collectors.toList());
        Page<TeamInfoPageVO> result = new Page<>(teamQuery.getCurrent(), teamQuery.getPageSize());
        result.setRecords(teamInfoPageVOS);
        return result;
    }

    private TeamInfoVO processTeamInfoVO(TeamInfo teamById,
                                         MatchInfoProfileVO matchInfo,
                                         User loginUser,
                                         Long teamId,
                                         boolean needRole,
                                         boolean needLeaderId)
    {
        if (matchInfo == null)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关联比赛信息不存在");
        }
        // 根据团队找成员信息
        List<UserTeamWorkVO> userTeamWorkVO = baseMapper.selectUserWorkVOByTeamId(teamId);
        if (userTeamWorkVO == null || userTeamWorkVO.isEmpty())
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "成员信息不存在");
        }
        // 根据userRole分类出队员信息
        HashMap<Integer, List<UserTeamWorkVO>> groupByUserMap =
                new HashMap<>(userTeamWorkVO.stream().collect(Collectors.groupingBy(UserTeamWorkVO::getTeamUserRole)));
        TeamInfoVO teamInfoVO = assembleTeamInfoVO(teamById, matchInfo, groupByUserMap);
        // 获取当前用户是否是队长，在申请状态，或是成员（普通队员或指导老师都是成员）
        if (needRole)
        {
            setUserRoleInfo(teamInfoVO, groupByUserMap, loginUser);
        }
        // 只有在内部接口调用时才会给值，通常用于检查是否是队长本人进行操作
        if (needLeaderId)
        {
            teamInfoVO.setLeaderId(teamById.getUserId());
        }
        return teamInfoVO;
    }

    /**
     * 根据id获取团队信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 20:28
     */
    @Override
    public TeamInfoVO getTeamAndRaceInfoById(Long teamId,
                                             User loginUser,
                                             boolean needRole,
                                             boolean needLeaderId)
    {
        // 获取队伍信息，这里不需要判断为空，因为在查询中已经判断了，如果为空直接报错
        TeamInfo teamById = getTeamById(teamId);
        // 获取比赛信息
        MatchInfoProfileVO matchInfo = matchService.getMatchInfo(teamById.getRaceId());
        return processTeamInfoVO(teamById, matchInfo, loginUser, teamId, needRole, needLeaderId);
    }

    @Override
    public List<TeamInfoVO> getTeamInfoVOByIds(List<Long> teamIds)
    {
        List<TeamInfo> teamByIds = this.list(new QueryWrapper<TeamInfo>().in("id", teamIds));
        if (teamByIds.isEmpty())
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "队伍不存在");
        }
        MatchInfoProfileVO matchInfo = matchService.getMatchInfo(teamByIds.get(0).getRaceId());
        return teamByIds.stream().map(teamItem ->
                processTeamInfoVO(teamItem, matchInfo, null, teamItem.getId(), false, false)).collect(Collectors.toList());
    }

    /**
     * 同意加入队伍请求
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 20:28
     */
    @Override
    public boolean resolveJoinTeam(ResolveAndRejectRequest teamJoinRequest, User loginUser)
    {
        return handleResolveJoinTeamOrReject(teamJoinRequest, loginUser, true);
    }

    /**
     * 拒绝加入队伍请求
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 20:28
     */
    @Override
    public boolean rejectJoinTeam(ResolveAndRejectRequest teamJoinRequest, User loginUser)
    {
        return handleResolveJoinTeamOrReject(teamJoinRequest, loginUser, false);
    }

    /**
     * 调整队伍报名状态
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 20:28
     */
    @Override
    public boolean makeRegister(Long teamId)
    {
        if (teamId == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不能为空");
        }
        TeamInfo teamInfo = this.getById(teamId);
        if (teamInfo == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }

        UpdateWrapper<TeamInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", TeamStatusEnum.REGISTED.getValue())
                .set("isPublic", 0)
                .eq("id", teamId);
        return this.update(updateWrapper);
    }

    /**
     * 处理加入队伍请求
     *
     * @author CAIXYPROMISE
     * @version 1.0 - 基本实现
     * @since 2024/3/4 02:08
     */
    private boolean handleResolveJoinTeamOrReject(ResolveAndRejectRequest teamJoinRequest, User loginUser, boolean isAccept)
    {
        if (teamJoinRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        TeamInfo team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        final long userId = loginUser.getId();
        final User tagetUser = userService.getByAccount(teamJoinRequest.getUserAccount());
        // 只有队长和管理员才能操作
        if (!team.getUserId().equals(userId) && !userService.isAdmin(loginUser))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }

        final Long matchId = teamJoinRequest.getRaceId();

        MatchInfoProfileVO matchInfoProfileVO = getMatchInfoProfileVO(matchId);

        // 检查用户是否已经加入这个比赛的任何队伍

        // 只有一个线程能获取到锁
        if (redisOperatorService.tryGetDistributedLock(
                isAccept ? RedisConstant.RESOLVE_JOIN_TEAM
                         : RedisConstant.REJECT_JOIN_TEAM,
                String.valueOf(teamId),
                String.valueOf(userId),
                RedisOperatorService.UNLIMITED_RETRY_TIMES))
        {
            try
            {
                // 抢到锁并执行
                if (isAccept)
                {
                    checkPermission(matchInfoProfileVO, loginUser);
                }

                // 检查有没有在申请状态
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId);
                queryWrapper.eq("userId", tagetUser.getId());
                UserTeam applyUser = userTeamMapper.selectOne(queryWrapper);
                // 检查是否存在申请记录
                if (applyUser == null)
                {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "没有申请加入该队伍");
                }

                // 根据用户角色进行判断
                Integer userRole = applyUser.getUserRole();
                if (userRole.equals(TeamRoleEnum.REJECT.getCode()))
                {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "申请被拒绝，无法再次申请");
                }
                else if (!userRole.equals(TeamRoleEnum.APPLYING.getCode())
                        || userRole >= 0)
                {
                    // 不是申请中状态或者角色码为非负数，都视为已经在队伍中
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经申请加入该队伍或已在队伍中");
                }
                // 已加入队伍的人数
                long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                if (teamHasJoinNum >= team.getMaxNum() || teamHasJoinNum >= matchInfoProfileVO.getMaxTeamSize())
                {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满或达到比赛要求上限");
                }
                // 修改队伍信息
                if (isAccept)
                {
                    // 同意申请，将申请状态改为成员
                    applyUser.setUserRole(TeamRoleEnum.MEMBER.getCode());
                    SendMessageDTO joinTeamMessage = MessageTemplate.joinTeam(tagetUser.getUserName(),
                            matchInfoProfileVO.getMatchName(),
                            team.getName(),
                            tagetUser.getId(),
                            team.getId(),
                            matchInfoProfileVO.getId(),
                            matchInfoProfileVO.getSignUpEndTime());
                    messageFeignClient.sendById(joinTeamMessage);
                }
                else
                {
                    // 拒绝申请，将申请状态改为拒绝
                    applyUser.setUserRole(TeamRoleEnum.REJECT.getCode());
                    SendMessageDTO joinTeamMessage = MessageTemplate.rejectTeam(tagetUser.getUserName(),
                            matchInfoProfileVO.getMatchName(),
                            team.getName(),
                            tagetUser.getId(),
                            team.getId(),
                            matchInfoProfileVO.getSignUpEndTime());
                    messageFeignClient.sendById(joinTeamMessage);
                    // todo: 实现站内通知时，发送通知
                }
                return userTeamService.updateById(applyUser);
            }
            catch (Exception e)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改队伍失败: " + e.getMessage());
            }
            finally
            {
                // 只能释放自己的锁
                redisOperatorService.releaseDistributedLock(RedisConstant.RESOLVE_JOIN_TEAM,
                        String.valueOf(teamId),
                        String.valueOf(userId));
            }
        }
        else
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "审批队员加入队伍失败");
        }
    }

    private MatchGroupPair getCategoryAndEventName(Long categoryId, Long eventId, MatchInfoProfileVO matchInfoProfileVO)
    {
        HashMap<Long, String> idsToNames = new HashMap<>();
        GroupDataItem.buildIdToParentGroupNameMap(matchInfoProfileVO.getGroupData(), null, idsToNames);

        String categoryName = idsToNames.get(categoryId);
        String eventName = idsToNames.get(eventId);

        return new MatchGroupPair(categoryName, eventName);
    }

    private List<UserTeam> getApplyUser(Long teamId, Long raceId)
    {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("raceId", raceId);
        queryWrapper.eq("userRole", TeamRoleEnum.APPLYING.getCode());
        return userTeamMapper.selectList(queryWrapper);
    }

    /**
     * 判断是否需要密码
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/28 22:00
     */
    private boolean isNeedPassword(TeamInfo item)
    {
        return item.getStatus().equals(2) && !item.getPassword().isEmpty();
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
        userTeamQueryWrapper.ne("userRole", TeamRoleEnum.REJECT.getCode());
        return userTeamService.count(userTeamQueryWrapper);
    }

    private static class MatchGroupPair
    {
        public String categoryName;
        public String eventName;

        MatchGroupPair(String categoryName, String eventName)
        {
            this.categoryName = categoryName;
            this.eventName = eventName;
        }
    }

    private TeamInfoVO assembleTeamInfoVO(TeamInfo teamInfo, MatchInfoProfileVO matchInfo, HashMap<Integer, List<UserTeamWorkVO>> groupByUserMap)
    {
        List<UserTeamWorkVO> memberUserList = groupByUserMap.get(TeamRoleEnum.MEMBER.getCode());
        TeamInfoVO teamInfoVO = new TeamInfoVO();
        teamInfoVO.setTeamDesc(teamInfo.getDescription());
        teamInfoVO.setRaceId(teamInfo.getRaceId().toString());
        teamInfoVO.setTeamName(teamInfo.getName());
        teamInfoVO.setTeamId(teamInfo.getId().toString());
        teamInfoVO.setRaceName(matchInfo.getMatchName());
        teamInfoVO.setTeamTags(JsonUtils.jsonToList(teamInfo.getTeamTags()));
        teamInfoVO.setTeamMaxNum(teamInfo.getMaxNum());
        teamInfoVO.setTeamCurrentNum(memberUserList == null ? 1 : memberUserList.size() + 1);
        teamInfoVO.setRaceMaxNum(matchInfo.getMaxTeamSize());
        teamInfoVO.setRaceMinNum(matchInfo.getMinTeamSize());
        teamInfoVO.setNeedPassword(isNeedPassword(teamInfo));
        teamInfoVO.setTeacherList(groupByUserMap.get(TeamRoleEnum.TEACHER.getCode()));
        teamInfoVO.setUserList(memberUserList);
        teamInfoVO.setLeaderInfo(groupByUserMap.get(TeamRoleEnum.LEADER.getCode()).get(0));
        MatchGroupPair matchGroupPair =
                getCategoryAndEventName(teamInfo.getCategoryId(), teamInfo.getEventId(), matchInfo);
        teamInfoVO.setCategoryName(matchGroupPair.categoryName);
        teamInfoVO.setEventName(matchGroupPair.eventName);
        teamInfoVO.setSignUpEndTime(matchInfo.getSignUpEndTime());
        teamInfoVO.setMatchLevel(matchInfo.getMatchLevel());
        teamInfoVO.setMatchType(matchInfo.getMatchType());
        return teamInfoVO;
    }

    private void setUserRoleInfo(TeamInfoVO teamInfoVO, HashMap<Integer, List<UserTeamWorkVO>> groupByUserMap, User loginUser)
    {
        UserTeamWorkVO leaderVO = groupByUserMap.get(TeamRoleEnum.LEADER.getCode()).get(0);
        if (leaderVO.getUserAccount().equals(loginUser.getUserAccount()))
        {
            teamInfoVO.setIsLeader(true);
            teamInfoVO.setApplyList(groupByUserMap.get(TeamRoleEnum.APPLYING.getCode()));
            teamInfoVO.setIsMember(false);
            teamInfoVO.setIsApply(false);
            return;
        }
        boolean isApply = groupByUserMap.get(TeamRoleEnum.APPLYING.getCode())
                .stream()
                .anyMatch(vo -> vo.getUserAccount().equals(loginUser.getUserAccount()));
        if (isApply)
        {
            teamInfoVO.setIsApply(true);
            teamInfoVO.setIsLeader(false);
            teamInfoVO.setIsMember(false);
            return;
        }

        boolean isMember =
                Stream.of(groupByUserMap.get(TeamRoleEnum.MEMBER.getCode()),
                                groupByUserMap.get(TeamRoleEnum.TEACHER.getCode())
                        )
                        .flatMap(Collection::stream)
                        .anyMatch(vo -> vo.getUserAccount().equals(loginUser.getUserAccount()));
        if (isMember)
        {
            teamInfoVO.setIsMember(true);
            teamInfoVO.setIsLeader(false);
            teamInfoVO.setIsApply(false);
        }
    }

    private MatchInfoProfileVO getMatchInfoProfileVO(Long raceId)
    {
        if (raceId == null || raceId < 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛id不合法");
        }
        MatchInfoProfileVO matchInfoProfileVO = matchService.getMatchInfo(raceId);
        if (matchInfoProfileVO == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不存在");
        }
        return matchInfoProfileVO;
    }

    private void checkPermission(MatchInfoProfileVO matchInfoProfileVO, User loginUser)
    {
        // 检查有没有被限制参加比赛
        HashMap<Long, HashMap<String, String>> matchPermissionRule =
                matchInfoProfileVO.getMatchPermissionRule();
        if (matchPermissionRule != null)
        {
            HashMap<String, String> departmentMap =
                    matchPermissionRule.get(loginUser.getUserDepartment());
            if (departmentMap != null)
            {
                if (departmentMap.get(loginUser.getUserMajor().toString()) != null)
                {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限参加比赛，快去看看其他比赛吧~");
                }
            }
        }
    }

    private void checkUserIsJoinTeam(Long userId, Long teamId, Long raceId)
    {
        List<UserTeam> isJoinList =
                userTeamMapper.checkUserInTeamOrRace(userId, teamId, raceId);
        if (!isJoinList.isEmpty())
        {
            boolean canJoin = isJoinList.stream().anyMatch(userTeam ->
                    userTeam.getRaceId().equals(raceId) &&
                            (TeamRoleEnum.REJECT.getCode().equals(userTeam.getUserRole()) ||
                                    TeamRoleEnum.APPLYING.getCode().equals(userTeam.getUserRole()))
            );
            if (!canJoin)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已经加入这个比赛的其他队伍啦！");
            }
        }
    }
}




