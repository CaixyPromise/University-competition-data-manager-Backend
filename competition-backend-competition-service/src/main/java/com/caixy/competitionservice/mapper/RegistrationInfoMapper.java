package com.caixy.competitionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caixy.model.entity.RegistrationInfo;
import com.caixy.model.vo.match.MyCreateRaceVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【registration_info(报名信息表)】的数据库操作Mapper
 * @createDate 2024-02-29 16:39:28
 * @Entity com.caixy.model.entity.RegistrationInfo
 */
public interface RegistrationInfoMapper extends BaseMapper<RegistrationInfo>
{
    List<MyCreateRaceVO> countTeamsByRaceIds(@Param("userId") Long userId);
}




