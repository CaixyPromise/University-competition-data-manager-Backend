package com.caixy.teamservice.controller.inner;

import com.caixy.model.vo.team.TeamInfoVO;
import com.caixy.serviceclient.service.TeamInfoFeignClient;
import com.caixy.teamservice.service.TeamInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 团队信息内部调用接口控制器
 *
 * @name: com.caixy.teamservice.controller.inner.TeamInnerController
 * @author: CAIXYPROMISE
 * @since: 2024-02-29 16:44
 **/
@RestController
@RequestMapping("/inner")
public class TeamInnerController implements TeamInfoFeignClient
{
    @Resource
    private TeamInfoService teamInfoService;

    @Override
    @GetMapping("/getById")
    public TeamInfoVO getTeamProfileInfoById(@RequestParam("teamId") Long teamId)
    {
        return teamInfoService.getTeamAndRaceInfoById(teamId, null, true, true);
    }

    @Override
    @GetMapping("/getByIds")
    public List<TeamInfoVO> getTeamProfileInfoByIds(@RequestParam("teamIds") List<Long> teamIds)
    {
        return teamInfoService.getTeamInfoVOByIds(teamIds);
    }

    @Override
    @PostMapping("/register")
    public Boolean register(@RequestParam("teamId") Long teamId)
    {
        return teamInfoService.makeRegister(teamId);
    }

}
