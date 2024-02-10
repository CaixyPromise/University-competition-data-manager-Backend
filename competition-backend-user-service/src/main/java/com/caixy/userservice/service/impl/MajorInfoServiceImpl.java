package com.caixy.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.model.entity.MajorInfo;
import com.caixy.model.vo.major.MajorInfoWithDepartmentQueryVO;
import com.caixy.model.vo.major.MajorWithDepartmentVO;
import com.caixy.userservice.mapper.MajorInfoMapper;
import com.caixy.userservice.service.MajorInfoService;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【major_info(专业信息表)】的数据库操作Service实现
* @createDate 2024-02-06 23:22:54
*/
@Service
public class MajorInfoServiceImpl extends ServiceImpl<MajorInfoMapper, MajorInfo>
    implements MajorInfoService
{
    /**
     * 根据学院名称判断该学院是否存在：用于学院创建
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 00:03
     */
    @Override
    public boolean majorExistByName(String departmentName, Long departmentId)
    {
        return this.count(new LambdaQueryWrapper<MajorInfo>()
                .eq(MajorInfo::getName, departmentName)
                .eq(MajorInfo::getDepartId, departmentId)) > 0;
    }

    /**
     * 根据学院id判断该学院是否存在：用于专业创建
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 00:03
     */
    @Override
    public boolean majorExistById(Long departmentId)
    {
        return this.count(new LambdaQueryWrapper<MajorInfo>()
                .eq(MajorInfo::getId, departmentId)) > 0;
    }

    @Override
    public Page<MajorInfoWithDepartmentQueryVO> listMajorInfoWithDepartment(long current, long size)
    {
        return this.baseMapper.listMajorWithDepartmentByPage(new Page<>(current, size));
    }

    @Override
    public MajorWithDepartmentVO getMajorWithDepartmentById(long id)
    {
        return this.baseMapper.getMajorWithDepartmentById(id);
    }
}




