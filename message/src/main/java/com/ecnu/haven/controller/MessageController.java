package com.ecnu.haven.controller;

import com.ecnu.haven.service.MessageService;
import com.ecnu.haven.vo.MessageRequestVO;
import com.ecnu.haven.vo.MessageResponseVO;
import com.ecnu.onion.vo.BaseResponseVO;
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
    private MessageService messageService;

    @GetMapping("/all")
    public BaseResponseVO getAllMessages() {
        List<MessageResponseVO> responseVOList = messageService.findAllMessages();
        return BaseResponseVO.success(responseVOList);
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

    @PostMapping("/post")
    public BaseResponseVO post(@RequestBody MessageRequestVO messageRequest) {
        messageService.post(messageRequest);
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
