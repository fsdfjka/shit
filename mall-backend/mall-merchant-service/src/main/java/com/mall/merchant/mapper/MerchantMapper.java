package com.mall.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.merchant.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    /** 提现扣余额：条件原子（balance >= amount 才允许），0 行=余额不足 */
    @Update("UPDATE merchant SET balance = balance - #{amount} WHERE id = #{merchantId} AND balance >= #{amount}")
    int deductBalance(@Param("merchantId") Long merchantId, @Param("amount") java.math.BigDecimal amount);

    /** 提现驳回/退款回补余额 */
    @Update("UPDATE merchant SET balance = balance + #{amount} WHERE id = #{merchantId}")
    int addBalance(@Param("merchantId") Long merchantId, @Param("amount") java.math.BigDecimal amount);
}
