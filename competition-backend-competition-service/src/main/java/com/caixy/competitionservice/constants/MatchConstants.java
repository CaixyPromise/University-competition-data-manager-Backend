package com.caixy.competitionservice.constants;

/**
 * 比赛信息的常量配置
 *
 * @name: com.caixy.competitionservice.constants.MatchConstants
 * @author: CAIXYPROMISE
 * @since: 2024-02-27 17:25
 **/
public interface MatchConstants
{
    /**
     * Logo图片限制最大大小：5mb
     */
    final Long LOG_MAX_SIZE = 5L * 1024L * 1024L;
    /**
     * JWT-TOKEN密钥
     */
    final byte[] JWT_TOKEN_KEY = "CAIXYPROMISE".getBytes();
    /**
     * 允许全部学院参加比赛常量配置
     */
    final Long ALL_COLLEGE_ID = -999L;
}
