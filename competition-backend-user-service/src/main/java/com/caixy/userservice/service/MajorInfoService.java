package com.caixy.userservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.entity.MajorInfo;
import com.caixy.model.vo.major.MajorInfoWithDepartmentQueryVO;
import com.caixy.model.vo.major.MajorWithDepartmentVO;

/**
 * @author CAIXYPROMISE
 * @description 针对表【major_info(专业信息表)】的数据库操作Service
 * @createDate 2024-02-06 23:22:54
 */
public interface MajorInfoService extends IService<MajorInfo>
{

    boolean majorExistByName(String departmentName, Long departmentId);

    boolean majorExistById(Long departmentId);

    Page<MajorInfoWithDepartmentQueryVO> listMajorInfoWithDepartment(long current, long size);
    MajorWithDepartmentVO getMajorWithDepartmentById(long id);
}
