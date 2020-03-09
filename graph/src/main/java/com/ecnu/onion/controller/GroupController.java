package com.ecnu.onion.controller;

import com.ecnu.onion.VO.GroupRequestVO;
import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.result.GroupInfoResult;
import com.ecnu.onion.service.GroupService;
import com.ecnu.onion.util.AuthUtil;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:36 下午
 */
@RestController
@RequestMapping("/group")
public class  GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/makeGroup")
    public BaseResponseVO makeGroup(@RequestBody GroupRequestVO requestVO) {
        Long groupId = groupService.makeGroup(requestVO);
        return BaseResponseVO.success(groupId);
    }

    @GetMapping("/myManagedGroups")
    public BaseResponseVO myManagedGroups() {
        String email = AuthUtil.getEmail();
        List<GroupInfoResult> note = groupService.findMyManagedGroups(email);
        return BaseResponseVO.success(note);
    }

    @GetMapping("/myJoinedGroups")
    public BaseResponseVO myJoinedGroups() {
        String email = AuthUtil.getEmail();
        List<GroupInfoResult> note = groupService.findMyJoinedGroups(email);
        return BaseResponseVO.success(note);
    }

    @PostMapping("/exitGroup")
    public BaseResponseVO exitGroup(@RequestParam Long groupId) {
        String email = AuthUtil.getEmail();
        groupService.exitGroup(email, groupId);
        return BaseResponseVO.success();
    }

    @PostMapping("/deleteGroup")
    public BaseResponseVO deleteGroup(@RequestParam Long groupId) {
        String email = AuthUtil.getEmail();
        groupService.deleteGroup(email, groupId);
        return BaseResponseVO.success();
    }

    @PostMapping("/invitePartner")
    public BaseResponseVO invitePartner(@RequestParam String email, @RequestParam Long groupId) {
        groupService.invitePartner(email, groupId);
        return BaseResponseVO.success();
    }

    @PostMapping("/share")
    public BaseResponseVO shareNotes(@RequestParam String noteId, @RequestParam Long groupId) {
        groupService.shareNotes(noteId, groupId);
        return BaseResponseVO.success();
    }

    @GetMapping("/groupNotes")
    public BaseResponseVO findGroupNotes(@RequestParam Long groupId) {
        List<NoteInfo> noteInfos = groupService.findGroupNotes(groupId);
        return BaseResponseVO.success(noteInfos);
    }

    @PostMapping("/modifyGroupName")
    public BaseResponseVO modifyGroupName(@RequestParam Long groupId, @RequestParam String name) {
        groupService.modifyGroupName(groupId, name);
        return BaseResponseVO.success();
    }
}
