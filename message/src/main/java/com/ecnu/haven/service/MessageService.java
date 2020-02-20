package com.ecnu.haven.service;

import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;

import java.util.List;

/**
 * @author HavenTong
 * @date 2020/2/20 4:20 下午
 */
public interface MessageService {
    /**
     * 查看所有消息
     * @return 用户的所有未删除消息
     */
    List<MessageResponseVO> findAllMessages();

    void changeStatus(String messageId, String type);

    void changeMultiStatus(List<String> messageIds, String type);

    void changeAllToRead();

    void delete(String messageId);

    void saveMessage(MessageRequestVO messageRequestVO);
}
