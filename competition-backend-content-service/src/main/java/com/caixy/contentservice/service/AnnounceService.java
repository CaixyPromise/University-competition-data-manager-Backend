package com.caixy.contentservice.service;

import com.caixy.model.dto.annouce.CreateAnnounceRequest;
import com.caixy.model.entity.Announce;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.entity.User;

/**
* @author CAIXYPROMISE
* @description 针对表【announce(公告信息表)】的数据库操作Service
* @createDate 2024-02-06 23:22:54
*/
public interface AnnounceService extends IService<Announce> {

    Boolean createAnnounce(CreateAnnounceRequest request, User loginUser);
}
