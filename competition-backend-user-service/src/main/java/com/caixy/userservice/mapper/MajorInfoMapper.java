package com.caixy.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.model.dto.major.MajorInfoQueryRequest;
import com.caixy.model.vo.department.UserDepartmentMajorVO;
import com.caixy.model.entity.MajorInfo;
import com.caixy.model.vo.major.MajorInfoWithDepartmentQueryVO;
import com.caixy.model.vo.major.MajorWithDepartmentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【major_info(专业信息表)】的数据库操作Mapper
 * @createDate 2024-02-06 23:22:54
 * @Entity generator.domain.MajorInfo
 */
public interface MajorInfoMapper extends BaseMapper<MajorInfo>
{
    List<MajorInfoWithDepartmentQueryVO> getMajorWithDepartment();
    Page<MajorInfoWithDepartmentQueryVO> listMajorWithDepartmentByPage(Page<UserDepartmentMajorVO> page);

    MajorWithDepartmentVO getMajorWithDepartmentById(@Param("id") Long id);
}




