package com.ecnu.onion.vo;

import com.ecnu.onion.excpetion.CommonServiceException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author onion
 * @date 2020/1/27 -7:02 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginVO extends BaseRequestVO{
    private String email;
    private String password;
    @Override
    public void checkParams() throws CommonServiceException {

    }
}
