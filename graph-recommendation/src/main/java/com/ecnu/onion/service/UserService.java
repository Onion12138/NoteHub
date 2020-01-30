package com.ecnu.onion.service;

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

    List<String> findMyFollowers(String email);

    List<String> findMyFollowings(String email);

    void addPublishRelation(String email, String noteId);
}
