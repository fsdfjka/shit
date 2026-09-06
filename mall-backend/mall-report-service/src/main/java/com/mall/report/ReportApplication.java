package com.mall.report;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mall.common.MyMetaObjectHandler;
import org.springframework.context.annotation.Import;

/**
 * 报表域服务：营收/入驻/进出账/数据看板聚合（只读模型例外——直连主库做聚合查询，
 * 写路径均在各自业务服务；口径见 database-design §5，退款中/已退款单不计营收）。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.report.mapper")
@Import(MyMetaObjectHandler.class)
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
