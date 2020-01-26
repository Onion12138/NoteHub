package com.ecnu.onion.dao;

import com.ecnu.onion.domain.entity.Group;
import com.ecnu.onion.result.GroupInfoResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:38 下午
 */
@Repository
public interface GroupDao extends Neo4jRepository<Group, Long> {
    @Query("match (u1:user)-[m:manage]->(g:group)<-[j:join]-(u2:user) " +
            "where u1.email = {0}" +
            "return u1 as owner, g as group, collect(u2) as partner")
    List<GroupInfoResult> findMyManagedGroups(String email);

    @Query("match (u1:user)-[m:join]->(g:group)<-[j:join]-(u2:user) " +
            "match (u3:user)-[n:manage]->(g:group) " +
            "where u1.email = {0}" +
            "return u3 as owner, g as group, collect(u2) as partner")
    List<GroupInfoResult> findMyJoinedGroups(String email);

    @Query("match (u:user)-[j:join]->(g:group) " +
            "where u.email = {0} and g.groupId = {1} " +
            "delete u, j, g")
    void exitGroup(String email, Long groupId);

    @Query("match (u1:user)-[m:manage]->(g:group)<-[j:join]-(u2:user)" +
            "where u1.email = {0} and g.groupId = {1} " +
            "delete u1, m, g, j, u2")
    void deleteGroup(String email, Long groupId);

    @Query("match (g:group),(n:note) " +
            "where g.groupId = {1} and n.noteId = {0} " +
            "merge (g)-[:share {shareDate:{2}}]->(n)")
    void shareNotes(String noteId, Long groupId, String shareDate);

    @Query("match (u:user),(g:group)" +
            "where u.email = {0} and g.groupId = {1}" +
            "merge (u)-[:manage {manageDate:{2}}]->(g)")
    void manageGroup(String ownerEmail, Long groupId, String manageDate);

    @Query("match (u:user),(g:group)" +
            "where u.email = {0} and g.groupId = {1}" +
            "merge (u)-[:join {joinDate:{2}}]->(g)")
    void joinGroup(String partnerEmail, Long groupId, String joinDate);
}
