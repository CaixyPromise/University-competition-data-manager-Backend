package com.caixy.userservice.service.impl;

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

}




