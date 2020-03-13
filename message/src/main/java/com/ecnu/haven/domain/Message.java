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
import java.util.Objects;

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
    private String receiverEmail;
    private String senderEmail;
    private MessageType type;
    private String content;
    // changed
    private Integer notRead;
    private Boolean isDeleted;

    /**
     * 和小组相关的消息，加入groupId
     */
    private String groupId;

    /**
     * 用于lookup
     */
    private String username;
    private String profileUrl;
    private Integer sum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createdAt;

}
