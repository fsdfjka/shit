package com.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单域服务：购物车、下单（跨店拆单 + 库存扣减）、订单状态机、超时取消（RocketMQ 延迟消息）。
 * 前置：Nacos/Redis/RocketMQ 于 192.168.193.131，MySQL localhost。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.order.mapper")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

