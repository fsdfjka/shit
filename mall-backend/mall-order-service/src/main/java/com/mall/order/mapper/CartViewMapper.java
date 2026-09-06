package com.mall.order.mapper;

import com.mall.order.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 购物车联表查询（cart + sku + product 只读 join，展示用） */
@Mapper
public interface CartViewMapper {

    @Select("SELECT c.id, c.user_id, c.sku_id, c.product_id, c.count, c.checked, " +
            "s.spec_json, s.price, s.stock, s.status AS sku_status, p.title AS product_title, p.main_img " +
            "FROM cart c " +
            "LEFT JOIN sku s ON c.sku_id = s.id " +
            "LEFT JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} ORDER BY c.create_time DESC")
    List<CartVO> selectCartByUser(@Param("userId") Long userId);
}
