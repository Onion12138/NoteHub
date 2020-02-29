package com.ecnu.onion.dao;

import com.ecnu.onion.domain.mongo.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author onion
 * @date 2020/2/28 -5:36 下午
 */
@Repository
public interface TagDao extends MongoRepository<Tag, Integer> {
}
