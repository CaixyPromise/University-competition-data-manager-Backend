package com.caixy.userservice.startup;

import com.alibaba.nacos.shaded.com.google.common.base.Stopwatch;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.model.dto.department.DepartmentWithMajorsDTO;
import com.caixy.userservice.mapper.DepartmentInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务启动数据预热类
 *
 * @name: com.caixy.userservice.startup.DataLoaderRunner
 * @author: CAIXYPROMISE
 * @since: 2024-02-15 00:56
 **/
@Component
@Slf4j
@ConditionalOnProperty(name = "preload.department-major.enabled", havingValue = "true")
public class DataLoaderRunner implements ApplicationRunner
{
    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private DepartmentInfoMapper departmentInfoService;

    @Override
    public void run(ApplicationArguments args)
    {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("开始预热数据...");
        // 预热学院+专业信息缓存
        departmentAndMajorDataPreload();
        log.info("预热数据完成，耗时：{}", stopwatch.stop());
    }

    /**
     * 预热加载学院+专业信息缓存
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/15 00:58
     */
    private void departmentAndMajorDataPreload()
    {
        // 学院+专业信息缓存
        List<DepartmentWithMajorsDTO> departmentWithMajorsList = departmentInfoService.selectMajorByDepartmentId(null);
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