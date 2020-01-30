package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.api.GraphAPI;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.NoteDao;
import com.ecnu.onion.domain.graph.NoteInfo;
import com.ecnu.onion.domain.mongo.Note;
import com.ecnu.onion.domain.search.NoteSearch;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.utils.KeyUtil;
import com.ecnu.onion.vo.AnalysisVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -5:50 下午
 */
@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private NoteDao noteDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GraphAPI graphAPI;
    /*
    * @Id
    private String id;
    private String authorEmail;
    private String authorName;
    private String title;
    private Boolean authority;
    private String forkFrom;
    private LocalDateTime createTime;
    private Set<String> keywords;
    private Set<String> languages;
    private Set<String> levelTitles;
    private String summary;
    private Integer stars;
    private Integer views;
    private Integer hates;
    private Integer forks;
    private Integer collects;
    private String content;
    private Boolean valid;
    private List<Comment> comments;
    * */
    //更新会生成新版本。老版本标识为invalid。
    @Override
    public void publishNote(AnalysisVO analyze, Map<String, String> map) {
        String oldId = map.get("id");
        String id = KeyUtil.getUniqueKey();
        if (oldId != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(oldId));
            Update update = new Update();
            update.set("valid",false);
            mongoTemplate.updateFirst(query, update, Note.class);
        }
        Note note = Note.builder().id(id).authorEmail(map.get("authorEmail")).authorName(map.get("authorName"))
                .authority("write".equals(map.get("authority"))).title(map.get("title"))
                .createTime(LocalDateTime.now())
                .keywords(analyze.getKeywords()).languages(analyze.getLanguages())
                .levelTitles(analyze.getLevelTitles())
                .summary(String.join(" ", analyze.getSummary()))
                .stars(0).views(0).hates(0).forks(0).collects(0).valid(true)
                .content(map.get("content")).comments(new ArrayList<>())
                .build();
        if (map.get("forkFrom") != null) {
            note.setForkFrom(map.get("forkFrom"));
        }
        noteDao.save(note);
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE,MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
        if (oldId != null)
            graphAPI.updateNote(oldId, id);
//        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE,MQConstant.GRAPH_NOTE_QUEUE, asGraphJson(note));
    }

    private String asSearchJson(Note note) {
        NoteSearch noteSearch = NoteSearch.builder().id(note.getId()).email(note.getAuthorEmail())
                .authorName(note.getAuthorName()).keywords(String.join(" ", note.getKeywords()))
                .summary(note.getSummary()).title(note.getTitle() + String.join(" ", note.getLevelTitles()))
                .createTime(LocalDate.now()).build();
        return JSON.toJSONString(noteSearch);
    }

    private String asGraphJson(Note note) {
        NoteInfo noteInfo = NoteInfo.builder().noteId(note.getId()).title(note.getTitle())
                .publishTime(LocalDate.now().toString()).build();
        return JSON.toJSONString(noteInfo);
    }
}
