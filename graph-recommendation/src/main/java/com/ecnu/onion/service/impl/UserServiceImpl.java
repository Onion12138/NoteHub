package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.UserInfoDao;
import com.ecnu.onion.domain.entity.UserInfo;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.task.NoteRelationTask;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * @author onion
 * @date 2020/1/25 -3:31 下午
 */
@Service
@RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = MQConstant.GRAPH_USER_QUEUE),
                exchange = @Exchange(value = MQConstant.EXCHANGE, type = "topic"))
})
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void addViewRelation(String email, String noteId) {
        userInfoDao.addViewRelation(email, noteId, LocalDate.now().toString());
        redisTemplate.opsForSet().add(NoteRelationTask.KEY, email);
    }

    @Override
    public void addCollectRelation(String email, String noteId) {
        userInfoDao.addCollectRelation(email, noteId, LocalDate.now().toString());
    }

    @Override
    public void addStarRelation(String email, String noteId) {
        userInfoDao.addStarRelation(email, noteId, LocalDate.now().toString());
    }

    @Override
    public void addHateRelation(String email, String noteId) {
        userInfoDao.addHateRelation(email, noteId, LocalDate.now().toString());
    }

    @Override
    public void addForkRelation(String email, String noteId) {
        userInfoDao.addForkRelation(email, noteId, LocalDate.now().toString());
    }

    @Override
    public void addFollowRelation(String followerEmail, String followedEmail) {
        userInfoDao.addFollowRelation(followerEmail, followedEmail, LocalDate.now().toString());
    }

    @Override
    public List<String> findMyFollowers(String email) {
        return userInfoDao.findMyFollowers(email);
    }

    @Override
    public List<String> findMyFollowings(String email) {
        return userInfoDao.findMyFollowings(email);
    }

    @Override
    public void addPublishRelation(String email, String noteId) {
        userInfoDao.addPublishRelation(email, noteId);
    }


    @RabbitHandler
    private void addOrUpdateUsers(String message) {
        String[] data = message.split("#");
        Arrays.stream(data).forEach(e-> userInfoDao.save(JSON.parseObject(e, UserInfo.class)));
    }

}
