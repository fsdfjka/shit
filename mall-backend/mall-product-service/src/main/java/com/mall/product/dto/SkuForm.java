package com.mall.product.dto;

import lombok.Data;

import java.math.BigDecimal;

/** 商品表单中的单个 SKU 行 */
@Data
public class SkuForm {

    private Long id;

    private String specJson;

    private BigDecimal price;

    private Integer stock;

    private String remark;

    /** 0 正常 1 停售 */
    private Integer status;
}
