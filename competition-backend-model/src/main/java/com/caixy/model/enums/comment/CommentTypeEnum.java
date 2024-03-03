package com.caixy.model.enums.comment;

import lombok.Getter;

/**
 * @name: com.caixy.model.enums.comment.CommentTypeEnum
 * @description: 评论信息类型枚举
 * @author: CAIXYPROMISE
 * @date: 2024-03-01 03:17
 **/
@Getter
public enum CommentTypeEnum
{
    /**
     * 是回复评论
     */
    REPLY(1, "评论回复"),
    COMMENT(0, "评论");

    CommentTypeEnum(int code, String label)
    {
        this.code = code;
        this.label = label;
    }

    public static CommentTypeEnum getEnumByValue(Integer value)
    {
        if (value == null)
        {
            return null;
        }
        for (CommentTypeEnum typeEnum : values())
        {
            if (typeEnum.getCode() == value)
            {
                return typeEnum;
            }
        }
        return null;
    }

    private final int code;
    private final String label;
}
