package com.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 统一网关（WebFlux，非 Servlet）。
 * 注册 Nacos（192.168.193.131:8848），路由 + JWT 鉴权过滤器见 AuthGlobalFilter。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
