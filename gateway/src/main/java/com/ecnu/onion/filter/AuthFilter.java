package com.ecnu.onion.filter;

import com.alibaba.fastjson.JSONObject;
import com.ecnu.onion.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author onion
 * @date 2020/3/9 -8:06 下午
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    private AntPathMatcher matcher = new AntPathMatcher();
    private final String[] ignorePaths = {
            "/noteApi/user/register/**",
            "/noteApi/user/sendCode/**",
            "/noteApi/user/login/**"
    };
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("path:{}",path);
        for (String url : ignorePaths){
            if (matcher.match(url,path)) {
                return chain.filter(exchange);
            }
        }
        String rawToken = exchange.getRequest().getHeaders()
                .getFirst("token");
        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isEmpty(rawToken)){
            JSONObject message = new JSONObject();
            message.put("message", "鉴权失败，无token或类型");
            byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }else{
            String token = rawToken.substring(5);
            String email = JwtUtil.parseJwt(token);
            ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("email", email).build();
            ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
            return chain.filter(mutableExchange);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
