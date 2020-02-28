package com.ecnu.onion.controller;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.VO.GroupRequestVO;
import com.ecnu.onion.vo.BaseResponseVO;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author onion
 * @date 2020/2/17 -8:52 下午
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GroupTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testMakeGroup() throws Exception {
        GroupRequestVO requestVO = new GroupRequestVO();
        requestVO.setOwnerEmail("969023014@qq.com");
        requestVO.setGroupName("Lady first");
        requestVO.setPartnerEmails(Lists.list("969023015@qq.com","969023016@qq.com"));
        MvcResult result = mvc.perform(post("/makeGroup").contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(requestVO)))
                .andReturn();
        BaseResponseVO responseVO = JSON.parseObject(result.getResponse().getContentAsByteArray(),
                BaseResponseVO.class);
        assertEquals(0, responseVO.getCode().intValue());
    }
//    @PostMapping("/makeGroup")
//    public BaseResponseVO makeGroup(@RequestBody GroupRequestVO requestVO) {
//        Long groupId = groupService.makeGroup(requestVO);
//        return BaseResponseVO.success(groupId);
//    }
//
//    @GetMapping("/myManagedGroups")
//    public BaseResponseVO myManagedGroups(@RequestParam String email) {
//        List<GroupInfoResult> note = groupService.findMyManagedGroups(email);
//        return BaseResponseVO.success(note);
//    }
//
//    @GetMapping("/myJoinedGroups")
//    public BaseResponseVO myJoinedGroups(@RequestParam String email) {
//        List<GroupInfoResult> note = groupService.findMyJoinedGroups(email);
//        return BaseResponseVO.success(note);
//    }
//
//    @GetMapping("/exitGroup")
//    public BaseResponseVO exitGroup(@RequestParam String email, @RequestParam Long groupId) {
//        groupService.exitGroup(email, groupId);
//        return BaseResponseVO.success();
//    }
//
//    @GetMapping("/deleteGroup")
//    public BaseResponseVO deleteGroup(@RequestParam String email, @RequestParam Long groupId) {
//        groupService.deleteGroup(email, groupId);
//        return BaseResponseVO.success();
//    }
//
//    @GetMapping("/invitePartner")
//    public BaseResponseVO invitePartner(@RequestParam String email, @RequestParam Long groupId) {
//        groupService.invitePartner(email, groupId);
//        return BaseResponseVO.success();
//    }
//
//    @GetMapping("/share")
//    public BaseResponseVO shareNotes(@RequestParam String noteId, @RequestParam Long groupId) {
//        groupService.shareNotes(noteId, groupId);
//        return BaseResponseVO.success();
//    }
//
//    @GetMapping("/groupNotes")
//    public BaseResponseVO findGroupNotes(@RequestParam Long groupId) {
//        List<NoteInfo> noteInfos = groupService.findGroupNotes(groupId);
//        return BaseResponseVO.success(noteInfos);
//    }
//
//    @GetMapping("/modifyGroupName")
//    public BaseResponseVO modifyGroupName(@RequestParam Long groupId, @RequestParam String name) {
//        groupService.modifyGroupName(groupId, name);
//        return BaseResponseVO.success();
//    }
}
