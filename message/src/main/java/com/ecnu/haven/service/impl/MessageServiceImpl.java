package com.ecnu.haven.service.impl;

import com.ecnu.haven.dao.MessageDao;
import com.ecnu.haven.domain.Message;
import com.ecnu.haven.service.MessageService;
import com.ecnu.haven.util.FormatUtil;
import com.ecnu.haven.util.HeaderUtil;
import com.ecnu.haven.util.KeyUtil;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;
import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.desktop.QuitResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HavenTong
 * @date 2020/2/20 4:23 下午
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<MessageResponseVO> findAllMessages() {
        String userEmail = HeaderUtil.getEmail();
        List<Message> messages
                = messageDao.findAllByUserEmailAndIsDeleted(userEmail,
                                                            false,
                                                            Sort.by(Sort.Direction.DESC, "createdAt"));
        List<MessageResponseVO> responseVOList = new ArrayList<>();
        for (Message message : messages) {
            MessageResponseVO response = new MessageResponseVO(
                    message.getMessageId(),
                    message.getType(),
                    message.getContent(),
                    message.getSenderName(),
                    message.getIsRead(),
                    FormatUtil.format(message.getCreatedAt())
            );
            responseVOList.add(response);
        }
        log.info("list: {}", responseVOList);
        return responseVOList;
    }

    @Override
    public void changeStatus(String messageId, String type) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("messageId").is(messageId)
                .and("userEmail").is(email));
        Update update = new Update();
        if ("Read".equals(type)) {
            update.set("isRead", true);
        } else {
            update.set("isRead", false);
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, Message.class);
        log.info("modified: {}", result.getModifiedCount());
    }

    @Override
    public void changeMultiStatus(List<String> messageIds, String type) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("messageId").in(messageIds)
                .and("userEmail").is(email));
        Update update = new Update();
        if ("Read".equals(type)) {
            update.set("isRead", true);
        } else {
            update.set("isRead", false);
        }
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("modified: {}", result.getModifiedCount());
    }


    @Override
    public void changeAllToRead() {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("userEmail").is(email));
        Update update = new Update().set("isRead", true);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("modified: {}", result.getModifiedCount());
    }

    @Override
    public void delete(String messageId) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("userEmail").is(email)
                .and("messageId").is(messageId));
        Update update = new Update().set("isDeleted", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, Message.class);
        log.info("deleted: {}", result.getModifiedCount());
    }

    // TODO: following service

    @Override
    public void saveMessage(MessageRequestVO messageRequestVO) {
        Message message = Message.builder()
                .messageId(KeyUtil.getUniqueKey())
                .userEmail(HeaderUtil.getEmail())
                .type(messageRequestVO.getType())
                .senderId(messageRequestVO.getSenderId())
                .senderName(messageRequestVO.getSenderName())
                .content(messageRequestVO.getContent())
                .isDeleted(false)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        messageDao.save(message);
    }
}
