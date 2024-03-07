package com.caixy.common.constant;

/**
 * @Name: com.caixy.common.constant.RegexPatternConstants
 * @Description: 正则匹配常量通配符
 * @Author: CAIXYPROMISE
 * @Date: 2024-03-06 20:51
 **/
public interface RegexPatternConstants
{
    String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    String PHONE_REGEX = "^1[3-9]\\d{9}$";

    String PASSWORD_REGEX = "^\\w{4,32}$";
}
