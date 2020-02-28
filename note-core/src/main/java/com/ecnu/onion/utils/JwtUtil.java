package com.ecnu.onion.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ecnu.onion.domain.mongo.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;

/**
 * @author onion
 * @date 2020/1/28 -10:56 上午
 */
@Slf4j
public class JwtUtil {
    private static final String KEY = "notehub";
    private static final long ttl = 60 * 60 * 24 * 1000;
    public static String createJwt(User user){
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        log.info("email:{}",user.getEmail());
        //附带username和userID生成签名
        return JWT.create()
                .withHeader(header)
                .withKeyId(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + ttl))
                .sign(Algorithm.HMAC256(KEY));
    }
}
