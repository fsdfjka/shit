package com.mall.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 支付域服务：支付单创建（支付宝沙箱/Mock 渠道）、回调幂等处理（锁+唯一索引+状态机）、退款。
 * 渠道抽象 AlipayGatewayClient：默认 Mock（可直接演示），配置密钥后切换真实沙箱实现。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.pay.mapper")
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
