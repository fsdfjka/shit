package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/** 规格 SKU（物理删除：有 uk_product_spec 业务唯一约束，软删后同规格重建撞键） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sku")
public class Sku extends BaseDO {

    private Long productId;

    /** 规格组合 JSON，如 {"颜色":"红","尺码":"L"} */
    private String specJson;

    private BigDecimal price;

    /** DB 基准库存（Redis 预扣目标，库存脚本为后续里程碑） */
    private Integer stock;

    private String img;

    /** 0 正常 1 停售 */
    private Integer status;

    private String remark;
}
