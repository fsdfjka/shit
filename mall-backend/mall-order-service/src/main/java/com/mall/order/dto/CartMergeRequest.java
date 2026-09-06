package com.mall.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 游客购物车合并请求（localStorage -> cart 表） */
@Data
public class CartMergeRequest {

    @NotEmpty(message = "待合并项不能为空")
    private List<Item> items;

    @Data
    public static class Item {

        @NotNull(message = "skuId 不能为空")
        private Long skuId;

        @NotNull(message = "数量不能为空")
        private Integer count;
    }
}
