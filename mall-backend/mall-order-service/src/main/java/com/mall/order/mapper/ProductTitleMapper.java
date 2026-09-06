package com.mall.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** 只读 product.title（订单明细快照来源，读模型约定） */
@Mapper
public interface ProductTitleMapper {

    @Select("SELECT title FROM product WHERE id = #{id}")
    String selectTitle(@Param("id") Long id);
}
