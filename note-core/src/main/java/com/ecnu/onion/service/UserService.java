package com.ecnu.onion.service;

import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.ModificationVO;
import com.ecnu.onion.vo.RegisterVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -5:49 下午
 */
public interface UserService {

    void register(RegisterVO registerVO);

    void activate(String code);

    Map<String, String> login(LoginVO loginVO);

    String uploadProfile(String email, MultipartFile file);

    void modifyPassword(ModificationVO modificationVO);

    void sendCode(String email);

    void modifyUsername(String email, String username);
}
