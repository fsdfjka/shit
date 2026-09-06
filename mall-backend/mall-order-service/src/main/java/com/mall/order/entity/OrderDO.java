package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 订单主表（状态机 + 物流 + 地址快照；order 为保留字须反引号）。物理保留。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`order`")
public class OrderDO extends BaseDO {

    private String orderNo;

    private Long userId;

    /** 一单一商家（跨店购物车拆单） */
    private Long merchantId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal freight;

    /** 0待支付 1已支付 2已发货 3已收货 4已取消 5退款中 6已退款 */
    private Integer status;

    private Integer lastStatus;

    private LocalDateTime payTime;

    private LocalDateTime cancelTime;

    private String remark;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String logisticsCompany;

    private String trackingNo;

    private LocalDateTime sendTime;

    private LocalDateTime receiveTime;
}
