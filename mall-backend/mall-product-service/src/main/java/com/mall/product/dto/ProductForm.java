package com.mall.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 商家保存商品表单（新建/编辑） */
@Data
public class ProductForm {

    private Long id;

    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @NotBlank(message = "商品名不能为空")
    private String title;

    private String subtitle;

    @NotBlank(message = "主图不能为空")
    private String mainImg;

    private String detail;

    /** 0 上架 1 下架 */
    private Integer status = 0;

    @NotNull(message = "至少一个规格")
    private List<SkuForm> skus;
}
