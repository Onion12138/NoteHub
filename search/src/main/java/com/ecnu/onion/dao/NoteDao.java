package com.ecnu.onion.dao;

import com.ecnu.onion.domain.Note;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author onion
 * @date 2020/1/27 -11:04 上午
 */
@Repository
public interface NoteDao extends ElasticsearchCrudRepository<Note, String> {
}
