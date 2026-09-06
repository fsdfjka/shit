package com.mall.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 支付单（回调幂等锚点：锁 + out_trade_no 唯一索引 + 状态机），物理保留 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_info")
public class PayInfo extends BaseDO {

    private String payNo;

    /** 一单一支付单（不做合并支付） */
    private String orderNo;

    private Long userId;

    private BigDecimal amount;

    /** 0 支付宝沙箱 */
    private Integer channel;

    /** 支付宝商户订单号（幂等锚点，可为 NULL，唯一索引允许多 NULL） */
    private String outTradeNo;

    private String tradeNo;

    private String notifyBody;

    /** 0 待支付 1 成功 2 失败 3 关闭 */
    private Integer status;

    private LocalDateTime notifyTime;
}
