package com.ecnu.onion.dao;

import com.ecnu.onion.domain.entity.NoteInfo;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author onion
 * @date 2020/2/28 -3:20 下午
 */
public interface NoteInfoDao extends Neo4jRepository<NoteInfo, String> {
}
