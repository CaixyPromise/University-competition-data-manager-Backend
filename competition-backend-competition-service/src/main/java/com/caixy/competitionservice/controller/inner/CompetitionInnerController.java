package com.caixy.competitionservice.controller.inner;

import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.serviceclient.service.CompetitionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping("/get/exist")
    public Boolean isExistById(@RequestParam("matchId") Long raceId)
    {
        return matchInfoService.getById(raceId) != null;
    }

    @Override
    @GetMapping("/get")
    public MatchInfoProfileVO getMatchInfo(@RequestParam("matchId") Long matchId)
    {
        if (matchId == null || matchId < 0)
        {
            throw new IllegalArgumentException("比赛ID不合法");
        }
        log.info("获取比赛信息，matchId:{}", matchId);
        MatchInfoProfileVO matchInfo = matchInfoService.getMatchInfo(matchId, true);
        log.info("获取比赛信息成功，matchId:{}", matchId);
        return matchInfo;
    }

    /**
     * 批量根据id获取比赛信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/28 01:59
     */
    @Override
    @PostMapping("/get/nameByIds")
    public HashMap<Long, MatchInfoProfileVO> getMatchInfoByIds(@RequestBody List<Long> Ids)
    {
        List<MatchInfoProfileVO> profileVOList = matchInfoService.getMatchInfoByIds(Ids, true);
        Map<Long, MatchInfoProfileVO> matchInfoMap = profileVOList.stream()
                .collect(Collectors.toMap(
                        MatchInfoProfileVO::getId, // 假设MatchInfoProfileVO有getId方法
                        profileVO -> profileVO,
                        (existingValue, newValue) -> existingValue)); // 如果有重复的ID，保留现有的值;
        return new HashMap<>(matchInfoMap);
    }
}
