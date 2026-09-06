package com.mall.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 下单请求：前端只传 skuId+count+收货信息，金额由服务端按 SKU 现价重算（防改价） */
@Data
public class CreateOrderRequest {

    /** 前端生成的请求幂等键（Redis 去重） */
    @NotBlank(message = "请求标识不能为空")
    private String reqId;

    @NotEmpty(message = "商品不能为空")
    private List<Item> items;

    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /** true：下单成功后删除购物车已勾选行 */
    private Boolean fromCart = false;

    @Data
    public static class Item {

        @NotNull(message = "skuId 不能为空")
        private Long skuId;

        @NotNull(message = "数量不能为空")
        private Integer count;
    }
}
