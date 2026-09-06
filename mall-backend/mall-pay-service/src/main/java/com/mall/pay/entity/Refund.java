package com.mall.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 退款单（物理保留） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("refund")
public class Refund extends BaseDO {

    private String refundNo;

    private String payNo;

    private String orderNo;

    /** 整单退（≤ pay_amount） */
    private BigDecimal amount;

    /** 退款幂等标识：同单退款失败重试不重复扣款 */
    private String outRequestNo;

    private String reason;

    /** 0 处理中 1 成功 2 失败 */
    private Integer status;

    private LocalDateTime handleTime;
}
