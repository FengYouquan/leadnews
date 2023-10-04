package com.youquan.wemedia;

import com.youquan.api.article.IArticleClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:20
 */
@EnableAsync
@EnableFeignClients(clients = IArticleClient.class)
@EnableDiscoveryClient
@SpringBootApplication
public class WemediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args);
    }
}
