package com.caixy.competitionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.dto.match.MatchInfoAddRequest;
import com.caixy.model.entity.MatchInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【match_info(比赛信息表)】的数据库操作Service
 * @createDate 2024-02-06 23:22:54
 */
public interface MatchInfoService extends IService<MatchInfo>
{
    String addMatchInfo(MatchInfoAddRequest postAddRequest, MultipartFile logoFile, User loginUser);
    MatchInfoProfileVO getMatchInfo(Long matchId, boolean canAdmin);

    List<MatchInfoProfileVO> getMatchInfoByIds(List<Long> matchIds, boolean canAdmin);
}
