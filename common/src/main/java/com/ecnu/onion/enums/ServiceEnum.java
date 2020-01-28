package com.ecnu.onion.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author onion
 * @date 2020/1/23 -5:31 下午
 */
@Getter
@AllArgsConstructor
public enum ServiceEnum {
    EMAIL_IN_USE(11, "邮箱已经被注册"),
    ACCOUNT_NOT_EXIST(12, "用户不存在"),
    WRONG_PASSWORD(13, "密码错误"),
    INVALID_TOKEN(14, "非法的token"),
    GROUP_CREATE_ERROR(51, "小组至少2个人"),
    PAGE_OVERFLOW(41, "非法的页数")
    ,
    ;
    private Integer code;
    private String message;
}
