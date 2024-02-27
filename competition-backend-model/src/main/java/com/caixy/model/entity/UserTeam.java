package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户队伍关系
 *
 * @TableName user_team
 */
@TableName(value = "user_team")
@Data
public class UserTeam implements Serializable
{
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 关联的比赛id
     */
    private Long raceId;

    /**
     * 团队角色
     */
    private Integer userRole;
    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    public static UserTeam createUser(Long teamId, Long raceId, Long userId, Integer userRole)
    {
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setRaceId(raceId);
        userTeam.setUserId(userId);
        userTeam.setUserRole(userRole);
        userTeam.setJoinTime(new Date());
        return userTeam;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}