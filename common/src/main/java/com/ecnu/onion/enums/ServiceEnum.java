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
    PROFILE_UPLOAD_ERROR(15, "头像上传出错"),
    CODE_NOT_EXIST(16, "验证码不存在或已过期"),
    ACCOUNT_DISABLED(17, "用户被禁用"),
    ACCOUNT_NOT_ACTIVATED(18, "用户尚未激活"),
    NOTE_NOT_EXIST(31, "笔记不存在"),
    NOTE_DELETED(32, "笔记被删除"),
    GROUP_CREATE_ERROR(51, "小组至少2个人"),
    WRONG_CODE(52, "验证码错误"),
    PAGE_OVERFLOW(41, "非法的页数")
    ,
    ;
    private Integer code;
    private String message;
}
