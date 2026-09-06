package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 线上购物车（物理删除：有 uk_user_sku 唯一约束，见数据库文档） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart")
public class Cart extends BaseDO {

    private Long userId;

    private Long skuId;

    private Long productId;

    private Integer count;

    /** 0 未勾选 1 勾选结算 */
    private Integer checked;
}
