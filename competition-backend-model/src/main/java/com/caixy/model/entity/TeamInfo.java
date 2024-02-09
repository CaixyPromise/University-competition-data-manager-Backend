package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 报名信息表
 * @TableName team_info
 */
@TableName(value ="team_info")
@Data
public class TeamInfo implements Serializable {
    /**
     * 报名id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 报名的竞赛id
     */
    private Long raceId;

    /**
     * 报名信息-报名信息json
     */
    private String signInfo;

    /**
     * 报名状态 1：待审核；2：报名成功；3：报名失败(审核失败)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 报名时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}