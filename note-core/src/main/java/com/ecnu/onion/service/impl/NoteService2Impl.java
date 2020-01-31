package com.ecnu.onion.service.impl;

import org.springframework.stereotype.Service;

/**
 * @author onion
 * @date 2020/1/27 -5:50 下午
 */

/*
* //todo
*    点赞、踩、收藏、评论
* */
@Service
public class NoteService2Impl{
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//    @Autowired
//    private NoteDao noteDao;
//    @Autowired
//    private MongoTemplate mongoTemplate;
//    @Autowired
//    private GraphAPI graphAPI;
//    @Autowired
//    private SearchAPI searchAPI;
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//    /*
//    * @Id
//    private String id;
//    private String authorEmail;
//    private String authorName;
//    private String title;
//    private Boolean authority;
//    private String forkFrom;
//    private LocalDateTime createTime;
//    private Set<String> keywords;
//    private Set<String> languages;
//    private Set<String> levelTitles;
//    private String summary;
//    private Integer stars;
//    private Integer views;
//    private Integer hates;
//    private Integer forks;
//    private Integer collects;
//    private String content;
//    private Boolean valid;
//    private List<Comment> comments;
//    * */
//    @Override
//    public String publishNote(AnalysisVO analyze, Map<String, String> map) {
//        String id = KeyUtil.getUniqueKey();
//        Note note = Note.builder().id(id).authorEmail(map.get("authorEmail")).authorName(map.get("authorName"))
//                .authority("write".equals(map.get("authority"))).title(map.get("title"))
//                .createTime(LocalDateTime.now()).forkFrom("")
//                .keywords(analyze.getKeywords()).languages(analyze.getLanguages())
//                .levelTitles(analyze.getLevelTitles())
//                .summary(String.join(" ", analyze.getSummary()))
//                .stars(0).views(0).hates(0).forks(0).collects(0).valid(true)
//                .content(map.get("content")).comments(new ArrayList<>())
//                .build();
//        String forkFrom = map.get("forkFrom");
//        if (forkFrom != null) {
//            note.setForkFrom(forkFrom);
//            graphAPI.addForkRelation(note.getAuthorEmail(), forkFrom);
//            increment("fork", forkFrom);
//        }
//        noteDao.save(note);
//        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE,MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
//        graphAPI.addPublishRelation(note.getAuthorEmail(), id, note.getTitle());
//        return id;
//    }
//
//    @Override
//    public String updateNote(AnalysisVO analyze, Map<String, String> map) {
//        String oldId = map.get("id");
//        Optional<Note> optional = noteDao.findById(oldId);
//        if (optional.isEmpty()) {
//            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
//        }
//        Note oldNote = optional.get();
//        oldNote.setValid(false);
//        noteDao.save(oldNote);
//        String id = KeyUtil.getUniqueKey();
//        Note note = Note.builder().id(id).authorEmail(oldNote.getAuthorEmail()).authorName(oldNote.getAuthorName())
//                .authority(oldNote.getAuthority()).title(map.get("title"))
//                .createTime(LocalDateTime.now()).forkFrom(oldNote.getForkFrom())
//                .keywords(analyze.getKeywords()).languages(analyze.getLanguages())
//                .levelTitles(analyze.getLevelTitles())
//                .summary(String.join(" ", analyze.getSummary()))
//                .stars(oldNote.getStars()).views(oldNote.getViews()).hates(oldNote.getHates())
//                .forks(oldNote.getForks()).collects(oldNote.getCollects()).valid(true)
//                .content(map.get("content")).comments(oldNote.getComments())
//                .build();
//        noteDao.save(note);
//        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE,MQConstant.SEARCH_NOTE_QUEUE, asSearchJson(note));
//        graphAPI.updateNote(oldId, id);
//        graphAPI.addPublishRelation(note.getAuthorEmail(), id,note.getTitle());
//        return id;
//    }
//
//    @Override
//    public void deleteNote(String noteId) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id").is(noteId));
//        Update update = new Update();
//        update.set("valid",false);
//        mongoTemplate.updateFirst(query, update, Note.class);
//        searchAPI.deleteNote(noteId);
//        graphAPI.deleteNote(noteId);
//    }
//
//    /*
//    *
//    * private String id;
//    private String authorEmail;
//    private String authorName;
//    private String title;
//    private Boolean authority;
//    private String forkFrom;
//    private LocalDateTime createTime;
//    private Integer stars;
//    private Integer views;
//    private Integer hates;
//    private Integer forks;
//    private Integer collects;
//    private String content;
//    private Boolean valid;
//    private List<Comment> comments;
//    * */
//    @Override
//    public NoteResponseVO findOneNote(String noteId) {
//        Optional<Note> optional = noteDao.findById(noteId);
//        if (optional.isEmpty()) {
//            throw new CommonServiceException(ServiceEnum.NOTE_NOT_EXIST);
//        }
//        Note note = optional.get();
//        if (!note.getValid()) {
//            throw new CommonServiceException(ServiceEnum.NOTE_DELETED);
//        }
//        NoteResponseVO noteResponseVO = new NoteResponseVO();
//
//        return noteResponseVO;
//    }
//
//    private void increment(String field, String noteId) {
//        if (redisTemplate.opsForHash().hasKey(field, noteId)) {
//            redisTemplate.opsForHash().increment(field, noteId, 1);
//        } else {
//            redisTemplate.opsForHash().put(field, noteId, "0");
//        }
//    }
//    private String asSearchJson(Note note) {
//        NoteSearch noteSearch = NoteSearch.builder().id(note.getId()).email(note.getAuthorEmail())
//                .authorName(note.getAuthorName()).keywords(String.join(" ", note.getKeywords()))
//                .summary(note.getSummary()).title(note.getTitle() + String.join(" ", note.getLevelTitles()))
//                .createTime(LocalDate.now()).build();
//        return JSON.toJSONString(noteSearch);
//    }

}
