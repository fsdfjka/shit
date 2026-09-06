package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseLogicDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 首页广告位（逻辑删除） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("advert")
public class Advert extends BaseLogicDO {

    private String title;

    private String imgUrl;

    private String linkUrl;

    private Integer sort;

    /** 0 启用 1 停用 */
    private Integer status;
}
