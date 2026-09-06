package com.mall.order.mapper;

import com.mall.order.entity.ProductMerchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 只读 product.merchant_id：跨店拆单按商家归属（读模型约定） */
@Mapper
public interface ProductMerchantMapper {

    @Select("<script>" +
            "SELECT id, merchant_id FROM product WHERE id IN " +
            "<foreach collection='ids' item='i' open='(' separator=',' close=')'>#{i}</foreach>" +
            "</script>")
    List<ProductMerchant> selectByKeys(@Param("ids") List<Long> ids);
}
