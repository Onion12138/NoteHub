package com.ecnu.onion.vo;

import com.ecnu.onion.excpetion.CommonServiceException;
import lombok.Data;

/**
 * @author onion
 * @date 2020/1/23 -10:28 上午
 */
@Data
public class BaseResponseVO<T> {
    private Integer code;   // 业务编号
    private String message; // 异常信息
    private T data;         // 业务数据返回

    private BaseResponseVO(){}

    public static<T> BaseResponseVO<T> success(){
        BaseResponseVO<T> response = new BaseResponseVO<>();
        response.setCode(200);
        response.setMessage("success");
        return response;
    }

    public static<T> BaseResponseVO<T> success(T data){
        BaseResponseVO<T> response = new BaseResponseVO<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static<T> BaseResponseVO<T> serviceException(RuntimeException e){
        BaseResponseVO<T> response = new BaseResponseVO<>();
        if (e instanceof CommonServiceException) {
            response.setCode(((CommonServiceException)e).getCode());
        } else {
            response.setCode(-1);
        }
        response.setMessage(e.getMessage());
        return response;
    }

}
