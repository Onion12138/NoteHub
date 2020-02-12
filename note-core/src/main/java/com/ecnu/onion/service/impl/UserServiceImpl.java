package com.ecnu.onion.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.UserDao;
import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import com.ecnu.onion.domain.mongo.User;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.utils.*;
import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.ModificationVO;
import com.ecnu.onion.vo.RegisterVO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("31536000")
    private long expireInSeconds;

    @Override
    public void register(RegisterVO registerVO)  {
        if (userDao.findById(registerVO.getEmail()).isPresent()) {
            throw new CommonServiceException(ServiceEnum.EMAIL_IN_USE);
        }
        String salt = SaltUtil.getSalt();
        String password = Md5Util.encrypt(registerVO.getPassword() + salt);
        User user = User.builder().email(registerVO.getEmail()).username(registerVO.getUsername()).registerTime(LocalDate.now())
                .password(password).salt(salt).profileUrl("https://avatars2.githubusercontent.com/u/33611404?s=400&v=4")
                .activated(false).disabled(false).activeCode(UuidUtil.getUuid())
                .collectIndexes(new ArrayList<>())
                .collectNotes(new HashSet<>())
                .interestedTags(new HashSet<>())
                .build();
        userDao.save(user);
        sendActivateLink(user);
    }

    @Override
    public void activate(String code) {
        Query query = Query.query(Criteria.where("activeCode").is(code));
        Update update = new Update();
        update.set("activated", true);
        mongoTemplate.updateFirst(query, update, User.class);
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
        if (user.getActivated()) {
            sendActivateLink(user);
            throw new CommonServiceException(ServiceEnum.ACCOUNT_NOT_ACTIVATED);
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
        map.put("collectNotes", user.getCollectNotes());
        map.put("collectIndexes", user.getCollectIndexes());
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
    public void chooseTags(String email, Set<String> tags) {
        Query query = Query.query(Criteria.where("_id").is(email));
        Update update = new Update();
        update.set("interestedTags", tags);
        MindMap mindMap = generateDefaultMindMap();
        update.addToSet("collectIndexes", mindMap);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void cancelCollectNote(String email, String noteId) {
        User user = userDao.findById(email).get();
        List<MindMap> list = user.getCollectIndexes();
        for (MindMap map : list) {
            removeCollectNote(noteId, map);
        }
        user.setCollectIndexes(list);
        user.getCollectNotes().removeIf(e->noteId.equals(e.getNoteId()));
        userDao.save(user);
    }

    @Override
    public void addIndex(String email, String mindMap) {
        Query query = Query.query(Criteria.where("_id").is(email));
        Update update = new Update();
        update.addToSet("collectIndexes", JSON.parseObject(mindMap, MindMap.class));
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void updateIndex(String email, Map<String, String> map) {
        String mindMap = map.get("mindMap");
        String num = map.get("num");
        User user = userDao.findById(email).get();
        MindMap newMindMap = JSON.parseObject(mindMap, MindMap.class);
        user.getCollectIndexes().set(Integer.parseInt(num), newMindMap);
    }


    @Override
    public MindMap findOneIndex(String email, Integer num) {
        User user = userDao.findById(email).get();
        return user.getCollectIndexes().get(num);
    }

    @Override
    public void deleteIndex(String email, Integer num) {
        User user = userDao.findById(email).get();
        user.getCollectIndexes().remove(num);
        userDao.save(user);
    }

    @Override
    public void collectNote(String email, CollectNote note) {
        User user = userDao.findById(email).get();
        user.getCollectNotes().add(note);
        String tag = note.getTag();
        moveToMenu(tag, note.getNoteId(), note.getTitle(), user.getCollectIndexes().get(0));
        log.info("user:{}",user);
        userDao.save(user);
        increment("collect",note.getNoteId());
    }


    private void removeCollectNote(String noteId, MindMap composite) {
        if (composite.isLeaf()) {
            return;
        }
        List<MindMap> children = composite.getChildren();
        Iterator<MindMap> iterator = children.iterator();
        while (iterator.hasNext()) {
            MindMap child = iterator.next();
            if (child.isLeaf() && child.getNoteId().equals(noteId)) {
                iterator.remove();
            } else if (!child.isLeaf()) {
                removeCollectNote(noteId, child);
            }
        }
    }

    private MindMap generateDefaultMindMap() {
        MindMap defaultMap = new MindMap("默认索引");
        String[] firstLevel = {"人工智能","前端开发","后端开发","移动/游戏开发","大数据云计算","测试运维","密码安全","学科基础","编程语言"};
        String[] secondLevel = {"机器学习 深度学习 计算机视觉 数字图像处理 自然语言处理",
                "Vue React Angular Html Css JavaScript TypeScript Node.js JQuery Bootstrap Webpack 微信小程序",
                "SpringBoot SpringMVC Flask Django SpringCloud Redis Mongodb Mysql Oracle Neo4j Spring 消息队列",
                "IOS Android 游戏开发",
                "Linux Hadoop Hive Spark Hbase 数据分析与挖掘",
                "Docker Kubernates 软件测试 Nginx",
                "密码学 网络安全 计算机病毒",
                "面向对象分析与设计 数据库原理 计算机网络 计算机组成 操作系统 UML 数字逻辑 数据结构 算法 编译原理 离散数学 形式化方法 专业英语 分布式系统 高等数学 线性代数 概率论与数理统计 凸优化 数值计算与优化 数学建模",
                "Java C C++ Python C# Go PHP Kotlin Scala"
        };
        for (int i = 0; i < firstLevel.length; i++) {
            MindMap mindMap = new MindMap(firstLevel[i]);
            String[] children = secondLevel[i].split(" ");
            for (String child : children) {
                mindMap.addComponent(new MindMap(child));
            }
            defaultMap.addComponent(mindMap);
        }
        return defaultMap;
    }
    private void moveToMenu(String tag, String noteId, String title, MindMap mindMap) {
        if (mindMap.isLeaf()) {
            return;
        }
        if (mindMap.getId().equals(tag)) {
            mindMap.addComponent(new MindMap(title, noteId));
            return;
        }
        for (int i = 0; i < mindMap.getChildren().size(); i++) {
            moveToMenu(tag, noteId, title, mindMap.getChildren().get(i));
        }
    }
    
    private void sendActivateLink(User user) {
        String content="<html>\n"+"<body>\n"
                + "<a href='http://localhost:8080/notehub/noteApi/user/activate?code="+user.getActiveCode()+"'>点击激活NoteHub账号</a>\n"
                +"</body>\n"+"</html>!" + user.getEmail();
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.MAIL_QUEUE, content);
        log.info("html:{}",content);
    }

    private void increment(String field, String noteId) {
        if (redisTemplate.opsForHash().hasKey(field, noteId)) {
            redisTemplate.opsForHash().increment(field, noteId, 1);
        } else {
            redisTemplate.opsForHash().put(field, noteId, "0");
        }
    }
}
