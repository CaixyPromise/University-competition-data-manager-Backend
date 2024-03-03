package com.caixy.model.dto.annouce;

import com.caixy.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告信息分页请求
 *
 * @name: com.caixy.model.dto.annouce.AnnouncePageRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 02:09
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncePageRequest extends PageRequest
{
    /**
     * 关联的赛事id
     */
    private Long raceId;
}
