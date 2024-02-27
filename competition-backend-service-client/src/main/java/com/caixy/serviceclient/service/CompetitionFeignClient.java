package com.caixy.serviceclient.service;

import com.caixy.model.vo.match.MatchInfoProfileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Name: com.caixy.serviceclient.service.CompetitionFeignClient
 * @Description: 比赛信息远程调用接口
 * @Author: CAIXYPROMISE
 * @Date: 2024-02-27 17:11
 **/
@FeignClient(name = "competition-backend-competition-service",
        path = "/api/competition/inner")
public interface CompetitionFeignClient
{
    @GetMapping("/get")
    MatchInfoProfileVO getMatchInfo(@RequestParam("matchId") Long matchId);
}