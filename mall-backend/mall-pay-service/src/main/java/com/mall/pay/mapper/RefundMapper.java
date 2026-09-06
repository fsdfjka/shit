package com.mall.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.pay.entity.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RefundMapper extends BaseMapper<Refund> {

    /** 状态机置成功：仅"处理中"可置（0 行=重复处理，幂等） */
    @Update("UPDATE refund SET status = 1, handle_time = NOW() WHERE id = #{id} AND status = 0")
    int markSuccess(@Param("id") Long id);

    /** 状态机置失败（渠道异常重试入口） */
    @Update("UPDATE refund SET status = 2, handle_time = NOW() WHERE id = #{id} AND status = 0")
    int markFailed(@Param("id") Long id);
}
