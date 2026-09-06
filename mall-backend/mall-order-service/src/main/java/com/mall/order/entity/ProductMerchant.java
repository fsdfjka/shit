package com.mall.order.entity;

import lombok.Data;

/** 只读 product 归属（product_id -> merchant_id），跨店拆单使用 */
@Data
public class ProductMerchant {

    private Long id;

    private Long merchantId;
}
