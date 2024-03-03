package com.caixy.contentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.exception.BusinessException;
import com.caixy.contentservice.mapper.AnnounceMapper;
import com.caixy.contentservice.service.AnnounceService;
import com.caixy.model.dto.annouce.CreateAnnounceRequest;
import com.caixy.model.entity.Announce;
import com.caixy.model.entity.User;
import com.caixy.serviceclient.service.CompetitionFeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author CAIXYPROMISE
 * @description 针对表【announce(公告信息表)】的数据库操作Service实现
 * @createDate 2024-02-06 23:22:54
 */
@Service
public class AnnounceServiceImpl extends ServiceImpl<AnnounceMapper, Announce>
        implements AnnounceService
{
    @Resource
    private CompetitionFeignClient competitionFeignClient;

    @Override
    public Boolean createAnnounce(CreateAnnounceRequest request, User loginUser)
    {
        Boolean raceIsExist = competitionFeignClient.isExistById(request.getMatchId());
        if (!raceIsExist)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }
        Announce announce = new Announce();
        announce.setRaceId(request.getMatchId());
        announce.setTitle(request.getTitle());
        announce.setContent(request.getContent());
        announce.setCreateUserId(loginUser.getId());
        return this.save(announce);
    }
}




