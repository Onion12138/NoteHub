package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.NoteInfoDao;
import com.ecnu.onion.domain.entity.NoteInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author onion
 * @date 2020/1/23 -5:13 下午
 */
@Service
@RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = MQConstant.GRAPH_NOTE_QUEUE),
                exchange = @Exchange(value = MQConstant.EXCHANGE, type = "topic"))
})
@Slf4j
public class NoteServiceImpl {
    @Autowired
    private NoteInfoDao noteInfoDao;

    @RabbitHandler
    private void addNote(String message) {
        log.info("message:{}",message);
        NoteInfo noteInfo = JSON.parseObject(message, NoteInfo.class);
        log.info("note:{}",noteInfo);
        noteInfoDao.save(noteInfo);
    }

}
