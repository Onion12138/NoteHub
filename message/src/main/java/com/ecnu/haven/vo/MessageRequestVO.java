package com.ecnu.haven.vo;

import com.ecnu.onion.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.List;

/**
 * @author HavenTong
 * @date 2020/2/15 11:38 上午
 * 接收推送消息请求的RequestBody
 */
@Data
@AllArgsConstructor
@Builder
public class MessageRequestVO {
    @Email
    private String receiverEmail;

    private String content;
}
