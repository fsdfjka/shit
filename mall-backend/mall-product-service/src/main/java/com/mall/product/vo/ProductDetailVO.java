package com.mall.product.vo;

import com.mall.product.entity.Sku;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 商品详情（前端 SKU 全量加载、前端过滤规格 —— 见数据库文档注 1） */
@Data
public class ProductDetailVO {

    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String title;

    private String subtitle;

    private String mainImg;

    private String detail;

    private BigDecimal minPrice;

    private String shopName;

    private Integer saleCount;

    private LocalDateTime createTime;

    private List<Sku> skus;
}
