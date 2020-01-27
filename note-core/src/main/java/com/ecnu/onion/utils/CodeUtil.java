package com.ecnu.onion.utils;

import java.util.Random;

/**
 * @author onion
 * @date 2020/1/27 -6:07 下午
 */
public class CodeUtil {
    private static final String SOURCE = "0123456789";
    public static String getCode(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 6; i++){
            sb.append(SOURCE.charAt(random.nextInt(10)));
        }
        return sb.toString();
    }
}
