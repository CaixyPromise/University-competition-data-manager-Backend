package com.caixy.marketservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.model.entity.DemandTakes;

import java.util.List;

/**
* @author CAIXYPROMISE
* @description 针对表【demand_takes(需求承接表)】的数据库操作Service
* @createDate 2024-03-03 17:18:45
*/
public interface DemandTakesService extends IService<DemandTakes>
{

    List<DemandTakes> getDemandTakerIdsByDemandId(Long demandId);

    DemandTakes getDemandTakerByDemandId(Long demandId);
}
