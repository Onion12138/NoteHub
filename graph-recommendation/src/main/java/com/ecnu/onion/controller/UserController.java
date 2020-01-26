package com.ecnu.onion.controller;

import com.ecnu.onion.domain.entity.UserInfo;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/25 -3:10 下午
 */
@RestController
@RequestMapping("/graph/user")
public class UserController{
    @Autowired
    private UserService userService;

    @GetMapping("/viewNote")
    public BaseResponseVO addViewRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addViewRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/collectNote")
    public BaseResponseVO addCollectRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addCollectRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/starNote")
    public BaseResponseVO addStarRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addStarRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/hateNote")
    public BaseResponseVO addHateRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addHateRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/downloadNote")
    public BaseResponseVO addDownloadRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addDownloadRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/follow")
    public BaseResponseVO addFollowRelation(@RequestParam String followerEmail, @RequestParam String followedEmail) {
        userService.addFollowRelation(followerEmail, followedEmail);
        return BaseResponseVO.success();
    }

    @GetMapping("/myFollowers")
    public BaseResponseVO findMyFollowers(@RequestParam String email) {
        List<String> userId = userService.findMyFollowers(email);
        return BaseResponseVO.success();
    }

    @GetMapping("/myFollowing")
    public BaseResponseVO findMyFollowings(@RequestParam String email) {
        List<String> userId = userService.findMyFollowings(email);
        return BaseResponseVO.success();
    }
}
