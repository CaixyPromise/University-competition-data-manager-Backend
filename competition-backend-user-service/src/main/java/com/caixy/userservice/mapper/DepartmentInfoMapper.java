package com.caixy.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.model.dto.department.DepartmentWithMajorsDTO;
import com.caixy.model.entity.DepartmentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【department_info(学院信息表)】的数据库操作Mapper
 * @createDate 2024-02-06 23:22:54
 * @Entity generator.domain.DepartmentInfo
 */
public interface DepartmentInfoMapper extends BaseMapper<DepartmentInfo>
{
    List<DepartmentWithMajorsDTO > selectMajorByDepartmentId(@Param("departmentId") Long departmentId);
}




