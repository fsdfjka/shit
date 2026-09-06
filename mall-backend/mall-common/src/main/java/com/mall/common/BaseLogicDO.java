package com.mall.common;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 逻辑删除基类：仅内容类表继承（category / product / advert / user_address）。
 * 注意：sku、cart 因有业务唯一约束，不继承本类（物理删除），否则软删后重建同键值会撞唯一索引。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseLogicDO extends BaseDO {

    @TableLogic
    private Integer deleted;
}
