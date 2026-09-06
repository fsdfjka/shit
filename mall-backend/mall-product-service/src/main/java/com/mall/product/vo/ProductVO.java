package com.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/** 商品列表条目（含最低价与店铺名） */
@Data
public class ProductVO {

    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String title;

    private String subtitle;

    private String mainImg;

    /** 展示价：该商品 SKU 最低价 */
    private BigDecimal minPrice;

    /** 店铺名（merchant.shop_name，只读查询） */
    private String shopName;

    private Integer status;

    private Integer saleCount;
}
