package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.NoteInfoDao;
import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.service.NoteService;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:13 下午
 */
@Service
@RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = MQConstant.GRAPH_NOTE_QUEUE),
                exchange = @Exchange(value = MQConstant.EXCHANGE, type = "topic"))
})
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteInfoDao noteInfoDao;

    @RabbitHandler
    private void addOrUpdateNotes(String message) {
        String[] data = message.split("#");
        Arrays.stream(data).forEach(e-> noteInfoDao.save(JSON.parseObject(e, NoteInfo.class)));
    }

    @Override
    public List<NoteInfo> recommend(String noteId) {
        List<NoteInfo> recommendNotes = noteInfoDao.findNearestRelatedNotes(noteId);
        if (recommendNotes == null) {
            recommendNotes = new ArrayList<>();
        }
        recommendNotes.addAll( noteInfoDao.findNearbyRelatedNotes(noteId));
        return recommendNotes;
    }

}
