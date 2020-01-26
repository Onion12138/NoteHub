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
    GROUP_CREATE_ERROR(51, "小组至少2个人")
    ,
    ;
    private Integer code;
    private String message;
}
