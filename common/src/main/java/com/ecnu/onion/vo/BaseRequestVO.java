package com.ecnu.onion.vo;

import com.ecnu.onion.excpetion.CommonServiceException;

/**
 * @author onion
 * @date 2020/1/23 -10:28 上午
 */
public abstract class BaseRequestVO {
    public abstract void checkParams() throws CommonServiceException;
}
