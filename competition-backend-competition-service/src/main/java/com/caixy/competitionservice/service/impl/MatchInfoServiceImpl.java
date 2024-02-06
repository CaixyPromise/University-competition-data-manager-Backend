package com.caixy.competitionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.competitionservice.mapper.MatchInfoMapper;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.entity.MatchInfo;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【match_info(比赛信息表)】的数据库操作Service实现
* @createDate 2024-02-06 23:22:54
*/
@Service
public class MatchInfoServiceImpl extends ServiceImpl<MatchInfoMapper, MatchInfo>
    implements MatchInfoService
{

}




