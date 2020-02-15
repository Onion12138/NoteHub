package com.ecnu.haven.socket;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.vo.BaseResponseVO;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author HavenTong
 * @date 2020/2/14 7:50 下午
 * websocket发送json数据的encoder
 */
@Slf4j
public class BaseResponseVOEncoder implements Encoder.Text<BaseResponseVO> {

    @Override
    public String encode(BaseResponseVO baseResponseVO) throws EncodeException {
        String result = JSON.toJSONString(baseResponseVO);
        log.info("encode: {}", result);
        return result;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
