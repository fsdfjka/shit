package com.mall.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 发货请求 */
@Data
public class ShipRequest {

    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;

    @NotBlank(message = "运单号不能为空")
    private String trackingNo;
}
