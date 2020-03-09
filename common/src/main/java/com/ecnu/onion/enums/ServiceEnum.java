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
    EMAIL_IN_USE("邮箱已经被注册"),
    ACCOUNT_NOT_EXIST("用户不存在"),
    WRONG_PASSWORD("密码错误"),
    INVALID_TOKEN("非法的token"),
    PROFILE_UPLOAD_ERROR("头像上传出错"),
    CODE_NOT_EXIST("验证码不存在或已过期"),
    ACCOUNT_DISABLED( "用户被禁用"),
    ACCOUNT_NOT_ACTIVATED("用户尚未激活"),
    ACCOUNT_NOT_LOGIN( "请您登录"),
    NOTE_NOT_EXIST("笔记不存在"),
    NOTE_DELETED( "笔记被删除"),
    NOTE_ILLEGAL( "笔记非法"),
    GROUP_CREATE_ERROR("小组至少2个人"),
    WRONG_CODE("验证码错误"),
    PAGE_OVERFLOW( "非法的页数")
    ,
    ;
    private String message;
}
