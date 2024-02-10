package com.caixy.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.model.dto.user.UserDepartmentMajorDTO;
import com.caixy.model.entity.User;

import java.util.List;

/**
 * @author CAIXYPROMISE
 * @description 针对表【user(用户)】的数据库操作Mapper
 * @createDate 2024-02-06 23:22:54
 * @Entity generator.domain.User
 */
public interface UserMapper extends BaseMapper<User>
{
    List<UserDepartmentMajorDTO> listUserWithDepartmentMajor();
    Page<UserDepartmentMajorDTO> listUserDetails(Page<UserDepartmentMajorDTO> page);
}




