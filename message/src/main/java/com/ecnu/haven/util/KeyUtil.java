package com.ecnu.haven.util;

import java.util.Random;

/**
 * @author HavenTong
 * @date 2020/2/15 11:18 上午
 * 生成messageId的util
 */
public class KeyUtil {
    public static synchronized String getUniqueKey(){
        Random random = new Random();
        Integer num = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(num);
    }
}
