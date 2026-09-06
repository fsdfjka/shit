package com.mall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Order 域服务（最小骨架）。scanBasePackages="com.mall"：扫描 mall-common 公共组件。
 * 业务开发时补充 @MapperScan("com.mall.order.mapper")。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
