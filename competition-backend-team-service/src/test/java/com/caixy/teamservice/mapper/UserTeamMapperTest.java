package com.caixy.teamservice.mapper;

import com.caixy.model.vo.user.UserTeamWorkVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class UserTeamMapperTest
{
    @Resource
    private TeamInfoMapper teamInfoMapper;

    @Test
    public void testSelectUserTeamList()
    {
        List<Long> teamIds = Collections.singletonList(1762484976099999745L);
        List<UserTeamWorkVO> result = teamInfoMapper.listUserWorkVOByTeamIds(teamIds);
        for (UserTeamWorkVO userWorkVO : result)
        {
            System.out.println(userWorkVO);
        }
    }

}