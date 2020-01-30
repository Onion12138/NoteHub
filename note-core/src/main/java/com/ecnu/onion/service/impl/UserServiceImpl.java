package com.ecnu.onion.service.impl;

import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.UserDao;
import com.ecnu.onion.domain.mongo.Note;
import com.ecnu.onion.domain.mongo.User;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.utils.JwtUtil;
import com.ecnu.onion.utils.Md5Util;
import com.ecnu.onion.utils.SaltUtil;
import com.ecnu.onion.utils.UuidUtil;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    @Value("2592000")
    private long expireInSeconds;

    //注册的当时如果没有激活呢？尚未解决
    @Override
    public void register(RegisterVO registerVO)  {
        if (userDao.findById(registerVO.getEmail()).isPresent()) {
            throw new CommonServiceException(ServiceEnum.EMAIL_IN_USE);
        }
        String salt = SaltUtil.getSalt();
        String password = Md5Util.encrypt(registerVO.getPassword() + salt);
        User user = User.builder().email(registerVO.getEmail()).username(registerVO.getUsername()).registerTime(LocalDate.now())
                .password(password).salt(salt).profileUrl("https://avatars2.githubusercontent.com/u/33611404?s=400&v=4")
                .activated(false).disabled(false).activeCode(UuidUtil.getUuid()).build();
        userDao.save(user);
        String content="<html>\n"+"<body>\n"
                + "<a href='http://localhost:8080/notehub/noteApi/user/activate?code="+user.getActiveCode()+"'>点击激活NoteHub账号</a>\n"
                +"</body>\n"+"</html>!" + user.getEmail();
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, MQConstant.MAIL_QUEUE, content);
        log.info("html:{}",content);
    }

    @Override
    public void activate(String code) {
        User user = userDao.findByActiveCode(code);
        user.setActivated(true);
        userDao.save(user);
    }

    @Override
    public Map<String, String> login(LoginVO loginVO) {
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
            throw new CommonServiceException(ServiceEnum.ACCOUNT_NOT_ACTIVATED);
        }
        String salt = user.getSalt();
        String rawPassword = loginVO.getPassword();
        if (!user.getPassword().equals(Md5Util.encrypt(rawPassword + salt))) {
            throw new CommonServiceException(ServiceEnum.WRONG_PASSWORD);
        }
        Map<String, String> map = new HashMap<>();
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
        String url = getProfileUrl(key);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(email));
        Update update = new Update();
        update.set("profileUrl", url);
        mongoTemplate.updateFirst(query, update, Note.class);
        return url;
    }

    @Override
    public void modifyPassword(ModificationVO modificationVO) {
        String redisCode = redisTemplate.opsForValue().get("code_" + modificationVO.getEmail());
        if ( redisCode == null) {
            throw new CommonServiceException(ServiceEnum.CODE_NOT_EXIST);
        }
        if (!modificationVO.getCode().equals(redisCode)) {
            throw new CommonServiceException(ServiceEnum.WRONG_CODE);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(modificationVO.getEmail()));
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
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(email));
        Update update = new Update();
        update.set("username", username);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    private String getProfileUrl(String filename){
        String domainOfBucket = "http://ecnuonion.club";
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }
}
