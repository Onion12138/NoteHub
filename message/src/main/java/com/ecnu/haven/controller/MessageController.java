package com.ecnu.haven.controller;

import com.ecnu.haven.domain.Message;
import com.ecnu.haven.service.MessageService;
import com.ecnu.haven.vo.MessageListVO;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author HavenTong
 * @date 2020/2/14 7:46 下午
 */
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;


    @GetMapping("/list")
    public BaseResponseVO findMessageList() {
       List<MessageListVO> result = messageService.findMessageList();
        return BaseResponseVO.success(result);
    }

    @GetMapping("/history")
    public BaseResponseVO findHistoryChat(@RequestParam String senderEmail) {
        List<MessageResponseVO> messages = messageService.findHistoryChat(senderEmail);
        return BaseResponseVO.success(messages);
    }

    @PostMapping("/clear-unread")
    public BaseResponseVO clearUnreadMessage(@RequestParam String senderEmail) {
        messageService.clearUnreadMessage(senderEmail);
        return BaseResponseVO.success();
    }

    @PostMapping("/status")
    public BaseResponseVO changeStatus(@RequestParam String messageId,
                                       @RequestParam String type) {
        messageService.changeStatus(messageId, type);
        return BaseResponseVO.success();
    }

    @PostMapping("/multiStatus")
    public BaseResponseVO changeMultiStatus(@RequestParam List<String> messageIds,
                                            @RequestParam String type) {
        messageService.changeMultiStatus(messageIds, type);
        return BaseResponseVO.success();
    }

    @PostMapping("/allRead")
    public BaseResponseVO changeAllToRead() {
        messageService.changeAllToRead();;
        return BaseResponseVO.success();
    }


    @PostMapping("/delete")
    public BaseResponseVO delete(@RequestParam String messageId) {
        messageService.delete(messageId);
        return BaseResponseVO.success();
    }


    @PostMapping("/deleteMulti")
    public BaseResponseVO deleteMulti(@RequestParam List<String> messageIds) {
        messageService.deleteMulti(messageIds);
        return BaseResponseVO.success();
    }

    @PostMapping("/deleteAll")
    public BaseResponseVO deleteAll() {
        messageService.deleteAll();
        return BaseResponseVO.success();
    }


    /**
     * 发送信息并提示用户
     * @param request 请求体参数，receiverEmail, content
     * @return
     */
    @PostMapping("/send")
    public BaseResponseVO sendMessage(@Validated @RequestBody MessageRequestVO request) {
        messageService.sendMessage(request.getReceiverEmail(), request.getContent());
        return BaseResponseVO.success();
    }


    /**
     * 测试用接口，向数据库插入一条消息
     * @param messageRequest 插入消息所必要的信息
     * @return BaseResponseVO
     */
    @PostMapping("/add")
    public BaseResponseVO add(@RequestBody MessageRequestVO messageRequest) {
        messageService.saveMessage(messageRequest);
        return BaseResponseVO.success();
    }

}
