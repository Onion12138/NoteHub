package com.ecnu.haven.domain;

import com.ecnu.haven.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.print.DocFlavor;
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
    private LocalDateTime createdAt;
}
