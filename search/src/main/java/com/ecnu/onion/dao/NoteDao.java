package com.ecnu.onion.dao;

import com.ecnu.onion.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author onion
 * @date 2020/2/29 -8:20 下午
 */
public interface NoteDao extends ElasticsearchRepository<Note, String>, ElasticsearchCrudRepository<Note, String> {
    Page<Note> findAllByTag(String tag, Pageable pageable);
    Page<Note> findAllByEmail(String email, Pageable pageable);

}
