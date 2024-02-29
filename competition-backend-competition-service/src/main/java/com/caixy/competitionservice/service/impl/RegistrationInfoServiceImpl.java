package com.caixy.competitionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.competitionservice.mapper.RegistrationInfoMapper;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.competitionservice.service.RegistrationInfoService;
import com.caixy.model.dto.RegistrationRaceRequest;
import com.caixy.model.entity.RegistrationInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.model.vo.team.TeamInfoVO;
import com.caixy.serviceclient.service.TeamInfoFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author CAIXYPROMISE
 * @description 针对表【registration_info(报名信息表)】的数据库操作Service实现
 * @createDate 2024-02-29 16:39:28
 */
@Service
public class RegistrationInfoServiceImpl extends ServiceImpl<RegistrationInfoMapper, RegistrationInfo>
        implements RegistrationInfoService
{
    @Resource
    private TeamInfoFeignClient teamInfoFeignClient;

    @Resource
    private MatchInfoService matchInfoService;

    /**
     * 保存报名信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 17:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRegistrationInfo(RegistrationRaceRequest registrationRaceRequest, User loginUser)
    {
        final Long teamId = registrationRaceRequest.getTeamId();
        final Long raceId = registrationRaceRequest.getRaceId();
        // 获取比赛信息

        MatchInfoProfileVO matchInfo = matchInfoService.getMatchInfo(raceId, true);
        ThrowUtils.throwIf(matchInfo == null, ErrorCode.PARAMS_ERROR, "比赛不存在");
        // 判断比赛的状态
        // 判断比赛是否在报名时间内
        Date signUpStartTime = matchInfo.getSignUpStartTime();
        Date signUpEndTime = matchInfo.getSignUpEndTime();
        Date now = new Date();
        if (signUpStartTime.after(now) || signUpEndTime.before(now))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不在报名时间内");
        }
        // 获取团队信息
        TeamInfoVO teamInfoById = teamInfoFeignClient.getTeamInfoById(teamId);
        ThrowUtils.throwIf(teamInfoById == null, ErrorCode.PARAMS_ERROR, "团队不存在");
        // 查找是否已经报过名
        QueryWrapper<RegistrationInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",  teamId);
        queryWrapper.eq("raceId", raceId);
        long isRegistered = this.count(queryWrapper);
        if (isRegistered > 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经报过名了");
        }
        // 不需要再做权限校验，因为创建+队员入队的时候，就已经校验了
        // todo: 如果想做校验权限，需要去查出用户User实体类信息，再根据实体类去查权限
        // 创建报名信息
        RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setTeamId(teamId);
        registrationInfo.setRaceId(raceId);
        boolean result = this.save(registrationInfo);
        if (!result)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "报名失败");
        }
        // 更新队伍状态
        boolean updateStatus = teamInfoFeignClient.register(teamId);
        if (!updateStatus)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍状态失败");
        }
        return true;
    }
}




