package com.caixy.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum
{

    ADMIN("管理员", "admin", 0),

    USER("用户", "user", 1),
    TEACHER("教师", "teacher", 2),

    BAN("被封号", "ban", -999);

    private final String text;

    private final String value;

    private final Integer code;

    UserRoleEnum(String text, String value, Integer code)
    {
        this.text = text;
        this.value = value;
        this.code = code;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues()
    {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value)
    {
        if (ObjectUtils.isEmpty(value))
        {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values())
        {
            if (anEnum.value.equals(value))
            {
                return anEnum;
            }
        }
        return null;
    }

    public static UserRoleEnum getEnumByCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values())
        {
            if (anEnum.code.equals(code))
            {
                return anEnum;
            }
        }
        return null;
    }

}
