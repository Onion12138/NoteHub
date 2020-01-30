package com.ecnu.onion.dao;

import com.ecnu.onion.domain.mongo.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author onion
 * @date 2020/1/26 -4:05 下午
 */
public interface NoteDao extends MongoRepository<Note, String> {
}
