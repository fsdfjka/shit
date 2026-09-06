package com.mall.pay.gateway;

import java.math.BigDecimal;

/**
 * 支付渠道抽象（支付创建/关闭/退款/查询）。
 * 实现：
 *  - MockAlipayClient（默认，@Primary：创建/退款同步成功，便于无密钥演示闭环）
 *  - AlipaySandboxClient（TODO：接入支付宝开放平台沙箱，需 appId/应用私钥/支付宝公钥，
 *    对应 alipay-sdk-3.x；开通后替换 Bean 打开真实回调验签——密钥配置位见 application.yml 注释）
 */
public interface AlipayGatewayClient {

    /** 创建支付：返回支付跳转地址/凭证（Mock 返回 payNo） */
    String createPay(String payNo, String outTradeNo, BigDecimal amount);

    /** 关闭支付 */
    void closePay(String outTradeNo);

    /**
     * 渠道退款：返回渠道退款流水号（失败抛异常）。
     * outRequestNo 为幂等标识：同单重试传相同值，渠道只退一次。
     */
    String refundPay(String outTradeNo, String outRequestNo, BigDecimal amount);
}
