package com.mall.order.vo;

import com.mall.order.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 订单视图（列表/详情共用） */
@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long merchantId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal freight;

    private Integer status;

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

    private LocalDateTime createTime;

    private List<OrderItem> items;
}
