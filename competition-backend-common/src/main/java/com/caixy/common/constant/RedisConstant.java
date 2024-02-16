package com.caixy.common.constant;

import lombok.Getter;

/**
 * @Name: com.caixy.project.constant.RedisConstant
 * @Description: Redis缓存的常量：Key和过期时间
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-20 20:20
 **/
@Getter
public enum RedisConstant
{
    // 单个学院对应专业的信息
    ACADEMY_MAJOR("ACADEMY_MAJOR", 60L * 60L * 24L),

    // 全部学院对应专业的信息：信息列表
    ALL_ACADEMY_MAJOR("ALL_ACADEMY_MAJOR",60L*60L*24L);


    private final String key;
    private final Long expire;

    RedisConstant(String key, Long expire)
    {
        this.key = key;
        this.expire = expire;
    }


}
