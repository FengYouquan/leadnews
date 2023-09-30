package com.youquan.file.config;

import com.youquan.file.service.FileStorageService;
import io.minio.MinioClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 0:03
 */
@Data
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({MinIOConfigProperties.class})
@ConditionalOnClass(FileStorageService.class)
public class MinIOConfig {
    private final MinIOConfigProperties minIOConfigProperties;

    @Bean
    public MinioClient buildMinioClient() {
        return MinioClient.builder()
                .endpoint(minIOConfigProperties.getEndpoint())
                .credentials(minIOConfigProperties.getAccessKey(), minIOConfigProperties.getSecretKey())
                .build();
    }
}
