package com.ecnu.haven.vo;

import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.vo.BaseRequestVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author HavenTong
 * @date 2020/2/15 11:38 上午
 * 接收推送消息请求的RequestBody
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class MessageRequestVO extends BaseRequestVO {
    private String senderId;
    private String senderName;
    private List<String> receiverEmails;
    private String title;
    @Override
    public void checkParams() throws CommonServiceException {

    }
}
