package com.caixy.contentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.contentservice.mapper.AnnounceMapper;
import com.caixy.contentservice.service.AnnounceService;
import com.caixy.model.entity.Announce;

import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【announce(公告信息表)】的数据库操作Service实现
* @createDate 2024-02-06 23:22:54
*/
@Service
public class AnnounceServiceImpl extends ServiceImpl<AnnounceMapper, Announce>
    implements AnnounceService
{

}




