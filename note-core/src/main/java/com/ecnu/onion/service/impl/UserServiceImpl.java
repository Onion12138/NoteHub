package com.ecnu.onion.service.impl;

import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.dao.UserDao;
import com.ecnu.onion.domain.User;
import com.ecnu.onion.service.MailService;
import com.ecnu.onion.service.UserService;
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
    private MailService mailService;
    @Override
    public void sendEmail(String email) {
//        String code = CodeUtil.getCode();
//        redisTemplate.opsForValue().set("code_"+email, code, expireTime, TimeUnit.SECONDS);
//        String content = "your code is " + code + " , please complete your registration in 10 minutes.";
//        mailService.sendMail(email,subject,content);
//        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE, "","");
        String content = "<a href='http://localhost:'>点击激活NoteHub账号</a>";
    }

    @Override
    public void register(RegisterVO registerVO)  {
        User user = User.builder().email(registerVO.getEmail()).username(registerVO.getUsername()).password(registerVO.getPassword())
                .disabled(true).activeCode(UuidUtil.getUuid()).build();
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
        user.setDisabled(false);
        userDao.save(user);
    }

    @Override
    public void login(LoginVO loginVO) {
        log.info("user: {}", userDao.findById(loginVO.getEmail()).get());
    }


}
