package com.ecnu.onion.excpetion;

import com.ecnu.onion.enums.ServiceEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author onion
 * @date 2020/1/23 -10:29 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonServiceException extends RuntimeException {
    private Integer code;
    private String message;

    public CommonServiceException(int code, String message){
        this.code = code;
        this.message = message;
    }
    public CommonServiceException(ServiceEnum serviceEnum) {
        this.code = serviceEnum.getCode();
        this.message = serviceEnum.getMessage();
    }
}
