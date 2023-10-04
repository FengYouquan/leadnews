package com.youquan.wemedia;

import com.youquan.api.article.IArticleClient;
import com.youquan.api.schedule.IScheduleClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:20
 */
@EnableScheduling
@EnableAsync
@EnableFeignClients(clients = {IArticleClient.class, IScheduleClient.class})
@EnableDiscoveryClient
@SpringBootApplication
public class WemediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args);
    }
}
