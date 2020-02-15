package com.ecnu.haven.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;

/**
 * @author HavenTong
 * @date 2020/2/14 7:41 下午
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter exporter() {
        return new ServerEndpointExporter();
    }

}
