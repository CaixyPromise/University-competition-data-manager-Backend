package com.caixy.model.dto.annouce;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建比赛公告请求
 *
 * @name: com.caixy.model.dto.annouce.CreateAnnounceRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 01:46
 **/
@Data
public class CreateAnnounceRequest implements Serializable
{
    /**
     * 公告标题
     */
    @NotNull
    @Max(30)
    @NotEmpty
    private String title;

    /**
     * 公告内容
     */
    @NotNull
    @Max(512)
    @NotEmpty
    private String content;

    /**
     * 比赛id
     */
    @NotNull
    private Long matchId;

    private static final long serialVersionUID = 1L;
}
