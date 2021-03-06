package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.api.GraphAPI;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.TagDao;
import com.ecnu.onion.dao.UserDao;
import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import com.ecnu.onion.domain.graph.UserInfo;
import com.ecnu.onion.domain.mongo.Tag;
import com.ecnu.onion.domain.mongo.User;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.utils.*;
import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.ModificationVO;
import com.ecnu.onion.vo.RegisterVO;
import com.ecnu.onion.vo.UserVO;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author onion
 * @date 2020/1/27 -5:50 下午
 */
@Service
@Slf4j
@RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = MQConstant.MAIL_QUEUE),
                exchange = @Exchange(value = MQConstant.EXCHANGE, type = "fanout"))
})
public class UserServiceImpl implements UserService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private GraphAPI graphAPI;

    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("31536000")
    private long expireInSeconds;

    @Override
    public void register(@Valid RegisterVO registerVO)  {
        if (userDao.findById(registerVO.getEmail()).isPresent()) {
            throw new CommonServiceException(ServiceEnum.EMAIL_IN_USE);
        }
        String redisCode = redisTemplate.opsForValue().get("code_" + registerVO.getEmail());
        if (redisCode == null) {
            throw new CommonServiceException(ServiceEnum.CODE_NOT_EXIST);
        }
        if (!registerVO.getCode().equals(redisCode)) {
            throw new CommonServiceException(ServiceEnum.WRONG_CODE);
        }
        String salt = SaltUtil.getSalt();
        String password = Md5Util.encrypt(registerVO.getPassword() + salt);
        MindMap mindMap = generateDefaultMindMap();
        User user = User.builder()
                .email(registerVO.getEmail())
                .username(registerVO.getUsername())
                .registerTime(LocalDate.now())
                .password(password)
                .salt(salt)
                .profileUrl("https://avatars2.githubusercontent.com/u/33611404?s=400&v=4")
                .disabled(false)
                .interestedTags(registerVO.getChooseTags())
                .mindMapList(new ArrayList<>())
                .collectNotes(new HashSet<>())
                .collection(mindMap)
                .build();
        userDao.save(user);
        UserInfo userInfo = UserInfo.builder().email(user.getEmail())
                .registerTime(LocalDate.now().toString()).build();
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.GRAPH_USER_QUEUE, JSON.toJSONString(userInfo));
    }

    @Override
    public Map<String, Object> login(LoginVO loginVO) {
        String email = loginVO.getEmail();
            Optional<User> optionalUser = userDao.findById(email);
        if (optionalUser.isEmpty()) {
            throw new CommonServiceException(ServiceEnum.ACCOUNT_NOT_EXIST);
        }
        User user = optionalUser.get();
        if (user.getDisabled()) {
            throw new CommonServiceException(ServiceEnum.ACCOUNT_DISABLED);
        }
        String salt = user.getSalt();
        String rawPassword = loginVO.getPassword();
        if (!user.getPassword().equals(Md5Util.encrypt(rawPassword + salt))) {
            throw new CommonServiceException(ServiceEnum.WRONG_PASSWORD);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtUtil.createJwt(user));
        map.put("email",user.getEmail());
        map.put("username",user.getUsername());
        map.put("profileUrl",user.getProfileUrl());
        return map;
    }

    @Override
    public String uploadProfile(String email, MultipartFile file){
        InputStream fileInputStream = null;
        try {
            fileInputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CommonServiceException(-1, e.getMessage());
        }
        String key = email + UuidUtil.getUuid();
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(fileInputStream, key, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("upload file : {}", putRet);
        } catch (QiniuException ex) {
            throw new CommonServiceException(ServiceEnum.PROFILE_UPLOAD_ERROR);
        }
        String url = DownloadUtil.getFileUrl(key, accessKey, secretKey, expireInSeconds);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(email));
        Update update = new Update();
        update.set("profileUrl", url);
        mongoTemplate.updateFirst(query, update, User.class);
        return url;
    }

    @Override
    public void modifyPassword(String email, ModificationVO modificationVO) {
        String redisCode = redisTemplate.opsForValue().get("code_" + email);
        if ( redisCode == null) {
            throw new CommonServiceException(ServiceEnum.CODE_NOT_EXIST);
        }
        if (!modificationVO.getCode().equals(redisCode)) {
            throw new CommonServiceException(ServiceEnum.WRONG_CODE);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(email));
        Update update = new Update();
        String newSalt = SaltUtil.getSalt();
        String newPassword = Md5Util.encrypt(modificationVO.getPassword() + newSalt);
        update.set("password", newPassword);
        update.set("salt", newSalt);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void sendCode(String email) {
        String code = SaltUtil.getCode();
        redisTemplate.opsForValue().set("code_" + email, code, 10, TimeUnit.MINUTES);
        String content = "这是你的验证码：" + code + ",此验证码10分钟内有效!" + email;
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.MAIL_QUEUE, content);
    }

    @Override
    public void modifyUsername(String email, String username) {
        Query query = Query.query(Criteria.where("_id").is(email));
        Update update = Update.update("username",username);
        mongoTemplate.updateFirst(query,update,User.class);
    }

    @Override
    public UserVO findUser(String email) {
        User user = getUser(email);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public void addMindMap(String email, MindMap mindMap) {
        User user = getUser(email);
        user.getMindMapList().add(mindMap);
        userDao.save(user);
    }

    @Override
    public List<MindMap> findMindMap(String email) {
        User user = getUser(email);
        return user.getMindMapList();
    }

    @Override
    public void mindMapNote(String email, CollectNote collectNote) {
        User user = getUser(email);
        user.getCollectNotes().add(collectNote);
        List<MindMap> mindMapList = user.getMindMapList();
        String[] path = collectNote.getTag();
        MindMap current = mindMapList.stream().filter(e->e.getLabel().equals(path[0])).findFirst().get();
        for (int i = 1; i < path.length; i++) {
            List<MindMap> children = current.getChildren();
            int finalI = i;
            current = children.stream().filter(e->e.getLabel().equals(path[finalI])).findFirst().get();
        }

        current.addComponent(new MindMap(collectNote.getDescription(), collectNote.getNoteId(), true));

        userDao.save(user);

    }

    @Override
    public MindMap getCollection(String email) {
        User user = getUser(email);
        return user.getCollection();
    }

    @Override
    public void collectNote(String email, CollectNote note) {
        User user = getUser(email);
        if (user.getCollectNotes().contains(note)) {
            throw new CommonServiceException(-1,"已经收藏过此笔记");
        }
        user.getCollectNotes().add(note);
        String[] tag = note.getTag();
        String firstLevelTag = tag[0];
        String secondLevelTag = tag[1];
        MindMap mindMap = user.getCollection();
        MindMap firstLevelMindMap = mindMap.getChildren().stream().
                filter(e -> e.getLabel().equals(firstLevelTag)).findFirst().get();
        MindMap secondLevelMindMap = firstLevelMindMap.getChildren().stream()
                .filter(e -> e.getLabel().equals(secondLevelTag)).findFirst().get();
        secondLevelMindMap.addComponent(new MindMap(note.getDescription(),note.getNoteId(),false));
        userDao.save(user);
        redisTemplate.opsForHash().increment(note.getNoteId(), "collect",1);
        graphAPI.addCollectRelation(email, note.getNoteId());
    }

    private User getUser(String email) {
        Optional<User> optional = userDao.findById(email);
        if (optional.isEmpty()) {
            throw new CommonServiceException(ServiceEnum.ACCOUNT_NOT_EXIST);
        }
        return optional.get();
    }
    private MindMap generateDefaultMindMap() {
        MindMap defaultMap = new MindMap("默认索引");
        List<Tag> tagList = tagDao.findAll();
        for (Tag tag : tagList) {
            MindMap mindMap = new MindMap(tag.getLabel());
            List<Tag> subTags = tag.getChildren();
            for (Tag child : subTags) {
                mindMap.addComponent(new MindMap(child.getLabel()));
            }
            defaultMap.addComponent(mindMap);
        }
        return defaultMap;
    }
}
