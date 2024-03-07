package com.caixy.common.utils;

import com.caixy.common.constant.RegexPatternConstants;

/**
 * 正则匹配操作类
 *
 * @name: com.caixy.common.utils.RegexUtils
 * @author: CAIXYPROMISE
 * @since: 2024-03-06 20:52
 **/
public class RegexUtils
{
    /**
     * 校验手机号格式
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:56
     */
    public static boolean isMobilePhone(String input)
    {
        return match(RegexPatternConstants.PHONE_REGEX, input);
    }

    /**
     * 校验邮箱格式
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:55
     */
    public static boolean isEmail(String input)
    {
        return match(RegexPatternConstants.EMAIL_REGEX, input);
    }

    /**
     * 校验密码合法性
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:58
     */
    public static boolean validatePassword(String input)
    {
        return match(RegexPatternConstants.PASSWORD_REGEX, input);
    }

    private static boolean match(String regex, String input)
    {
        return input.matches(regex);
    }
}
