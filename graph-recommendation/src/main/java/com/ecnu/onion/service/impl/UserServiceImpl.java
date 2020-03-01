package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.UserInfoDao;
import com.ecnu.onion.domain.entity.UserInfo;
import com.ecnu.onion.result.UserFollowResult;
import com.ecnu.onion.service.UserService;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public void addViewRelation(String email, String noteId) {
        userInfoDao.addViewRelation(email, noteId, LocalDate.now().toString());
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
    public List<UserFollowResult> findMyFollowers(String email) {
        List<UserFollowResult> results = userInfoDao.findMyFollowers(email);
        results.sort((o1, o2) -> - o1.getFollowDate().compareTo(o2.getFollowDate()));
        return results;
    }

    @Override
    public List<UserFollowResult> findMyFollowings(String email) {
        List<UserFollowResult> results = userInfoDao.findMyFollowings(email);
        results.sort((o1, o2) -> - o1.getFollowDate().compareTo(o2.getFollowDate()));
        return results;
    }

    @Override
    public void cancelFollowRelation(String followerEmail, String followedEmail) {
        userInfoDao.cancelFollowRelation(followerEmail, followedEmail);
    }

    @Override
    public List<String> checkRelation(String email, String noteId) {
        return userInfoDao.checkRelation(email, noteId);
    }

    @Override
    public void addFriend(String email, String friendEmail) {
        userInfoDao.addFriendRelation(email, friendEmail, LocalDate.now().toString());
    }

    @Override
    public void deleteFriend(String email, String friendEmail) {
        userInfoDao.deleteFriendRelation(email, friendEmail);
    }

    @Override
    public List<String> getMyFriends(String email) {
        return userInfoDao.getMyFriends(email);
    }

    @RabbitHandler
    private void addUser(String message) {
        UserInfo userInfo = JSON.parseObject(message, UserInfo.class);
        userInfoDao.save(userInfo);
    }


}
