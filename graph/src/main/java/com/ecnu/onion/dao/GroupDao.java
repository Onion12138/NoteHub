package com.ecnu.onion.dao;

import com.ecnu.onion.domain.entity.Group;
import com.ecnu.onion.domain.entity.NoteInfo;
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
            "return u1.email as owner, g as group, collect(u2.email) as partner")
    List<GroupInfoResult> findMyManagedGroups(String email);

    @Query("match (u1:user)-[m:join]->(g:group)<-[j:join]-(u2:user) " +
            "match (u3:user)-[n:manage]->(g:group) " +
            "where u1.email = {0}" +
            "return u3.email as owner, g as group, collect(u2.email) as partner")
    List<GroupInfoResult> findMyJoinedGroups(String email);

    @Query("match (u:user)-[j:join]->(g:group) " +
            "where u.email = {0} and id(g) = {1} " +
            "delete j")
    void exitGroup(String email, Long groupId);

    @Query("match (u1:user)-[m:manage]->(g:group)<-[j:join]-(u2:user)" +
            "where u1.email = {0} and id(g) = {1} " +
            "delete m, g, j")
    void deleteGroup(String email, Long groupId);

    @Query("match (g:group),(n:note) " +
            "where id(g) = {1} and n.noteId = {0} " +
            "merge (g)-[:share {shareDate:{2}}]->(n)")
    void shareNotes(String noteId, Long groupId, String shareDate);

    @Query("match (u:user),(g:group)" +
            "where u.email = {0} and id(g) = {1}" +
            "merge (u)-[:manage {manageDate:{2}}]->(g)")
    void manageGroup(String ownerEmail, Long groupId, String manageDate);

    @Query("match (u:user),(g:group)" +
            "where u.email = {0} and id(g) = {1}" +
            "merge (u)-[:join {joinDate:{2}}]->(g)")
    void joinGroup(String partnerEmail, Long groupId, String joinDate);

    @Query("match (g:group)-[:share]-(n:note) " +
            "where id(g) = {0} " +
            "return n")
    List<NoteInfo> findGroupNotes(Long groupId);

    @Query("match (g:group) where id(g) = {0} " +
            "set g.groupName = {1}")
    void modifyGroupName(Long groupId, String name);
}
