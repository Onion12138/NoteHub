package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.api.GraphAPI;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.NoteDao;
import com.ecnu.onion.domain.graph.NoteInfo;
import com.ecnu.onion.domain.mongo.Note;
import com.ecnu.onion.domain.search.NoteSearch;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.utils.DownloadUtil;
import com.ecnu.onion.utils.KeyUtil;
import com.ecnu.onion.utils.UuidUtil;
import com.ecnu.onion.vo.AnalysisVO;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author onion
 * @date 2020/1/31 -2:29 下午
 */
@Service
@Slf4j
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
    private RedisTemplate<String, String> redisTemplate;

    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("2592000")
    private long expireInSeconds;

    @Override
    public String publishNote(AnalysisVO analyze, Map<String, String> map) {
        if (analyze.getCode() != 0) {
            throw new CommonServiceException(ServiceEnum.NOTE_ILLEGAL);
        }
        String id = KeyUtil.getUniqueKey();
        String email = map.get("authorEmail");
        Note note = Note.builder()
                .id(id)
                .authorEmail(email)
                .description(map.get("description"))
                .authority("write".equals(map.get("authority")))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .tag(map.get("tag"))
                .keywords(analyze.getKeywords())
                .titles(analyze.getTitles())
                .levelTitle(analyze.getLevelTitles())
                .summary(analyze.getSummary())
                .content(map.get("content"))
                .valid(true)
                .build();
        String forkFrom = map.get("forkFrom");
        if (forkFrom != null) {
            note.setForkFrom(forkFrom);
            redisTemplate.opsForHash().increment(forkFrom, "fork", 1);
        }
        noteDao.save(note);
        String[] fields = {"star","hate","view","collect","fork"};
        for (String field: fields) {
            redisTemplate.opsForHash().put(id, field, "0");
        }
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.GRAPH_NOTE_QUEUE, asGraphJson(note));
        return id;
    }

    @Override
    public void updateNote(AnalysisVO analyze, Map<String, String> map) {
        if (analyze.getCode() != 0) {
            throw new CommonServiceException(ServiceEnum.NOTE_ILLEGAL);
        }
        String id = map.get("id");
        Note note = findById(id);
        note.setDescription(map.get("description"));
        note.setContent(map.get("content"));
        note.setUpdateTime(LocalDateTime.now());

        note.setSummary(analyze.getSummary());
        note.setTitles(analyze.getTitles());
        note.setLevelTitle(analyze.getLevelTitles());
        note.setKeywords(analyze.getKeywords());
        noteDao.save(note);
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
    }

    @Override
    public void deleteNote(String noteId) {
        updateField(noteId, "valid", false);
    }

    @Override
    public Note findOneNote(String email, String noteId) {
        Note note = findById(noteId);
        if (!note.getValid()) {
            throw new CommonServiceException(ServiceEnum.NOTE_DELETED);
        }
        redisTemplate.opsForHash().increment(noteId,"view",1);
        graphAPI.addViewRelation(email, noteId);
        return note;
    }


    @Override
    public void changeAuthority(String noteId, String authority) {
        updateField(noteId, "authority", "write".equals(authority));
    }

//    @Override
//    public String comment(CommentVO commentVO) {
//        Optional<Note> optional = noteDao.findById(commentVO.getNoteId());
//        if (optional.isEmpty()) {
//            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
//        }
//        Note note = optional.get();
//        String commentId = UuidUtil.getUuid();
//        commentVO.setCommentId(commentId);
//        List<Comment> commentList = note.getComments();
//        Comment comment = new Comment();
//        BeanUtils.copyProperties(commentVO, comment);
//        if (StringUtils.isEmpty(commentVO.getParentCommentId())){
//            commentList.add(comment);
//            note.setComments(commentList);
//            noteDao.save(note);
//            return commentId;
//        }
//        int i;
//        for (i = 0; i < commentList.size() - 1; i++) {
//            if (commentList.get(i).getCommentId().equals(commentVO.getParentCommentId())) {
//                break;
//            }
//        }
//        commentList.add(i+1, comment);
//        note.setComments(commentList);
//        noteDao.save(note);
//        return commentId;
//    }

    @Override
    public void starOrHate(String type, String noteId, String email) {
        redisTemplate.opsForHash().increment(noteId, type, 1);
        if ("star".equals(type)) {
            graphAPI.addStarRelation(email, noteId);
        }
        if ("hate".equals(type)) {
            graphAPI.addHateRelation(email, noteId);
        }
    }

//    @Override
//    public void deleteComment(String noteId, String commentId) {
//        Optional<Note> optional = noteDao.findById(noteId);
//        if (optional.isEmpty()) {
//            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
//        }
//        Note note = optional.get();
//        List<Comment> commentList = note.getComments();
//        note.setComments(commentList.stream().filter
//                (e-> !e.getCommentId().equals(commentId) && !commentId.equals(e.getParentCommentId()))
//                .collect(Collectors.toList()));
//        noteDao.save(note);
//    }

    @Override
    public String uploadPicture(String noteId, MultipartFile file) {
        InputStream fileInputStream = null;
        try {
            fileInputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CommonServiceException(-1, e.getMessage());
        }
        String key = noteId + UuidUtil.getUuid();
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(fileInputStream, key, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            throw new CommonServiceException(ServiceEnum.PROFILE_UPLOAD_ERROR);
        }
        return DownloadUtil.getFileUrl(key, accessKey, secretKey, expireInSeconds);
    }

    @Override
    public Map<Object, Object> getCounter(String noteId) {
        return redisTemplate.opsForHash().entries(noteId);
    }

    private String asGraphJson(Note note) {
        NoteInfo noteInfo = NoteInfo.builder()
                .publishTime(LocalDate.now().toString())
                .noteId(note.getId())
                .build();
        log.info("note:{}", JSON.toJSONString(noteInfo));
        return JSON.toJSONString(noteInfo);
    }

    private String asSearchJson(Note note) {
        NoteSearch noteSearch = NoteSearch.builder()
                .id(note.getId())
                .email(note.getAuthorEmail())
                .keywords(note.getKeywords())
                .titles(note.getTitles())
                .summary(note.getSummary())
                .description(note.getDescription())
                .tag(note.getTag())
                .createTime(LocalDate.now().toString())
                .updateTime(LocalDate.now().toString()).build();
        return JSON.toJSONString(noteSearch);
    }

    private void updateField(String noteId, String field, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(noteId));
        Update update = new Update();
        update.set(field, value);
        mongoTemplate.updateFirst(query, update, Note.class);
    }

    private Note findById(String noteId) {
        Optional<Note> optional = noteDao.findById(noteId);
        if (optional.isEmpty()) {
            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
        }
        return optional.get();
    }

}
