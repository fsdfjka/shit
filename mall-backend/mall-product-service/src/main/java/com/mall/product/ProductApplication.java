package com.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product 域服务（最小骨架）。scanBasePackages="com.mall"：扫描 mall-common 公共组件。
 * 业务开发时补充 @MapperScan("com.mall.product.mapper")。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
