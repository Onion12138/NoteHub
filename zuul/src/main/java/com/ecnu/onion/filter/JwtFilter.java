package com.ecnu.onion.filter;

import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author onion
 * @date 2020/2/12 -9:32 上午
 */
@Component
@Slf4j
public class JwtFilter extends ZuulFilter {

    @Autowired
    ObjectMapper mapper;

    private AntPathMatcher matcher = new AntPathMatcher();
    private final String[] path = {
            "/noteApi/user/register/**",
            "/noteApi/user/activate/**",
            "/noteApi/user/login/**"
    };
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        for (String url : path) {
            if (matcher.match(url, context.getRequest().getRequestURI())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("token").substring(5);
        Claims claims;
        try {
            //解析没有异常则表示token验证通过，如有必要可根据自身需求增加验证逻辑
            claims = JwtUtil.parseJwt(token);
            log.info("token : {} 验证通过", token);
            //对请求进行路由
            ctx.setSendZuulResponse(true);
            //请求头加入userId，传给业务服务
            ctx.addZuulRequestHeader("email", claims.getId());
        } catch (ExpiredJwtException expiredJwtEx) {
            log.error("token : {} 过期", token );
            ctx.setSendZuulResponse(false);
            throw new CommonServiceException(ServiceEnum.INVALID_TOKEN);
        } catch (Exception ex) {
            log.error("token : {} 验证失败" , token );
            ctx.setSendZuulResponse(false);
            throw new CommonServiceException(-1, ex.getMessage());
        }
        return null;
    }

}
