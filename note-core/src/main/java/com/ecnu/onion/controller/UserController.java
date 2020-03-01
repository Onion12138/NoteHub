package com.ecnu.onion.controller;

import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import com.ecnu.onion.domain.mongo.Tag;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.utils.AuthUtil;
import com.ecnu.onion.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @PostMapping("/login")
    public BaseResponseVO login(@RequestBody LoginVO loginVO){
        Map<String, Object> map = userService.login(loginVO);
        return BaseResponseVO.success(map);
    }

    @PostMapping("/uploadProfile")
    public BaseResponseVO uploadProfile(@RequestParam MultipartFile file) {
        String email = AuthUtil.getEmail();
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
        String email = AuthUtil.getEmail();
        userService.modifyPassword(email, modificationVO);
        return BaseResponseVO.success();
    }

    @PostMapping("/modifyUsername")
    public BaseResponseVO modifyUsername(@RequestParam String username) {
        String email = AuthUtil.getEmail();
        userService.modifyUsername(email, username);
        return BaseResponseVO.success();
    }

    @PostMapping("/collectNote")
    public BaseResponseVO collectNote(@RequestBody CollectNote collectNote){
        String email = AuthUtil.getEmail();
        userService.collectNote(email, collectNote);
        return BaseResponseVO.success();
    }

    @PostMapping("/chooseTags")
    public BaseResponseVO chooseTags(@RequestParam String tagStr) {
        String email = AuthUtil.getEmail();
        Set<String> tags = new HashSet<>(Set.of(tagStr.split(" ")));
        userService.chooseTags(email, tags);
        return BaseResponseVO.success();
    }

    @PostMapping("/cancelCollectNote")
    public BaseResponseVO cancelCollectNote(@RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        userService.cancelCollectNote(email, noteId);
        return BaseResponseVO.success();
    }

    @PostMapping("/addIndex")
    public BaseResponseVO addIndex(@RequestParam String mindMap) {
        String email = AuthUtil.getEmail();
        userService.addIndex(email, mindMap);
        return BaseResponseVO.success();
    }

    @PostMapping("/updateIndex")
    public BaseResponseVO updateIndex(@RequestParam Map<String,String> map) {
        String email = AuthUtil.getEmail();
        userService.updateIndex(email, map);
        return BaseResponseVO.success();
    }

    @GetMapping("/findOneIndex")
    public BaseResponseVO findOneIndex(@RequestParam Integer num) {
        String email = AuthUtil.getEmail();
        MindMap mindMap = userService.findOneIndex(email, num);
        return BaseResponseVO.success(mindMap);
    }

    @PostMapping("/deleteIndex")
    public BaseResponseVO deleteIndex(@RequestParam Integer num) {
        String email = AuthUtil.getEmail();
        userService.deleteIndex(email, num);
        return BaseResponseVO.success();
    }

    @GetMapping("/findUser")
    public BaseResponseVO findUser(@RequestParam String email) {
        UserVO userVO = userService.findUser(email);
        return BaseResponseVO.success(userVO);
    }

    @GetMapping("/findTag")
    public BaseResponseVO findTag() {
        List<Tag> tagList = userService.findTag();
        return BaseResponseVO.success(tagList);
    }
}
