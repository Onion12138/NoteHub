package com.ecnu.onion.dao;

import com.ecnu.onion.domain.entity.NoteInfo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:10 下午
 */
@Repository
public interface NoteInfoDao extends Neo4jRepository<NoteInfo, String> {
    @Query("match (note1:note)-[r:relate]-(note2:note)" +
            "where note1.noteId = {0} " +
            "return note2 " +
            "order by r.relatedTimes " +
            "limit 20")
    List<NoteInfo> findNearestRelatedNotes(String noteId);

    @Query("match (note1:note)-[r:relate*2..3]-(note2:note)" +
            "where note1.noteId = {0} " +
            "return note2 " +
            "limit 10")
    List<NoteInfo> findNearbyRelatedNotes(String noteId);

    @Query("MATCH (n1:note),(n2:note) " +
            "WHERE n1.noteId = {0} AND n2.noteId = {1} " +
            "MERGE (n1)-[r:relate]-(n2) " +
            "ON CREATE SET r.relateTimes = 1 " +
            "ON MATCH SET r.relateTimes = r.relateTimes + 1")
    void addRelation(String NoteId1, String noteId2);

    @Query("match (u:user)-[p1:publish]->(n1:note)" +
            "where n1.noteId = {0} delete p1 " +
            "create (n2:note {noteId:{1},title:{2},publishTime:{3}})" +
            "merge (u:user)-[p2:publish {publishTime:{3}}]->(n2)" +
            "merge (n1)-[up:update {updateTime:{3}}]->(n2)")
    void updateNote(String oldNoteId, String newNoteId, String title, String publishTime);

    @Query("match (n1:note)-[:update*0..]-(n2:note)" +
            "where n1.noteId = {1} and ()-[:publish]->(n2)" +
            "return n2.noteId")
    String jumpToLatest(String noteId);

    @Query("match (u:user)-[p:publish]->(n1:note),(n2:note)" +
            "where n1.noteId = {0} and n2.noteId = {1}" +
            "delete p " +
            "merge (u)-[p2:publish {publishTime:{2}}]->(n2)")
    void rollback(String currentVersion, String rollbackVersion, String publishTime);

    @Query("match (n1:note)-[:update*]-(n2:note)" +
            "where n1.id = {0} " +
            "return n2")
    List<NoteInfo> historyVersion(String noteId);

    @Query("match (:user)-[p:publish]-(n:note)" +
            "where n.noteId = {0} " +
            "delete p")
    void deleteNote(String noteId);
}
