package com.ecnu.haven.domain;

import com.ecnu.onion.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HavenTong
 * @date 2020/2/15 10:39 上午
 */
@Data
@Builder
@AllArgsConstructor
@Document(collection = "message")
public class Message implements Serializable {
    @Id
    private String messageId;
    private String userEmail;
    private MessageType type;
    private String content;
    private String senderId;
    private String senderName;
    private Boolean isRead;
    private Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createdAt;
}
