package com.ecnu.onion.service.impl;

import com.ecnu.onion.VO.GroupRequestVO;
import com.ecnu.onion.dao.GroupDao;
import com.ecnu.onion.domain.entity.Group;
import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.result.GroupInfoResult;
import com.ecnu.onion.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:38 下午
 */
@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDao groupDao;
    @Override
    public Long makeGroup(GroupRequestVO requestVO) {
        Group group = Group.builder().groupName(requestVO.getGroupName())
                .createTime(LocalDate.now().toString()).build();
        group = groupDao.save(group);
        final Long groupId = group.getGroupId();
        log.info("id:{}",groupId);
        groupDao.manageGroup(requestVO.getOwnerEmail(), groupId, LocalDate.now().toString());
        requestVO.getPartnerEmails().forEach(e->groupDao.joinGroup(e, groupId, LocalDate.now().toString()));
        return groupId;
    }

    @Override
    public List<GroupInfoResult> findMyManagedGroups(String email) {
        return groupDao.findMyManagedGroups(email);
    }

    @Override
    public List<GroupInfoResult> findMyJoinedGroups(String email) {
        return groupDao.findMyJoinedGroups(email);
    }

    @Override
    public void exitGroup(String email, Long groupId) {
        groupDao.exitGroup(email, groupId);
    }

    @Override
    public void deleteGroup(String email, Long groupId) {
        groupDao.deleteGroup(email, groupId);
    }

    @Override
    public void invitePartner(String email, Long groupId) {
        groupDao.joinGroup(email, groupId, LocalDate.now().toString());
    }

    @Override
    public void shareNotes(String noteId, Long groupId) {
        groupDao.shareNotes(noteId, groupId, LocalDate.now().toString());
    }

    @Override
    public List<NoteInfo> findGroupNotes(Long groupId) {
        return groupDao.findGroupNotes(groupId);
    }

    @Override
    public void modifyGroupName(Long groupId, String name) {
        groupDao.modifyGroupName(groupId, name);
    }
}
