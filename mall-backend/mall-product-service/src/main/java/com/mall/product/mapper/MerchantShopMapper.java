package com.mall.product.mapper;

import com.mall.product.entity.MerchantShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 只读查询商家店铺名（读模型约定）。
 * 说明：微服务下写路径不跨服务；此处仅为商品列表/详情展示"店铺"名称，用原生只读 SQL，
 * 与 report-service 直连库聚合同属"读模型例外"，答辩可自证。
 */
@Mapper
public interface MerchantShopMapper {

    @Select("SELECT id, merchant_name, shop_name FROM merchant WHERE id = #{id}")
    MerchantShop selectNameById(Long id);
}
