package com.caixy.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.model.dto.department.DepartmentWithMajorsDTO;
import com.caixy.model.entity.DepartmentInfo;
import com.caixy.userservice.mapper.DepartmentInfoMapper;
import com.caixy.userservice.service.DepartmentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CAIXYPROMISE
 * @description 针对表【department_info(学院信息表)】的数据库操作Service实现
 * @createDate 2024-02-06 23:22:54
 */
@Service
@Slf4j
public class DepartmentInfoServiceImpl extends ServiceImpl<DepartmentInfoMapper, DepartmentInfo>
        implements DepartmentInfoService
{
    private RedisOperatorService redisOperatorService;


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

    @Override
    public List<DepartmentWithMajorsDTO> getMajorUnderDepartment(Long departmentId)
    {
        return this.baseMapper.selectMajorByDepartmentId(departmentId);
    }

    @Override
    public List<Map<String, Object>> validateDepartmentsAndMajors(List<Long> departmentIds, List<Long> majorIds)
    {
        return this.baseMapper.validateDepartmentsAndMajors(departmentIds, majorIds);
    }

    @Override
    public void departmentAndMajorDataPreload()
    {
        // 学院+专业信息缓存
        List<DepartmentWithMajorsDTO> departmentWithMajorsList = this.baseMapper.selectMajorByDepartmentId(null);
        HashMap<String, HashMap<String, Object>> organizedData = new HashMap<>();
        log.info("学院+专业信息缓存预热数据库加载信息：{}", departmentWithMajorsList);
        departmentWithMajorsList.forEach(dto ->
        {
            if (!organizedData.containsKey(String.valueOf(dto.getDepartmentId())))
            {
                organizedData.put(String.valueOf(dto.getDepartmentId()), new HashMap<>());
            }
            HashMap<String, Object> valueMap = organizedData.get(String.valueOf(dto.getDepartmentId()));
            valueMap.put(dto.getMajorId().toString(), dto.getMajorName());
        });

        log.info("departmentWithMajorsList.forEach，组织后的数据：{}", organizedData);


        //
        departmentWithMajorsList
                .stream()
                .collect(Collectors.groupingBy(DepartmentWithMajorsDTO::getDepartmentId))
                .forEach((deptId, dtos) ->
                {
                    String deptName = dtos.get(0).getDepartmentName();
                    organizedData.get(String.valueOf(deptId)).put("_name", deptName);
                });
        log.info("学院+专业信息缓存预热，组织后的数据：{}", organizedData);

        // 更新缓存
        organizedData.forEach((idKey, value) ->
        {
            redisOperatorService.setHashMap(RedisConstant.ACADEMY_MAJOR, idKey, value);
            log.info("学院专业信息缓存更新成功，学院id: {}, 学院名称: {}", idKey, value.get("_name"));
        });

    }
}




