package com.caixy.model.enums.team;

import lombok.Getter;

/**
 * 队伍开放状态枚举
 *
 * @name: com.caixy.model.enums.team.TeamStatusEnum
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 15:35
 **/
@Getter
public enum TeamStatusEnum
{
    REGISTED(3,"已报名比赛"),

    PUBLIC(0,"公开"),

    PRIVATE(1,"私有"),

    SECRET(2,"加密");

    private int value;

    private String text;


    public static TeamStatusEnum getEnumByValue(Integer value)
    {
        if (value == null)
        {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values)
        {
            if (teamStatusEnum.getValue() == value)
            {
                return teamStatusEnum;
            }
        }
        return null;
    }


    TeamStatusEnum(int value, String text)
    {
        this.value = value;
        this.text = text;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
