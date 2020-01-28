package com.ecnu.onion.service.impl;

import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.LoginLogDao;
import com.ecnu.onion.dao.UserDao;
import com.ecnu.onion.domain.User;
import com.ecnu.onion.domain.log.LoginLog;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.utils.JwtUtil;
import com.ecnu.onion.utils.Md5Util;
import com.ecnu.onion.utils.SaltUtil;
import com.ecnu.onion.utils.UuidUtil;
import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private UserDao userDao;

    @Autowired
    private LoginLogDao loginLogDao;

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
        String salt = user.getSalt();
        String rawPassword = loginVO.getPassword();
        if (!user.getPassword().equals(Md5Util.encrypt(rawPassword + salt))) {
            throw new CommonServiceException(ServiceEnum.WRONG_PASSWORD);
        }
        LoginLog loginLog = LoginLog.builder().email(user.getEmail()).username(user.getUsername())
                .loginTime(LocalDateTime.now()).build();
        loginLogDao.insert(loginLog);
        Map<String, String> map = new HashMap<>();
        map.put("token", JwtUtil.createJwt(user));
        map.put("email",user.getEmail());
        map.put("username",user.getUsername());
        map.put("profileUrl",user.getProfileUrl());
        return map;
    }


}
