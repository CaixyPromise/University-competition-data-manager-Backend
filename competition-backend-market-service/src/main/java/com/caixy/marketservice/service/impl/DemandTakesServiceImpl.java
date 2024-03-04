package com.caixy.marketservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.marketservice.mapper.DemandTakesMapper;
import com.caixy.marketservice.service.DemandTakesService;
import com.caixy.model.entity.DemandTakes;
import com.caixy.model.enums.market.TaskStatusEnum;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【demand_takes(需求承接表)】的数据库操作Service实现
 * @createDate 2024-03-03 17:18:45
 */
@Service
public class DemandTakesServiceImpl extends ServiceImpl<DemandTakesMapper, DemandTakes>
        implements DemandTakesService
{
    /**
     * 根据需求id获取需求承接者id列表
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 16:25
     */
    @Override
    public List<DemandTakes> getDemandTakerIdsByDemandId(Long demandId)
    {
        QueryWrapper<DemandTakes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("demandId", demandId);
        List<DemandTakes> takesListlist = this.list(queryWrapper);
        if (takesListlist.isEmpty())
        {
            return Collections.emptyList();
        }
        return takesListlist;
    }

    /**
     * 获取承接人的信息；
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 16:28
     */
    @Override
    public DemandTakes getDemandTakerByDemandId(Long demandId)
    {
        QueryWrapper<DemandTakes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("demandId", demandId);
        queryWrapper.eq("status", TaskStatusEnum.ACCEPTED.getCode()).or()
                .eq("status", TaskStatusEnum.COMPLETED.getCode());
        return this.getOne(queryWrapper);
    }
}




