package com.caixy.competitionservice.mapper;

import com.caixy.model.vo.match.MyCreateRaceVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationInfoMapperTest
{
    @Resource
    private RegistrationInfoMapper registrationInfoMapper;


    @Test
    // 测试方法
    public void Test()
    {
        List<MyCreateRaceVO> myCreateRaceVOS = registrationInfoMapper.countTeamsByRaceIds(1L);
        for (MyCreateRaceVO myCreateRaceVO : myCreateRaceVOS)
        {

            System.out.println(myCreateRaceVO);
        }

    }
}