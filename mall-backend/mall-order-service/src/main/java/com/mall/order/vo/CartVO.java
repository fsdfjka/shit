package com.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/** 购物车行（联表展示） */
@Data
public class CartVO {

    private Long id;

    private Long userId;

    private Long skuId;

    private Long productId;

    private Integer count;

    private Integer checked;

    private String specJson;

    private BigDecimal price;

    private Integer stock;

    private Integer skuStatus;

    private String productTitle;

    private String mainImg;
}
