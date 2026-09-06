package com.mall.common.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * MinIO 对象存储配置（mall.minio.*）：
 * {@code
 *   mall:
 *     minio:
 *       endpoint: http://192.168.193.131:9000
 *       access-key: root
 *       secret-key: xxxx
 *       bucket: mall-x
 *       public-url: http://192.168.193.131:9000   # 可选；默认取 endpoint
 * }
 * 各业务服务 use {@link MinioUploader} 上传图片；本类 @Component 注册（配合各服务 scanBasePackages="com.mall" 扫描到）。
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "mall.minio")
public class MinioConfig {

    /** 服务地址（对外 http://192.168.193.131:9000） */
    private String endpoint;
    private String accessKey;
    private String secretKey;
    /** 默认桶（自动创建） */
    private String bucket = "mall-x";
    /** 公开访问 URL（桶内对象可直接 GET 时使用；默认与 endpoint 同源） */
    private String publicUrl;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        log.info("[MinIO] 初始化完成 endpoint={} bucket={}", endpoint, bucket);
        return client;
    }
}

