package com.caixy.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.model.dto.user.AboutMeDTO;
import com.caixy.model.vo.department.UserDepartmentMajorVO;
import com.caixy.model.entity.User;
import com.caixy.model.vo.user.UserWorkVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【user(用户)】的数据库操作Mapper
 * @createDate 2024-02-06 23:22:54
 * @Entity generator.domain.User
 */
public interface UserMapper extends BaseMapper<User>
{
    List<UserDepartmentMajorVO> getUserWithDepartmentMajor();
    Page<UserDepartmentMajorVO> listUserDetailsByPage(Page<UserDepartmentMajorVO> page);

    /**
     * 获取用户信息，可以直接查询道用户的学院+专业名称
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 16:48
     */
    UserWorkVO getUserWorkVO(@Param("userId") long userId);

    /**
     * 批量获取用户信息：学院与专业信息直接返回
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 16:47
     */
    List<UserWorkVO> getUserWorkVOList(@Param("userIds") List<Long> userIdList);

    AboutMeDTO getAboutMe(@Param("userId") Long userId);
}




