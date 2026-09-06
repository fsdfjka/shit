package com.mall.order.entity;

import lombok.Data;

import java.math.BigDecimal;

/** 下单所需 SKU 只读视图（sku 表） */
@Data
public class SkuOrder {

    private Long id;

    private Long productId;

    private String specJson;

    private BigDecimal price;

    private Integer stock;

    private Integer status;
}
