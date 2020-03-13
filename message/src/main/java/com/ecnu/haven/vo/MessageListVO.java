package com.ecnu.haven.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author HavenTong
 * @date 2020/3/13 4:58 下午
 */
@Builder
@Data
public class MessageListVO {
    private String receiverEmail;
    private String senderEmail;
    private String content;

    /**
     * 用于lookup
     */
    private String username;
    private String profileUrl;
    private Integer sum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createdAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageListVO that = (MessageListVO) o;
        return (receiverEmail.equals(that.getSenderEmail()) &&
                senderEmail.equals(that.getReceiverEmail()))
                || (receiverEmail.equals(that.receiverEmail) &&
                senderEmail.equals(that.senderEmail));
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverEmail) + Objects.hash(senderEmail);
    }
}
