package com.ecnu.onion.controller;

import com.ecnu.onion.service.UserService;
import com.ecnu.onion.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -9:38 上午
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponseVO register(@RequestBody RegisterVO registerVO) {
        userService.register(registerVO);
        return BaseResponseVO.success();
    }

    @GetMapping("/activate")
    public BaseResponseVO activate(@RequestParam String code) {
        userService.activate(code);
        return BaseResponseVO.success();
    }

    @PostMapping("/login")
    public BaseResponseVO login(@RequestBody LoginVO loginVO){
        Map<String, String> map = userService.login(loginVO);
        return BaseResponseVO.success(map);
    }

    @GetMapping("/uploadProfile")
    public BaseResponseVO uploadProfile(@RequestParam String email, @RequestParam MultipartFile file) {
        //可能从token中获取email
        String url = userService.uploadProfile(email, file);
        return BaseResponseVO.success(url);
    }

    @GetMapping("/sendCode")
    public BaseResponseVO sendCode(@RequestParam String email) {
        userService.sendCode(email);
        return BaseResponseVO.success();
    }

    @PostMapping("/modifyPassword")
    public BaseResponseVO modifyPassword(@RequestBody ModificationVO modificationVO) {
        userService.modifyPassword(modificationVO);
        return BaseResponseVO.success();
    }

    //我并不想提供这个接口，懒得数据同步
    @GetMapping("/modifyUsername")
    public BaseResponseVO modifyUsername(@RequestParam String email, @RequestParam String username) {
        userService.modifyUsername(email, username);
        return BaseResponseVO.success();
    }
}
