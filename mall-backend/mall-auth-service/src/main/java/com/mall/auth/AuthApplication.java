package com.mall.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mall.common.MyMetaObjectHandler;
import org.springframework.context.annotation.Import;

/**
 * 认证服务。scanBasePackages="com.mall"：扫描 mall-common 的
 * GlobalExceptionHandler / MyMetaObjectHandler 等公共组件。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.auth.mapper")
@Import(MyMetaObjectHandler.class)
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
