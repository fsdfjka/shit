package com.mall.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 取消链路关单：直连同库 pay_info（与订单取消同事务）。
 * 约定：生产应按 pay-service 内部接口 lockAndClose 编排；单库演示环境同事务直写（注释见 OrderService）。
 */
@Mapper
public interface PayInfoMapper {

    @Select("SELECT status FROM pay_info WHERE order_no = #{orderNo}")
    Integer selectStatus(@Param("orderNo") String orderNo);

    /** 仅"待支付"可关闭（幂等：非待支付影响 0 行） */
    @Update("UPDATE pay_info SET status = 3, update_time = NOW() WHERE order_no = #{orderNo} AND status = 0")
    int closePending(@Param("orderNo") String orderNo);
}
