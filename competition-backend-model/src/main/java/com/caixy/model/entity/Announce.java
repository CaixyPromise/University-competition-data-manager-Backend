package com.caixy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 公告信息表
 * @TableName announce
 */
@TableName(value ="announce")
@Data
public class Announce implements Serializable {
    /**
     * 公告id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告创建所属用户id
     */
    private Long createUserId;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 创建时间
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