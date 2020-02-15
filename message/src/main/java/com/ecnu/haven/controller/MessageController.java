package com.ecnu.haven.controller;

import com.ecnu.haven.socket.WebSocket;
import com.ecnu.haven.util.HeaderUtil;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.onion.vo.BaseRequestVO;
import com.ecnu.onion.vo.BaseResponseVO;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author HavenTong
 * @date 2020/2/14 7:46 下午
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private WebSocket webSocket;

    @GetMapping("/all")
    public BaseResponseVO getAllMessages() {
        return BaseResponseVO.success();
    }

    @PostMapping("/status")
    public BaseResponseVO changeStatus(@RequestParam String messageId,
                                       @RequestParam String type) {
        return BaseResponseVO.success();
    }

    @PostMapping("/delete")
    public BaseResponseVO delete(@RequestParam String messageId) {
        return BaseResponseVO.success();
    }

    @PostMapping("/group/create")
    public BaseResponseVO pushCreateGroupMessage(@RequestBody MessageRequestVO messageRequest) {
        return BaseResponseVO.success();
    }

    @PostMapping("/group/shareNote")
    public BaseResponseVO pushShareNoteMessage(@RequestParam String sharedBy,
                                               @RequestBody MessageRequestVO messageRequest) {
        return BaseResponseVO.success();
    }

    @PostMapping("/user/comment")
    public BaseResponseVO pushCommentMessage(@RequestParam String senderName,
                                             @RequestParam String authorEmail,
                                             @RequestParam String title) {
        return BaseResponseVO.success();
    }

    @PostMapping("/user/reply")
    public BaseResponseVO pushReplyMessage(@RequestParam String senderName,
                                           @RequestParam String receiverEmail,
                                           @RequestParam String title) {
        return BaseResponseVO.success();
    }

    @PostMapping("/user/notifyUpdate")
    public BaseResponseVO pushNoteUpdateMessage(@RequestParam String type,
                                                @RequestBody MessageRequestVO messageRequest) {
        return BaseResponseVO.success();
    }
}
