package com.ecnu.onion.dao;

import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.domain.entity.UserInfo;
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
            "merge (u)-[s:star {starDate:{2}}]->(n)")
    void addStarRelation(String email, String noteId, String starDate);

    @Query("match (u:user),(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[h:hate {hateDate:{2}}]->(n)")
    void addHateRelation(String email, String noteId, String hateDate);

    @Query("match (u:user),(n:note)" +
            "where u.email = {0} and n.noteId = {1} " +
            "merge (u)-[d:download {downloadDate:{2}}]->(n)")
    void addDownloadRelation(String email, String noteId, String downloadDate);

    @Query("match (u1:user),(u2:user)" +
            "where u1.email = {0} and u2.email = {1} " +
            "merge (u1)-[f:follow {followDate:{2}}]->(u2)")
    void addFollowRelation(String followerEmail, String followedEmail, String followDate);

    @Query("match (u1:user)-[f:follow]->(u2:user)" +
            "where u2.email = {0} " +
            "return u1.email")
    List<String> findMyFollowers(String email);

    @Query("match (u1:user)-[f:follow]->(u2:user)" +
            "where u1.email = {0} " +
            "return u2.email")
    List<String> findMyFollowings(String email);

    @Query("match (u:user)-[v:view]->(n:note)" +
            "where u.email = {0} and v.viewDate = {1} " +
            "return n")
    List<NoteInfo> findViewedNote(String email, String viewDate);
}
