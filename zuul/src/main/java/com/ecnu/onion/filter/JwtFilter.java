package com.ecnu.onion.filter;

import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

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
            "/notehub/noteApi/user/register/**",
            "/notehub/noteApi/user/sendCode/**",
            "/notehub/noteApi/user/login/**"
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
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            return null;
//            throw new CommonServiceException(ServiceEnum.ACCOUNT_NOT_LOGIN);
        }
        token = token.substring(5);
        try {
            String email = JwtUtil.parseJwt(token);
            ctx.setSendZuulResponse(true);
            ctx.addZuulRequestHeader("email", email);
        } catch (Exception ex) {
            ctx.setSendZuulResponse(false);
            throw new CommonServiceException(-1, ex.getMessage());
        }
        return null;
    }

}
