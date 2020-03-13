package com.ecnu.haven.service.impl;

import com.ecnu.haven.dao.MessageDao;
import com.ecnu.haven.domain.Message;
import com.ecnu.haven.service.MessageService;
import com.ecnu.haven.socket.WebSocket;
import com.ecnu.haven.util.HeaderUtil;
import com.ecnu.haven.util.KeyUtil;
import com.ecnu.haven.vo.MessageListVO;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;

import com.ecnu.onion.enums.MessageType;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    public List<MessageListVO> findMessageList() {
        String email = HeaderUtil.getEmail();
        Aggregation aggregationForReceive = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("receiverEmail").is(email).and("isDeleted").is(false).and("type").is(MessageType.CHAT)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")),
                Aggregation.group("senderEmail")
                        .sum("notRead").as("sum")
                .first("senderEmail").as("senderEmail")
                .first("receiverEmail").as("receiverEmail")
                .first("content").as("content")
                .first("createdAt").as("createdAt"),
                Aggregation.lookup("user", "senderEmail", "_id", "userDoc"),
                Aggregation.project( "senderEmail",
                        "receiverEmail", "content", "userDoc.username", "userDoc.profileUrl", "sum", "createdAt")
        );
        AggregationResults<Message> receiveMessageResult = mongoTemplate.aggregate(aggregationForReceive, Message.class, Message.class);
        log.info("receiveList: {}", receiveMessageResult.getMappedResults());
        Aggregation aggregationForSend = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("senderEmail").is(email).and("isDeleted").is(false).and("type").is(MessageType.CHAT)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")),
                Aggregation.group("receiverEmail")
                        .first("senderEmail").as("senderEmail")
                        .first("receiverEmail").as("receiverEmail")
                        .first("content").as("content")
                        .first("createdAt").as("createdAt"),
                Aggregation.lookup("user", "receiverEmail", "_id", "userDoc"),
                Aggregation.project( "senderEmail",
                        "receiverEmail", "content", "userDoc.username", "userDoc.profileUrl", "createdAt")
        );
        AggregationResults<Message> sendMessageResult = mongoTemplate.aggregate(aggregationForSend, Message.class, Message.class);
        log.info("sendList: {}", sendMessageResult.getMappedResults());
        List<Message> totalList = new ArrayList<>(sendMessageResult.getMappedResults());
        totalList.addAll(receiveMessageResult.getMappedResults());
        totalList.sort((Message m1, Message m2)
                -> (m2.getCreatedAt().compareTo(m1.getCreatedAt())));
        Set<MessageListVO> messageSet = new LinkedHashSet<>();
        for (Message message : totalList) {
            MessageListVO messageListVO = MessageListVO.builder()
                    .receiverEmail(message.getReceiverEmail())
                    .senderEmail(message.getSenderEmail())
                    .content(message.getContent())
                    .sum(Objects.isNull(message.getSum()) ? 0 : message.getSum())
                    .username(message.getUsername())
                    .profileUrl(message.getProfileUrl())
                    .createdAt(message.getCreatedAt())
                    .build();
            messageSet.add(messageListVO);
        }
        return new ArrayList<>(messageSet);
    }

    @Override
    public List<MessageResponseVO> findHistoryChat(String senderEmail) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("receiverEmail").is(email)
                        .and("senderEmail").is(senderEmail),
                Criteria.where("receiverEmail").is(senderEmail)
                        .and("senderEmail").is(email))
        .and("isDeleted").is(false).and("type").is(MessageType.CHAT));
        List<Message> messages = mongoTemplate.find(query, Message.class);
        List<MessageResponseVO> responseVOList = new ArrayList<>();
        for (Message message : messages) {
            Boolean notRead = message.getNotRead().equals(1);
            MessageResponseVO responseVO = MessageResponseVO.builder()
                    .messageId(message.getMessageId())
                    .content(message.getContent())
                    .receiverEmail(message.getReceiverEmail())
                    .senderEmail(message.getSenderEmail())
                    .notRead(notRead)
                    .createdAt(message.getCreatedAt())
                    .build();
            responseVOList.add(responseVO);
        }
        return responseVOList;
    }

    @Override
    public void clearUnreadMessage(String senderEmail) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("senderEmail").is(senderEmail)
                .and("receiverEmail").is(email)
                .and("type").is(MessageType.CHAT)
                .and("isDeleted").is(false));
        Update update = new Update().set("notRead", 0);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("clear unread message: {}", result.getModifiedCount());
    }

    @Override
    public void changeStatus(String messageId, String type) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("messageId").is(messageId)
                .and("receiverEmail").is(email));
        Update update = new Update();
        if ("Read".equals(type)) {
            update.set("notRead", 0);
        } else {
            update.set("notRead", 1);
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, Message.class);
        log.info("modified: {}", result.getModifiedCount());
    }

    @Override
    public void changeMultiStatus(List<String> messageIds, String type) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("messageId").in(messageIds)
                .and("receiverEmail").is(email));
        Update update = new Update();
        if ("Read".equals(type)) {
            update.set("notRead", 0);
        } else {
            update.set("notRead", 1);
        }
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("modified: {}", result.getModifiedCount());
    }


    @Override
    public void changeAllToRead() {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("receiverEmail").is(email));
        Update update = new Update().set("notRead", 0);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("modified: {}", result.getModifiedCount());
    }

    @Override
    public void delete(String messageId) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("receiverEmail").is(email)
                .and("messageId").is(messageId));
        Update update = new Update().set("isDeleted", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, Message.class);
        log.info("deleted: {}", result.getModifiedCount());
    }


    @Override
    public void deleteMulti(List<String> messageIds) {
        String email = HeaderUtil.getEmail();
        Query query = new Query(Criteria.where("receiverEmail").is(email)
                .and("messageId").in(messageIds));
        Update update = new Update().set("isDeleted", true);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("deleted: {}", result.getModifiedCount());
    }

    // TODO: following service

    @Override
    public void deleteAll() {
        String email = HeaderUtil.getEmail();
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("receiverEmail").is(email),
                Criteria.where("senderEmail").is(email)
        ).and("type").is(MessageType.CHAT));
        Update update = new Update().set("isDeleted", true);
        UpdateResult result = mongoTemplate.updateMulti(query, update, Message.class);
        log.info("deleted: {}", result.getModifiedCount());
    }

    @Override
    public void sendMessage(String receiverEmail, String content) {
        String notify = "您已收到一条新的私信";
        String email = HeaderUtil.getEmail();
        Message message = Message.builder()
                .messageId(KeyUtil.getUniqueKey())
                .senderEmail(email)
                .receiverEmail(receiverEmail)
                .type(MessageType.CHAT)
                .content(content)
                .notRead(1)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        messageDao.save(message);
        webSocket.sendMessage(receiverEmail, notify);
    }

    @Override
    public void saveMessage(MessageRequestVO messageRequestVO) {
        Message message = Message.builder()
                .messageId(KeyUtil.getUniqueKey())
                .senderEmail(HeaderUtil.getEmail())
                .receiverEmail(messageRequestVO.getReceiverEmail())
                .content(messageRequestVO.getContent())
                .isDeleted(false)
                .notRead(1)
                .type(MessageType.CHAT)
                .createdAt(LocalDateTime.now())
                .build();
        messageDao.save(message);
    }
}
