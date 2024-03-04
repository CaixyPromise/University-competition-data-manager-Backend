package com.caixy.model.enums.market;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @name: com.caixy.model.enums.market.DemandStatus
 * @description: 需求状态码
 * @author: CAIXYPROMISE
 * @date: 2024-03-03 18:05
 **/
@Getter
@AllArgsConstructor
public enum DemandStatus
{
    OPEN(0, "开放中"),
    CLOSED(1, "关闭"),
    IN_PROGRESS(2, "进行中");

    private final int code;
    private final String desc;

    /**
     * 根据code获取描述信息
     *
     * @param code 状态码
     * @return 状态描述，如果未找到返回null
     */
    public static String getDescByCode(int code)
    {
        for (DemandStatus status : DemandStatus.values())
        {
            if (status.getCode() == code)
            {
                return status.getDesc();
            }
        }
        return null; // 或者你可以选择抛出一个异常
    }
}

