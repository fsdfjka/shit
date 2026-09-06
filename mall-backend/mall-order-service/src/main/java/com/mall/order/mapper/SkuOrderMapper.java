package com.mall.order.mapper;

import com.mall.order.entity.SkuOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 下单直连同库 sku 表（读价格/校验库存/条件扣减/回补）。
 * 约定说明（单库演示环境）：库存扣减与订单创建在同事务（MySQL 同库同连接）；
 * 生产拆分时改为调用 product-service 本地事务接口或消息编排（见 OrderService 注释）。
 */
@Mapper
public interface SkuOrderMapper {

    @Select("SELECT * FROM sku WHERE id = #{id} AND status = 0")
    SkuOrder selectActive(Long id);

    /** 全部在售 SKU（预热用） */
    @Select("SELECT * FROM sku WHERE status = 0")
    List<SkuOrder> selectAllActive();

    @Select("<script>" +
            "SELECT * FROM sku WHERE id IN " +
            "<foreach collection='ids' item='i' open='(' separator=',' close=')'>#{i}</foreach>" +
            " AND status = 0</script>")
    List<SkuOrder> selectActiveBatch(@Param("ids") List<Long> ids);

    /** 条件扣减：影响 0 行即库存不足（天然幂等） */
    @Update("UPDATE sku SET stock = stock - #{count} WHERE id = #{skuId} AND stock >= #{count} AND status = 0")
    int deductStock(@Param("skuId") Long skuId, @Param("count") int count);

    /** 回补库存（取消/失败补偿） */
    @Update("UPDATE sku SET stock = stock + #{count} WHERE id = #{skuId}")
    int refundStock(@Param("skuId") Long skuId, @Param("count") int count);
}
