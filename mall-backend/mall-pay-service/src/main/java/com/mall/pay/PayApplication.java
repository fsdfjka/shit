package com.mall.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Pay 域服务（最小骨架）。scanBasePackages="com.mall"：扫描 mall-common 公共组件。
 * 业务开发时补充 @MapperScan("com.mall.pay.mapper")。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
