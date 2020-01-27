package com.ecnu.onion.dao;

import com.ecnu.onion.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author onion
 * @date 2020/1/27 -10:56 上午
 */
public interface UserDao extends MongoRepository<User, String> {
    User findByActiveCode(String activeCode);
}
