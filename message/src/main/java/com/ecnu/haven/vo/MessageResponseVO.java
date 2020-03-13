package com.ecnu.haven.vo;

import com.ecnu.onion.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private Boolean notRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createdAt;
}
