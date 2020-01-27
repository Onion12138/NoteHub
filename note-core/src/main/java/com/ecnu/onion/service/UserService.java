package com.ecnu.onion.service;

import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.RegisterVO;

/**
 * @author onion
 * @date 2020/1/27 -5:49 下午
 */
public interface UserService {
    void sendEmail(String email);

    void register(RegisterVO registerVO);

    void activate(String code);

    void login(LoginVO loginVO);
}
