package com.ecnu.onion.vo;

import com.ecnu.onion.excpetion.CommonServiceException;
import lombok.Data;

/**
 * @author onion
 * @date 2020/1/23 -10:28 上午
 */
@Data
public class BaseResponseVO {
    private Integer code;   // 业务编号
    private String message; // 异常信息
    private Object data;         // 业务数据返回

    private BaseResponseVO(){}

    public static<T> BaseResponseVO success(){
        BaseResponseVO response = new BaseResponseVO ();
        response.setCode(200);
        response.setMessage("success");
        return response;
    }

    public static BaseResponseVO success(Object data){
        BaseResponseVO response = new BaseResponseVO();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static BaseResponseVO error(String message) {
        BaseResponseVO response = new BaseResponseVO();
        response.setCode(200);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
    public static BaseResponseVO serviceException(RuntimeException e){
        BaseResponseVO response = new BaseResponseVO();
        if (e instanceof CommonServiceException) {
            response.setCode(((CommonServiceException)e).getCode());
        } else {
            response.setCode(-1);
        }
        response.setMessage(e.getMessage());
        return response;
    }

}
