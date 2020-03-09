package com.ecnu.onion.excpetion;

import com.ecnu.onion.vo.BaseResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author onion
 * @date 2020/1/23 -10:31 上午
 */
@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public BaseResponseVO serviceExceptionHandler(RuntimeException e){
        return BaseResponseVO.serviceException(e);
    }
}
