package com.ecnu.haven.controller;

import com.ecnu.haven.socket.WebSocket;
import com.ecnu.haven.util.HeaderUtil;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.onion.vo.BaseRequestVO;
import com.ecnu.onion.vo.BaseResponseVO;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author HavenTong
 * @date 2020/2/14 7:46 下午
 */
@RestController
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

    @PostMapping("/multiStatus")
    public BaseResponseVO changeMultiStatus(@RequestParam List<String> messageIds,
                                            @RequestParam String type) {
        return BaseResponseVO.success();
    }

    @PostMapping("/allRead")
    public BaseResponseVO changeAllToRead() {
        return BaseResponseVO.success();
    }

    @PostMapping("/delete")
    public BaseResponseVO delete(@RequestParam String messageId) {
        return BaseResponseVO.success();
    }

    @PostMapping("/deleteMulti")
    public BaseResponseVO deleteMulti(@RequestParam List<String> messageIds) {
        return BaseResponseVO.success();
    }

    @PostMapping("/deleteAll")
    public BaseResponseVO deleteAll() {
        return BaseResponseVO.success();
    }

    @PostMapping("/post")
    public BaseResponseVO post(@RequestBody MessageRequestVO messageRequest) {
        return BaseResponseVO.success();
    }
}
