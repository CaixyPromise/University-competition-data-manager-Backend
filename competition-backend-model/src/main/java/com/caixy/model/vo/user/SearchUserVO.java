package com.caixy.model.vo.user;

import com.caixy.model.entity.User;
import lombok.Data;

import java.io.Serializable;

/**
 * 查找用户返回的VO封装类
 *
 * @name: com.caixy.model.vo.user.SearchUserVO
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 21:31
 **/
@Data
public class SearchUserVO implements Serializable
{
    private Long userId;
    /**
     * 账号(用户学/工号)
     */
    private String userAccount;
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户部门/院系(学院)
     */
    private String userDepartment;

    /**
     * 用户专业
     */
    private String userMajor;

    public static SearchUserVO EntityConvertToVO(User user)
    {
        SearchUserVO vo = new SearchUserVO();
        vo.setUserMajor(String.valueOf(user.getUserMajor()));
        vo.setUserDepartment(String.valueOf(user.getUserDepartment()));
        vo.setUserName(user.getUserName());
        vo.setUserAccount(user.getUserAccount());
        vo.setUserId(user.getId());
        return vo;
    }

    private static final long serialVersionUID = -1L;
}
