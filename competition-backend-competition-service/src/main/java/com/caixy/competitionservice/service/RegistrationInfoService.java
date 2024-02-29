package com.caixy.competitionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.dto.RegistrationRaceRequest;
import com.caixy.model.entity.RegistrationInfo;
import com.caixy.model.entity.User;

/**
 * @author CAIXYPROMISE
 * @description 针对表【registration_info(报名信息表)】的数据库操作Service
 * @createDate 2024-02-29 16:39:28
 */
public interface RegistrationInfoService extends IService<RegistrationInfo>
{
    /**
     * 保存报名信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 17:01
     */
    boolean saveRegistrationInfo(RegistrationRaceRequest registrationRaceRequest, User loginUser);
}
