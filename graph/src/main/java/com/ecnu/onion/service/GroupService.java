package com.ecnu.onion.service;

import com.ecnu.onion.VO.GroupRequestVO;
import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.result.GroupInfoResult;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:37 下午
 */
public interface GroupService {
    Long makeGroup(GroupRequestVO requestVO);

    List<GroupInfoResult> findMyManagedGroups(String email);

    List<GroupInfoResult> findMyJoinedGroups(String email);

    void exitGroup(String email, Long groupId);

    void deleteGroup(String email, Long groupId);

    void invitePartner(String email, Long groupId);

    void shareNotes(String noteId, Long groupId);

    List<NoteInfo> findGroupNotes(Long groupId);

    void modifyGroupName(Long groupId, String name);
}
