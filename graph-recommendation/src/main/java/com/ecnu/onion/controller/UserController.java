package com.ecnu.onion.controller;

import com.ecnu.onion.result.UserFollowResult;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.util.AuthUtil;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/viewNote")
    public BaseResponseVO addViewRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addViewRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @PostMapping("/collectNote")
    public BaseResponseVO addCollectRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addCollectRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @PostMapping("/starNote")
    public BaseResponseVO addStarRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addStarRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @PostMapping("/hateNote")
    public BaseResponseVO addHateRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addHateRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @PostMapping("/forkNote")
    public BaseResponseVO addForkRelation(@RequestParam String email, @RequestParam String noteId) {
        userService.addForkRelation(email, noteId);
        return BaseResponseVO.success();
    }

    @PostMapping("/follow")
    public BaseResponseVO addFollowRelation(@RequestParam String followedEmail) {
        String email = AuthUtil.getEmail();
        userService.addFollowRelation(email, followedEmail);
        return BaseResponseVO.success();
    }

    @PostMapping("/unfollow")
    public BaseResponseVO cancelFollowRelation(@RequestParam String followedEmail) {
        String email = AuthUtil.getEmail();
        userService.cancelFollowRelation(email, followedEmail);
        return BaseResponseVO.success();
    }

    @GetMapping("/myFollowers")
    public BaseResponseVO findMyFollowers() {
        String email = AuthUtil.getEmail();
        List<UserFollowResult> userId = userService.findMyFollowers(email);
        return BaseResponseVO.success(userId);
    }

    @GetMapping("/myFollowings")
    public BaseResponseVO findMyFollowings() {
        String email = AuthUtil.getEmail();
        List<UserFollowResult> users = userService.findMyFollowings(email);
        return BaseResponseVO.success(users);
    }

    @GetMapping("/checkRelation")
    public BaseResponseVO checkRelation(@RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        List<String> relation = userService.checkRelation(email, noteId);
        return BaseResponseVO.success(relation);
    }

}
