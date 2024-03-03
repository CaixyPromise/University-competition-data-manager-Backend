package com.caixy.model.enums.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @name: com.caixy.model.enums.message.MessageUserRoleEnum
 * @description: 消息队列中的用户身份枚举
 * @author: CAIXYPROMISE
 * @date: 2024-03-03 00:46
 **/
@Getter
@AllArgsConstructor
public enum MessageUserRoleEnum
{
    SYSTEM(0, "系统"),
    ADMIN(-1, "管理员");

    private final Integer code;
    private final String desc;

    public static MessageUserRoleEnum getEnumByCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (MessageUserRoleEnum value : MessageUserRoleEnum.values())
        {
            if (value.getCode().equals(code))
            {
                return value;
            }
        }
        return null;
    }

}
