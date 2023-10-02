package com.youquan.wemedia.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 23:46
 */
@EnableDiscoveryClient
@SpringBootApplication
public class WemediaGatewayAplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaGatewayAplication.class, args);
    }
}
