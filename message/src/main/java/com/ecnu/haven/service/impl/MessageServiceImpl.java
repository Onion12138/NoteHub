package com.ecnu.haven.service.impl;

import com.ecnu.haven.dao.MessageDao;
import com.ecnu.haven.domain.Message;
import com.ecnu.haven.service.MessageService;
import com.ecnu.haven.socket.WebSocket;
import com.ecnu.haven.util.FormatUtil;
import com.ecnu.haven.util.HeaderUtil;
import com.ecnu.haven.util.KeyUtil;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;
import com.ecnu.onion.vo.BaseRequestVO;
import com.ecnu.onion.vo.BaseResponseVO;
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
    private WebSocket webSocket;

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


    @Override
    public void deleteMulti(List<String> messageIds) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("userEmail").is(email)
                .and("messageId").in(messageIds));
        Update update = new Update().set("isDeleted", true);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("deleted: {}", result.getModifiedCount());
    }

    // TODO: following service

    @Override
    public void deleteAll() {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("userEmail").is(email));
        Update update = new Update().set("isDeleted", true);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("deleted: {}", result.getModifiedCount());
    }

    @Override
    public void post(MessageRequestVO messageRequestVO) {
        List<String> receiverEmails = messageRequestVO.getReceiverEmails();
        List<Message> messages = new ArrayList<>();
        for (String email : receiverEmails) {
            String messageId = KeyUtil.getUniqueKey();
            LocalDateTime current = LocalDateTime.now();
            MessageResponseVO responseVO = MessageResponseVO.builder()
                    .messageId(messageId)
                    .content(messageRequestVO.getContent())
                    .senderName(messageRequestVO.getSenderName())
                    .type(messageRequestVO.getType())
                    .isRead(false)
                    .createdAt(FormatUtil.format(current))
                    .build();
            webSocket.sendMessage(email, BaseResponseVO.success(responseVO));
            Message message = Message.builder()
                    .messageId(messageId)
                    .userEmail(email)
                    .type(messageRequestVO.getType())
                    .content(messageRequestVO.getContent())
                    .senderName(messageRequestVO.getSenderName())
                    .senderId(messageRequestVO.getSenderId())
                    .isRead(false)
                    .isDeleted(false)
                    .createdAt(current)
                    .build();
            messages.add(message);
        }
        List<Message> savedMessages = messageDao.saveAll(messages);
        log.info("saved: {}", savedMessages);
    }

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
