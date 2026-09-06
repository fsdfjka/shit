package com.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品域服务：类目/商品/SKU/广告。
 * scanBasePackages="com.mall"：扫描 mall-common 公共组件；前台浏览走 /api/portal/**，
 * 商家管理走 /api/product/**（网关校验 type=2），平台运营（类目/广告）走 /api/admin/**。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.product.mapper")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
