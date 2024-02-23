package com.caixy.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.model.entity.MajorInfo;
import com.caixy.model.vo.department.UserDepartmentMajorVO;
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
    /**
     * 根据专业ID获取专业及其所属部门的详细信息。
     * <p>
     * 获取一个专业的详细信息，包括专业ID、专业名称、创建时间、更新时间，
     * 以及所属部门的ID和名称。
     * 忽略已被标记为删除的专业。
     * </p>
     *
     * @param id 要查询的专业的ID。
     * @return {@link MajorWithDepartmentVO} 包含专业和其所属部门信息。如果没有找到对应的专业，返回 {@code null}。
     */
    MajorWithDepartmentVO getMajorWithDepartmentById(@Param("id") Long id);
}




