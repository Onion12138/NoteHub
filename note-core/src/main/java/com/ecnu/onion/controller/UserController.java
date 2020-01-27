package com.ecnu.onion.controller;

import com.ecnu.onion.service.UserService;
import com.ecnu.onion.vo.BaseResponseVO;
import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author onion
 * @date 2020/1/27 -9:38 上午
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/sendEmail")
    public BaseResponseVO sendEmail(@RequestParam String email) {
        userService.sendEmail(email);
        return BaseResponseVO.success();
    }

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
        userService.login(loginVO);
        return BaseResponseVO.success();
    }
}
