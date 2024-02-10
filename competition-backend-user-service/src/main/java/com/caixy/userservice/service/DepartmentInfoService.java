package com.caixy.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.entity.DepartmentInfo;

/**
 * @author CAIXYPROMISE
 * @description 针对表【department_info(学院信息表)】的数据库操作Service
 * @createDate 2024-02-06 23:22:54
 */
public interface DepartmentInfoService extends IService<DepartmentInfo>
{
    boolean departmentExistByName(String departmentName);

    boolean departmentExistById(Long departmentId);
}
