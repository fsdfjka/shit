package com.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 商家入驻申请（创建 merchant，applyStatus=0 待审核） */
@Data
public class MerchantApplyRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "商家名不能为空")
    private String merchantName;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String contact;

    private String shopName;
}
