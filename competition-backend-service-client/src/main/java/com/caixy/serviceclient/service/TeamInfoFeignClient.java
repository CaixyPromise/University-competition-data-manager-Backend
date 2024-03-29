package com.caixy.serviceclient.service;

import com.caixy.model.vo.team.TeamInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 团队信息内部调用接口
 *
 * @name: com.caixy.serviceclient.service.TeamInfoFeignClient
 * @author: CAIXYPROMISE
 * @since: 2024-02-29 16:46
 **/
@FeignClient(name = "competition-backend-team-service", path = "/api/team/inner")
public interface TeamInfoFeignClient
{
    /**
     * 根据团队id获取团队完整信息信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/29 17:31
     */
    @GetMapping("/getById")
    TeamInfoVO getTeamProfileInfoById(@RequestParam("teamId") Long teamId);

    @GetMapping("/getByIds")
    List<TeamInfoVO> getTeamProfileInfoByIds(@RequestParam("teamIds") List<Long> teamIds);

    /**
     * 报名端调用该接口，实现报名
     *
     * @author CAIXYPROMISE
     * @since 2024/2/29 20:26
     * @version 1.0
     */
    @PostMapping("/register")
    Boolean register(@RequestParam("teamId") Long teamId);
}
