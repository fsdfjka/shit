package com.mall.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
 * 仅配置数据类；由 {@link MinioConfiguration} 在配置存在时注册对应 Bean。
 */
@Data
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
}
