package com.caixy.model.dto.annouce;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新评论请求体
 *
 * @name: com.caixy.model.dto.annouce.UpdateAnnounceRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 02:03

 **/
@Data
public class UpdateAnnounceRequest implements Serializable
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

    @NotNull
    private Long id;

    private static final long serialVersionUID = 1L;
}
