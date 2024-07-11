package com.caixy.model.vo.user;

import com.caixy.common.utils.JsonUtils;
import com.caixy.model.dto.user.AboutMeDTO;
import com.caixy.model.enums.user.UserSexEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 个人完整信息VO
 *
 * @name: com.caixy.model.vo.user.AboutMeVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 03:39
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AboutMeVO extends UserWorkVO
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
    private String userSex;
    /**
     * 用户个人介绍
     */
    private String userProfile;

    /**
     * 用户余额
     */
    private Long balance;

    /**
     * 用户标签
     */
    private List<String> userTags;

    public static AboutMeVO of(AboutMeDTO aboutMeDTO)
    {
        AboutMeVO aboutMeVO = new AboutMeVO();
        BeanUtils.copyProperties(aboutMeDTO, aboutMeVO);
        aboutMeVO.setUserSex(UserSexEnum.getTextByCode(aboutMeDTO.getUserSex()));
        aboutMeVO.setUserTags(JsonUtils.jsonToList(aboutMeDTO.getUserTags()));
        return aboutMeVO;
    }


    private static final long serialVersionUID = 1L;
}
