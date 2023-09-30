package com.youquan.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 0:01
 */
@ConfigurationProperties(prefix = "minio")
@Data
public class MinIOConfigProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;
}
