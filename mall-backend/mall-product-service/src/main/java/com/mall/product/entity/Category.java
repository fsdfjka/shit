package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseLogicDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 类目（逻辑删除；两级，parent_id=0 为根） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseLogicDO {

    private Long parentId;

    private String name;

    private String icon;

    private Integer sort;

    /** 0 启用 1 停用 */
    private Integer status;
}
