package com.ecnu.onion.service;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.NoteDao;
import com.ecnu.onion.domain.Note;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * @author onion
 * @date 2020/1/27 -11:12 上午
 */
@Slf4j
@Service
@RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = MQConstant.SEARCH_NOTE_QUEUE),
                exchange = @Exchange(value = MQConstant.EXCHANGE, type = "topic"))
})
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteDao noteDao;

    @Override
    public Page<Note> findByAuthorEmail(String email, int page) {
        Sort sort = Sort.by("updateTime");
        return noteDao.findAllByEmail(email, PageRequest.of(page - 1, 10, sort));
    }

    @Override
    public Page<Note> findByKeyword(String keyword, int page) {
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("summary")
                .field("keywords")
                .field("description")
                .field("titles");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(keyword, "summary","keywords","description","titles")
                                .field("description", 3.0f)
                        .field("titles", 1.0f)
                        .field("summary",0.4f)
                        .field("keywords",0.2f)
                        .type(MultiMatchQueryBuilder.Type.MOST_FIELDS))
                .withHighlightBuilder(highlightBuilder)
                .withPageable(PageRequest.of(page - 1, 10 )).build();
        return noteDao.search(searchQuery);
    }

    @Override
    public Page<Note> findByTag(String tag, int page) {
        Sort sort = Sort.by("updateTime");
        return noteDao.findAllByTag(tag, PageRequest.of(page - 1, 10, sort));
    }

    @RabbitHandler
    private void saveNote(String message){
        Note note = JSON.parseObject(message, Note.class);
        noteDao.save(note);
    }

}
