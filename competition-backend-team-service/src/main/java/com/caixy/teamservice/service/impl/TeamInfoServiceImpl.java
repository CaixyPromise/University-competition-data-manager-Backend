package com.caixy.teamservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.model.entity.TeamInfo;
import com.caixy.teamservice.service.TeamInfoService;
import com.caixy.model.mapper.TeamInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【team_info(报名信息表)】的数据库操作Service实现
* @createDate 2024-02-06 23:38:52
*/
@Service
public class TeamInfoServiceImpl extends ServiceImpl<TeamInfoMapper, TeamInfo>
    implements TeamInfoService{

}




