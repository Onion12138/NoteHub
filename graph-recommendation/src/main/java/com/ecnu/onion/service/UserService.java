package com.ecnu.onion.service;

import com.ecnu.onion.domain.entity.UserInfo;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/25 -3:29 下午
 */
public interface UserService {
    void addViewRelation(String email, String noteId);

    void addCollectRelation(String email, String noteId);

    void addStarRelation(String email, String noteId);

    void addHateRelation(String email, String noteId);

    void addForkRelation(String email, String noteId);

    void addFollowRelation(String followerEmail, String followedEmail);

    List<UserInfo> findMyFollowers(String email);

    List<UserInfo> findMyFollowings(String email);

    void addPublishRelation(String email, String noteId, String title);

    void cancelFollowRelation(String followerEmail, String followedEmail);

    void addUser(String email, String username);
}
