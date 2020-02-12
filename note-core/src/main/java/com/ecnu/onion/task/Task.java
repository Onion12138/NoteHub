package com.ecnu.onion.task;

import com.ecnu.onion.domain.mongo.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author onion
 * @date 2020/1/31 -4:53 下午
 */
@Configuration
@EnableScheduling
public class Task {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final String[] field = {"star","hate","view","fork"};
    @Scheduled(cron = "0 30 23 * * ?")
    private void performTask() {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            hash.entries(field[i]).forEach((key, value) -> {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(key));
                Update update = new Update();
                update.inc(field[finalI], Integer.parseInt(value));
                mongoTemplate.updateFirst(query, update, Note.class);
            });
        }
    }
}
