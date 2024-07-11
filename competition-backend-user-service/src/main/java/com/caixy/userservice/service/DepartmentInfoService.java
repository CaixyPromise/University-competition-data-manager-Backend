package com.caixy.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.dto.department.DepartmentWithMajorsDTO;
import com.caixy.model.entity.DepartmentInfo;

import java.util.List;
import java.util.Map;

/**
 * @author CAIXYPROMISE
 * @description 针对表【department_info(学院信息表)】的数据库操作Service
 * @createDate 2024-02-06 23:22:54
 */
public interface DepartmentInfoService extends IService<DepartmentInfo>
{
    /**
     * 判断学院是否存在：根据名称：用于新建学院
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 13:21
     */
    boolean departmentExistByName(String departmentName);

    /**
     * 判断学院是否存在：根据ID：用于更新学院
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 13:22
     */
    boolean departmentExistById(Long departmentId);

    /**
     * 获取学院下的所有专业信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/11 13:22
     */
    List<DepartmentWithMajorsDTO> getMajorUnderDepartment(Long departmentId);

    /**
     * 批量验证学院和专业是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/12 01:04
     */
    List<Map<String, Object>> validateDepartmentsAndMajors(List<Long> departmentIds, List<Long> majorIds);

    void departmentAndMajorDataPreload();
}
