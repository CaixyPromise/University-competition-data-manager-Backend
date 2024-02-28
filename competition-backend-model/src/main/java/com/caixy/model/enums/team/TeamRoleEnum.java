package com.caixy.model.enums.team;

import lombok.Getter;

/**
 * @name: com.caixy.model.enums.team.TeamRoleEnum
 * @description: 团队角色枚举信息
 * @author: CAIXYPROMISE
 * @date: 2024-02-28 18:34
 **/
public enum TeamRoleEnum
{
    /**
     * 团队角色枚举信息
     */
    LEADER(0, "管理员"),
    MEMBER(1, "成员"),
    TEACHER(2, "指导老师"),
    APPLY(-1, "申请加入中");

    @Getter
    private final Integer code;

    @Getter
    private final String label;
    TeamRoleEnum(Integer code, String label)
    {
        this.code = code;
        this.label = label;
    }
}
