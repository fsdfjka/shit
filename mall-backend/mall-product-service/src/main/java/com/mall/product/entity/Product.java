package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseLogicDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 商品（逻辑删除；展示价动态 MIN(sku.price)，无冗余列） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseLogicDO {

    private Long merchantId;

    private Long categoryId;

    private String title;

    private String subtitle;

    private String mainImg;

    private String detail;

    /** 0 上架 1 下架 */
    private Integer status;

    private Integer saleCount;
}
