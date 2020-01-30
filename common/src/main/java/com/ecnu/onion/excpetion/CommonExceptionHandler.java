package com.ecnu.onion.excpetion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author onion
 * @date 2020/1/23 -10:31 上午
 */
@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseBody
//    public BaseResponseVO serviceExceptionHandler(RuntimeException e){
//        return BaseResponseVO.serviceException(e);
//    }
}
