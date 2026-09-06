package com.mall.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Report 域服务（最小骨架）。scanBasePackages="com.mall"：扫描 mall-common 公共组件。
 * 业务开发时补充 @MapperScan("com.mall.report.mapper")。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
