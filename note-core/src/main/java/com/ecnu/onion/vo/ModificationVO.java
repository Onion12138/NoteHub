package com.ecnu.onion.vo;

import com.ecnu.onion.excpetion.CommonServiceException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author onion
 * @date 2020/1/29 -9:52 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModificationVO extends BaseRequestVO {
    private String code;
    private String password;
    @Override
    public void checkParams() throws CommonServiceException {

    }
}
