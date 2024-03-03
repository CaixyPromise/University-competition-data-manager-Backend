package com.caixy.model.dto.comment;

import com.caixy.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 获取比赛信息评论请求
 *
 * @name: com.caixy.model.dto.comment.ListCommentByIdPage
 * @author: CAIXYPROMISE
 * @since: 2024-03-01 03:50
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ListCommentByIdPage extends PageRequest implements Serializable
{
    private Long raceId;

}
