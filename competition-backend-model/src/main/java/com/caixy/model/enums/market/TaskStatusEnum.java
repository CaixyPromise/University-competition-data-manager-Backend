package com.caixy.model.enums.market;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @name: com.caixy.model.enums.market.TaskStatusEnum
 * @description: 需求承接状态码
 * @author: CAIXYPROMISE
 * @date: 2024-03-03 18:07
 **/
@Getter
@AllArgsConstructor
public enum TaskStatusEnum
{
    // 申请请求
    PENDING(0, "申请中"),
    ACCEPTED(1, "已接受"),
    COMPLETED(3, "已完成"),
    REJECTED(-1, "已拒绝");

    private final int code;
    private final String desc;


    public static TaskStatusEnum getEnumByCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (TaskStatusEnum status : values())
        {
            if (status.getCode() == code)
            {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据code获取承接状态的描述信息
     *
     * @param code 状态码
     * @return 状态描述，如果未找到返回null
     */
    public static String getDescByCode(int code)
    {
        for (TaskStatusEnum status : values())
        {
            if (status.getCode() == code)
            {
                return status.getDesc();
            }
        }
        return null;
    }
}
