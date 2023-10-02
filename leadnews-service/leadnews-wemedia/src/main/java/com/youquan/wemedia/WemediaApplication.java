package com.youquan.wemedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:20
 */
@EnableDiscoveryClient
@SpringBootApplication
public class WemediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args);
    }
}
