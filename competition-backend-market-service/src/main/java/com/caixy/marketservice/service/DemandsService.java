package com.caixy.marketservice.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.caixy.model.dto.market.DemandQueryRequest;
import com.caixy.model.entity.Demands;
import com.caixy.model.vo.market.DemandVO;
import com.caixy.model.vo.user.UserWorkVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author CAIXYPROMISE
* @description 针对表【demands(需求市场表)】的数据库操作Service
* @createDate 2024-03-03 17:18:45
*/
public interface DemandsService extends IService<Demands>
{
    void validPost(Demands post);

    DemandVO getDemandVO(Demands post, UserWorkVO userWorkVO);

    boolean isTaker(Long demandId, Long userId);

    Wrapper<Demands> getQueryWrapper(DemandQueryRequest postQueryRequest);

    Page<DemandVO> getPostVOPage(Page<Demands> postPage, HttpServletRequest request);

    boolean applyForDemand(Long demandId, Long userId);

    boolean acceptDemandTake(Long demandId, Long loginUserId, Long targetUserId);

    boolean completeDemand(Long demandId, Long userId);

    boolean rejectDemandTake(Long demandId, Long loginUserId, Long targetUserId);
}
