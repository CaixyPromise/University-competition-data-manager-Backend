package com.caixy.competitionservice.controller.inner;

import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.serviceclient.service.CompetitionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 比赛信息远程调用接口控制器实现
 *
 * @name: com.caixy.competitionservice.controller.inner.CompetitionInnerController
 * @author: CAIXYPROMISE
 * @since: 2024-02-27 17:14
 **/
@RestController
@RequestMapping("/inner")
@Slf4j
public class CompetitionInnerController implements CompetitionFeignClient
{
    @Resource
    private MatchInfoService matchInfoService;

    @Override
    @GetMapping("/get")
    public MatchInfoProfileVO getMatchInfo(@RequestParam("matchId") Long matchId)
    {
        if (matchId == null || matchId < 0)
        {
            throw new IllegalArgumentException("比赛ID不合法");
        }
        log.info("获取比赛信息，matchId:{}", matchId);
        return matchInfoService.getMatchInfo(matchId, true);
    }
}
