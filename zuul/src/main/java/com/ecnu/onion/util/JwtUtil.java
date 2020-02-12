package com.ecnu.onion.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * @author onion
 * @date 2020/2/12 -9:56 上午
 */
public class JwtUtil {
    private static final String KEY = "notehub";
    public static Claims parseJwt(String jwtStr){
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(jwtStr).getBody();
    }
}
