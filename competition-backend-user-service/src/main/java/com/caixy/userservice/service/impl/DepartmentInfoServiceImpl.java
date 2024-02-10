package com.caixy.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.model.entity.DepartmentInfo;
import com.caixy.userservice.mapper.DepartmentInfoMapper;
import com.caixy.userservice.service.DepartmentInfoService;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【department_info(学院信息表)】的数据库操作Service实现
* @createDate 2024-02-06 23:22:54
*/
@Service
public class DepartmentInfoServiceImpl extends ServiceImpl<DepartmentInfoMapper, DepartmentInfo>
    implements DepartmentInfoService
{
    /**
     * 根据学院名称判断该学院是否存在：用于学院创建
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 00:03
     */
    @Override
    public boolean departmentExistByName(String departmentName)
    {
        return this.count(new LambdaQueryWrapper<DepartmentInfo>()
                .eq(DepartmentInfo::getName, departmentName)) > 0;
    }

    /**
     * 根据学院id判断该学院是否存在：用于专业创建
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 00:03
     */
    @Override
    public boolean departmentExistById(Long departmentId)
    {
        return this.count(new LambdaQueryWrapper<DepartmentInfo>()
                .eq(DepartmentInfo::getId, departmentId)) > 0;
    }
}




