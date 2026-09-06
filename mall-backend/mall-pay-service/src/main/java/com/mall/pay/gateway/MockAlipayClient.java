package com.mall.pay.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mock 渠道：无密钥闭环演示（回调由 /api/pay/mock/callback 触发，走同一回调处理链路）。
 * 真实沙箱接入见 AlipayGatewayClient 注释。
 */
@Slf4j
@Component
public class MockAlipayClient implements AlipayGatewayClient {

    @Override
    public String createPay(String payNo, String outTradeNo, BigDecimal amount) {
        log.info("[MOCK渠道] 创建支付: outTradeNo={}, amount={}", outTradeNo, amount);
        return payNo;
    }

    @Override
    public void closePay(String outTradeNo) {
        log.info("[MOCK渠道] 关闭支付: outTradeNo={}", outTradeNo);
    }

    @Override
    public String refundPay(String outTradeNo, String outRequestNo, BigDecimal amount) {
        log.info("[MOCK渠道] 退款成功: outTradeNo={}, outRequestNo={}, amount={}", outTradeNo, outRequestNo, amount);
        return "MOCK-REFUND-" + outRequestNo;
    }
}
