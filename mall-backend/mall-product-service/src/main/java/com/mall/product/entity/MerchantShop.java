package com.mall.product.entity;

import lombok.Data;

/** 只读商家店铺名（来自 merchant 表，仅用于展示） */
@Data
public class MerchantShop {

    private Long id;

    private String merchantName;

    private String shopName;
}
