package com.ecnu.onion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author onion
 * @date 2020/1/22 -8:13 下午
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GraphApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphApplication.class, args);
    }
}
