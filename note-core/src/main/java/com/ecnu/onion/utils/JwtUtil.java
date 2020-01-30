package com.ecnu.onion.utils;

import com.ecnu.onion.domain.mongo.User;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author onion
 * @date 2020/1/28 -10:56 上午
 */
public class JwtUtil {
    private static final String KEY = "notehub";
    private static final long ttl = 60 * 60 * 24 * 1000;
    public static String createJwt(User user){
        long now = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setId(user.getEmail())
                .setIssuedAt(new Date(now))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .setExpiration(new Date(now + ttl));
        return builder.compact();
    }
    public static Claims parseJwt(String jwtStr){
        try{
            return Jwts.parser().setSigningKey(KEY).parseClaimsJws(jwtStr).getBody();
        }catch (Exception e){
            throw new CommonServiceException(ServiceEnum.INVALID_TOKEN);
        }
    }
}
