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

    @Query("match (n1:note),(n2:note)" +
            "where n1.noteId = {0} and n2.noteId = {1}" +
            "merge (n1)-[u:update]->(n2)")
    void updateNote(String oldNoteId, String newNoteId);
}
