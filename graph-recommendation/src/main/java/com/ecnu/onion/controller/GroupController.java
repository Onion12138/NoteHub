package com.ecnu.onion.controller;

import com.ecnu.onion.VO.GroupRequestVO;
import com.ecnu.onion.result.GroupInfoResult;
import com.ecnu.onion.service.GroupService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:36 下午
 */
@RestController
@RequestMapping("/graph/group")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @PostMapping("/makeGroup")
    public BaseResponseVO makeGroup(@RequestBody GroupRequestVO requestVO) {
        groupService.makeGroup(requestVO);
        return BaseResponseVO.success();
    }

    @GetMapping("/myManagedGroups")
    public BaseResponseVO myManagedGroups(@RequestParam String email) {
        List<GroupInfoResult> note = groupService.findMyManagedGroups(email);
        return BaseResponseVO.success(note);
    }

    @GetMapping("/myJoinedGroups")
    public BaseResponseVO myJoinedGroups(@RequestParam String email) {
        List<GroupInfoResult> note = groupService.findMyJoinedGroups(email);
        return BaseResponseVO.success(note);
    }

    @GetMapping("/exitGroup")
    public BaseResponseVO exitGroup(@RequestParam String email, @RequestParam Long groupId) {
        groupService.exitGroup(email, groupId);
        return BaseResponseVO.success();
    }

    @GetMapping("/deleteGroup")
    public BaseResponseVO deleteGroup(@RequestParam String email, @RequestParam Long groupId) {
        groupService.deleteGroup(email, groupId);
        return BaseResponseVO.success();
    }

    @GetMapping("/invitePartner")
    public BaseResponseVO invitePartner(@RequestParam String email, @RequestParam Long groupId) {
        groupService.invitePartner(email, groupId);
        return BaseResponseVO.success();
    }

    @GetMapping("/share")
    public BaseResponseVO shareNotes(@RequestParam String noteId, @RequestParam Long groupId) {
        groupService.shareNotes(noteId, groupId);
        return BaseResponseVO.success();
    }
}
