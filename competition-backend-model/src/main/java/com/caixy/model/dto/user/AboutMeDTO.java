package com.caixy.model.dto.user;

import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查找个人信息的数据库载体
 *
 * @name: com.caixy.model.dto.user.AboutMeDTO
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 16:51
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AboutMeDTO extends UserWorkVO
{
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户性别
     */
    private Integer userSex;
    /**
     * 用户个人介绍
     */
    private String userProfile;
    /**
     * 用户标签
     */
    private String userTags;

    private static final long serialVersionUID = 1L;
}
