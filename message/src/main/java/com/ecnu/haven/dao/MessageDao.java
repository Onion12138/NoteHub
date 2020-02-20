package com.ecnu.haven.dao;

import com.ecnu.haven.domain.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HavenTong
 * @date 2020/2/15 12:24 下午
 */
@Repository
public interface MessageDao extends MongoRepository<Message, String> {

    List<Message> findAllByUserEmailAndIsDeleted(String userEmail, boolean isDeleted, Sort sort);

}
