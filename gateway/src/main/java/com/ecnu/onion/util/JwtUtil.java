package com.ecnu.onion.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * @author onion
 * @date 2020/2/12 -9:56 上午
 */
public class JwtUtil {
    private static final String KEY = "notehub";
    public static String parseJwt(String jwtStr){
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(jwtStr);
        return jwt.getKeyId();
    }
}
