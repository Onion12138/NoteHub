package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.api.GraphAPI;
import com.ecnu.onion.api.SearchAPI;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.NoteDao;
import com.ecnu.onion.domain.mongo.Note;
import com.ecnu.onion.domain.search.NoteSearch;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.utils.KeyUtil;
import com.ecnu.onion.vo.AnalysisVO;
import com.ecnu.onion.vo.NoteResponseVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author onion
 * @date 2020/1/31 -2:29 下午
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
    @Autowired
    private SearchAPI searchAPI;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /*
    *
    *
    * private String id;
    private String authorEmail;
    private String authorName;
    private List<String> title;
    private Boolean authority;
    private String forkFrom;
    private List<LocalDateTime> createTime;
    private List<String> keywords;
    private List<String> languages;
    private List<String> levelTitles;
    private List<String> summary;
    private Integer stars;
    private Integer views;
    private Integer hates;
    private Integer forks;
    private Integer collects;
    private Integer version;
    private List<String> content;
    private Boolean valid;
    private List<List<Comment>> comments;
    * */
    @Override
    public String publishNote(AnalysisVO analyze, Map<String, String> map) {
        String id = KeyUtil.getUniqueKey();
        Note note = Note.builder().id(id).authorEmail(map.get("authorEmail"))
                .authorName(map.get("authorName"))
                .authority("write".equals(map.get("authority")))
                .title(Collections.singletonList(map.get("title")))
                .createTime(Collections.singletonList(LocalDateTime.now()))
                .forkFrom("")
                .keywords(Collections.singletonList(String.join(" ", analyze.getKeywords())))
                .languages(Collections.singletonList(String.join(" ",analyze.getLanguages())))
                .levelTitles(Collections.singletonList(String.join(" ", analyze.getLevelTitles())))
                .summary(Collections.singletonList(String.join(" ", analyze.getSummary())))
                .stars(0).views(0).hates(0).forks(0).collects(0).version(0).valid(true)
                .content(Collections.singletonList(map.get("content")))
                .comments(new ArrayList<>())
                .build();
        String forkFrom = map.get("forkFrom");
        if (forkFrom != null) {
            note.setForkFrom(forkFrom);
        }
        noteDao.save(note);
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE,MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
        return id;
    }

    @Override
    public String updateNote(AnalysisVO analyze, Map<String, String> map) {
        String id = map.get("id");
        Optional<Note> optional = noteDao.findById(id);
        if (optional.isEmpty()) {
            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
        }
        Note note = optional.get();
        int version = note.getVersion();
        note.setVersion(version + 1);
        note.getContent().add(map.get("content"));
        note.getTitle().add(map.get("title"));
        note.getSummary().add(String.join(" ", analyze.getSummary()));
        note.getLanguages().add(String.join(" ", analyze.getLanguages()));
        note.getLevelTitles().add(String.join(" ", analyze.getLevelTitles()));
        noteDao.save(note);
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE,MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
        return id;
    }

    @Override
    public void deleteNote(String noteId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(noteId));
        Update update = new Update();
        update.set("valid",false);
        mongoTemplate.updateFirst(query, update, Note.class);
        searchAPI.deleteNote(noteId);
        graphAPI.deleteNote(noteId);
    }

    @Override
    public NoteResponseVO findOneNote(String noteId) {
        Optional<Note> optional = noteDao.findById(noteId);
        if (optional.isEmpty()) {
            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
        }
        Note note = optional.get();
        int version = note.getVersion();
        if (!note.getValid()) {
            throw new CommonServiceException(ServiceEnum.NOTE_DELETED);
        }
        NoteResponseVO noteResponseVO = NoteResponseVO.builder()
                .id(noteId)
                .authorEmail(note.getAuthorEmail())
                .authorName(note.getAuthorName())
                .authority(note.getAuthority())
                .title(note.getTitle().get(version))
                .content(note.getContent().get(version))
                .forkFrom(note.getForkFrom())
                .createTime(note.getCreateTime().get(version))
                .comments(note.getComments())
                .stars(note.getStars())
                .hates(note.getHates())
                .forks(note.getForks())
                .collects(note.getCollects())
                .views(note.getViews())
                .build();
        return noteResponseVO;
    }

    private String asSearchJson(Note note) {
        int version = note.getVersion();
        NoteSearch noteSearch = NoteSearch.builder().id(note.getId()).email(note.getAuthorEmail())
                .authorName(note.getAuthorName())
                .keywords(note.getKeywords().get(version))
                .summary(note.getSummary().get(version))
                .title(note.getTitle().get(version))
                .createTime(LocalDate.now()).build();
        return JSON.toJSONString(noteSearch);
    }
}
