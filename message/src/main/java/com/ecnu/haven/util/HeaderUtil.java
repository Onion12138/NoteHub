package com.ecnu.haven.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HavenTong
 * @date 2020/2/15 10:27 上午
 * 处理头部信息的util
 */
public class HeaderUtil {

    public static String getEmail(){
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getHeader("email");
    }
}
