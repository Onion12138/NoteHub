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
@RequestMapping("/user")
public class UserController{
    @Autowired
    private UserService userService;

    @GetMapping("/publishNote")
    public BaseResponseVO addPublishRelation(@RequestParam String email,
                                             @RequestParam String noteId, @RequestParam String title) {
        userService.addPublishRelation(email, noteId, title);
        return BaseResponseVO.success();
    }

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

    @GetMapping("/forkNote")
    public BaseResponseVO addForkRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addForkRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/follow")
    public BaseResponseVO addFollowRelation(@RequestParam String followerEmail, @RequestParam String followedEmail) {
        userService.addFollowRelation(followerEmail, followedEmail);
        return BaseResponseVO.success();
    }

    @GetMapping("/unfollow")
    public BaseResponseVO cancelFollowRelation(@RequestParam String followerEmail, @RequestParam String followedEmail) {
        userService.cancelFollowRelation(followerEmail, followedEmail);
        return BaseResponseVO.success();
    }

    @GetMapping("/myFollowers")
    public BaseResponseVO findMyFollowers(@RequestParam String email) {
        List<UserInfo> userId = userService.findMyFollowers(email);
        return BaseResponseVO.success(userId);
    }

    @GetMapping("/myFollowings")
    public BaseResponseVO findMyFollowings(@RequestParam String email) {
        List<UserInfo> users = userService.findMyFollowings(email);
        return BaseResponseVO.success(users);
    }

    @GetMapping("/addUser")
    public BaseResponseVO addUser(@RequestParam String email, @RequestParam String username) {
        userService.addUser(email, username);
        return BaseResponseVO.success();
    }
}
