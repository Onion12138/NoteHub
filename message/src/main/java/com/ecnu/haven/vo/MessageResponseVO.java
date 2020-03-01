package com.ecnu.haven.vo;

import com.ecnu.onion.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HavenTong
 * @date 2020/2/20 4:10 下午
 * Message响应类型
 */
@Data
@Builder
@AllArgsConstructor
public class MessageResponseVO implements Serializable {
    private String messageId;
    private MessageType type;
    private String content;
    private String senderName;
    private Boolean isRead;
    private String createdAt;
}
