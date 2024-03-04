package com.caixy.model.vo.market.properties;

import com.caixy.model.vo.user.UserWorkVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 需求承接人VO
 *
 * @name: com.caixy.model.vo.market.properties.TakerProfileVO
 * @author: CAIXYPROMISE
 * @since: 2024-03-04 16:31
 **/
@Data
public class TakerProfileVO implements Serializable
{
    String userDepartment;
    String userMajor;
    String userName;
    String userAccount;
    String userEmail;
    Date takeTime;
    Long userId;

    public static TakerProfileVO of(UserWorkVO userWorkVO, Date takeTime)
    {
        TakerProfileVO takerProfileVO = new TakerProfileVO();
        BeanUtils.copyProperties(userWorkVO, takerProfileVO);
        takerProfileVO.setTakeTime(takeTime);
        return takerProfileVO;
    }

    private static final long serialVersionUID = 1L;
}
