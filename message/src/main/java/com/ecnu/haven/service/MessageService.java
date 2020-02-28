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

    /**
     * 修改消息状态
     * @param messageId 消息id
     * @param type  "Read"表示设置为已读, "NotRead"表示设置为未读
     */
    void changeStatus(String messageId, String type);

    /**
     * 批量修改消息状态
     * @param messageIds 消息id列表
     * @param type "Read"表示设置为已读, "NotRead"表示设置为未读
     */
    void changeMultiStatus(List<String> messageIds, String type);

    /**
     * 修改用户的所有消息为已读
     */
    void changeAllToRead();

    /**
     * 删除消息
     * @param messageId 消息id
     */
    void delete(String messageId);

    /**
     * 删除多条消息
     * @param messageIds 消息id列表
     */
    void deleteMulti(List<String> messageIds);

    /**
     * 删除用户的全部消息
     */
    void deleteAll();

    /**
     * 推送并存储消息
     * @param messageRequestVO 推送消息的必要信息, 见API文档
     */
    void post(MessageRequestVO messageRequestVO);

    /**
     * 测试用方法，向表中插入一条消息
     * @param messageRequestVO 插入消息所必要的信息
     */
    void saveMessage(MessageRequestVO messageRequestVO);
}
