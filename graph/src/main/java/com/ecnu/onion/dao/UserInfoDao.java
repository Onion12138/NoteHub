package com.ecnu.onion.dao;

import com.ecnu.onion.domain.entity.UserInfo;
import com.ecnu.onion.result.UserFollowResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -10:48 下午
 */
@Repository
public interface UserInfoDao extends Neo4jRepository<UserInfo, String> {
    @Query("match (u:user),(n:note) " +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[v:view {viewDate:{2}}]->(n)")
    void addViewRelation(String email, String noteId, String viewDate);

    @Query("match (u:user),(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[c:collect {collectDate:{2}}]->(n)")
    void addCollectRelation(String email, String noteId, String collectDate);

    @Query("match (u:user),(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[s:star {starDate:{2}}]->(n) " +
            "on create set s.times = 1 " +
            "on match set s.times = s.times + 1")
    void addStarRelation(String email, String noteId, String starDate);

    @Query("match (u:user),(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[h:hate {hateDate:{2}}]->(n) " +
            "on create set s.times = 1 " +
            "on match set s.times = s.times + 1")
    void addHateRelation(String email, String noteId, String hateDate);

    @Query("match (u:user),(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[d:fork {forkDate:{2}}]->(n)")
    void addForkRelation(String email, String noteId, String forkDate);

    @Query("match (u1:user),(u2:user)" +
            "where u1.email = {0} and u2.email = {1} " +
            "merge (u1)-[f:follow {followDate:{2}}]->(u2)")
    void addFollowRelation(String followerEmail, String followedEmail, String followDate);

    @Query("match (u1:user)-[f:follow]->(u2:user)" +
            "where u2.email = {0} " +
            "return f.followDate as followDate, u1.email as email, u1.username as username")
    List<UserFollowResult> findMyFollowers(String email);

    @Query("match (u1:user)-[f:follow]->(u2:user)" +
            "where u1.email = {0} " +
            "return f.followDate as followDate, u2.email as email, u2.username as username")
    List<UserFollowResult> findMyFollowings(String email);

    @Query("match (u1:user)-[f:follow]->(u2:user)" +
            "where u1.email = {0} and u2.email = {1} " +
            "delete f ")
    void cancelFollowRelation(String followerEmail, String followedEmail);

    @Query("match (u:user)-[r:star|hate]->(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "return type(r)")
    List<String> checkRelation(String email, String noteId);

    @Query("match (u:user),(u1:user)" +
            "where u.email = {0} and n1.email = {1} " +
            "merge (u)-[f:friend {meetDate:{2}}]->(u1)")
    void addFriendRelation(String email, String friendEmail, String date);

    @Query("match (u:user)-[f:friend]-(u1:user)" +
            "where u.email = {0} and n1.email = {1} " +
            "delete f")
    void deleteFriendRelation(String email, String friendEmail);

    @Query("match (u:user)-[f:friend]-(u1:user) " +
            "where u.email = {0} " +
            "return u1.email")
    List<String> getMyFriends(String email);
}
