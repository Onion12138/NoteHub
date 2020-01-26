package com.ecnu.onion.task;

import com.ecnu.onion.dao.NoteInfoDao;
import com.ecnu.onion.dao.UserInfoDao;
import com.ecnu.onion.domain.entity.NoteInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author onion
 * @date 2020/1/26 -11:07 上午
 */
@Configuration
@EnableScheduling
public class NoteRelationTask {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private NoteInfoDao noteInfoDao;
    public static final String KEY = "active_user";
    //每天11：30执行
    @Scheduled(cron = "0 30 23 * * ?")
    private void performTask() {
        SetOperations<String, String> set = redisTemplate.opsForSet();
        while (Optional.ofNullable(set.size(KEY)).orElse(0L) > 0) {
            String email = set.pop(KEY);
            List<NoteInfo> notes = userInfoDao.findViewedNote(email, LocalDate.now().toString());
            if (notes == null || notes.size() < 2) {
                continue;
            }
            NoteInfo start = notes.get(0);
            notes.stream().skip(1).map(NoteInfo::getNoteId).forEach(e->noteInfoDao.addRelation(start.getNoteId(), e));
        }
    }
}
