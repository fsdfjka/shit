package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseLogicDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 收货地址（逻辑删除 BaseLogicDO） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_address")
public class UserAddress extends BaseLogicDO {

    private Long userId;

    private String receiver;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;

    /** 0 否 1 默认 */
    private Integer isDefault;
}
