package com.caixy.userservice;

import com.caixy.model.dto.department.DepartmentWithMajorsDTO;
import com.caixy.userservice.mapper.DepartmentInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class UserServiceApplicationTests
{
    @Resource
    private DepartmentInfoMapper departmentInfoService;
    @Test
    void contextLoads()
    {
        // 学院+专业信息缓存
        List<DepartmentWithMajorsDTO> departmentWithMajorsList = departmentInfoService.selectMajorByDepartmentId(null);
        HashMap<String, HashMap<String, String>> organizedData = new HashMap<>();

        departmentWithMajorsList.forEach(dto ->
        {
            if (!organizedData.containsKey(dto.getDepartmentId()))
            {
                organizedData.put(String.valueOf(dto.getDepartmentId()), new HashMap<>());
            }
            organizedData.get(dto.getDepartmentId()).put(dto.getMajorId().toString(), dto.getMajorName());
        });

        //
        departmentWithMajorsList
                .stream()
                .collect(Collectors.groupingBy(DepartmentWithMajorsDTO::getDepartmentId))
                .forEach((deptId, dtos) ->
                {
                    String deptName = dtos.get(0).getDepartmentName();
                    organizedData.get(deptId).put("_name", deptName);
                });
    }

}
