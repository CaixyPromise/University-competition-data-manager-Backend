package com.caixy.model.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 用户性别枚举
 *
 * @name: com.caixy.model.enums.user.UserSexEnum
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 17:33
 **/
@Getter
@AllArgsConstructor
public enum UserSexEnum
{
    /**
     * 性别
     */
    FEMALE(1, "女"),
    MALE(2, "男"),
    SECRET(0, "保密");

    private final Integer code;
    private final String text;

    public static String getTextByCode(Integer code)
    {
        for (UserSexEnum userSexEnum : values())
        {
            if (Objects.equals(userSexEnum.getCode(), code))
            {
                return userSexEnum.getText();
            }
        }
        return null;
    }

}
