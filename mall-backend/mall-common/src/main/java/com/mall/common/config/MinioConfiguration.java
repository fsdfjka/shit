package com.mall.common.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 组件条件装配：当配置了 mall.minio.endpoint 时才注册 MinioClient（网关等无配置服务自动跳过）。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mall.minio", name = "endpoint")
@EnableConfigurationProperties(MinioConfig.class)
public class MinioConfiguration {

    private final MinioConfig minioConfig;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                .build();
        log.info("[MinIO] 初始化完成 endpoint={} bucket={}", minioConfig.getEndpoint(), minioConfig.getBucket());
        return client;
    }
}
