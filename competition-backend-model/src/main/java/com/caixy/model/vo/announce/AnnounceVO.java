package com.caixy.model.vo.announce;

import com.caixy.model.entity.Announce;
import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告信息VO
 *
 * @name: com.caixy.model.vo.announce.AnnounceVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 02:07
 **/
@Data
public class AnnounceVO implements Serializable
{
    /**
     * 公告id
     */
    private Long id;
    /**
     * 公告标题
     */
    private String title;
    /**
     * 公告内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人信息
     */
    private UserWorkVO createUser;

    public static AnnounceVO of(Announce announce)
    {
        AnnounceVO announceVO = new AnnounceVO();
        BeanUtils.copyProperties(announce, announceVO);
        return announceVO;
    }


    private static final long serialVersionUID = 1L;
}
