package com.mall.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商家域服务：入驻审核、店铺信息、提现（balance 资金账本，随支付/退款/提现事件更新）。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.merchant.mapper")
public class MerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }
}

