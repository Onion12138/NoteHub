package com.ecnu.haven.service;

import com.ecnu.haven.vo.MessageListVO;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;

import java.util.List;

/**
 * @author HavenTong
 * @date 2020/2/20 4:20 下午
 */
public interface MessageService {

    /**
     * 用户获取消息列表
     * @return 返回消息列表，返回用户和所有联系人最近的一条消息, 按照时间降序排序，
     */
    List<MessageListVO> findMessageList();

    /**
     * 查看和某个联系人的历史消息
     * @param senderEmail 联系人email
     * @return 历史消息列表
     */
    List<MessageResponseVO> findHistoryChat(String senderEmail);

    /**
     * 将和某个联系人的消息全部标为已读
     * @param senderEmail 发送
     */
    void clearUnreadMessage(String senderEmail);

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
     * 存储消息并推送给用户提示信息
     * @param receiverEmail 接受者Email
     * @param content 发送内容
     */
    void sendMessage(String receiverEmail, String content);

    /**
     * 测试用方法，向表中插入一条消息
     * @param messageRequestVO 插入消息所必要的信息
     */
    void saveMessage(MessageRequestVO messageRequestVO);
}
