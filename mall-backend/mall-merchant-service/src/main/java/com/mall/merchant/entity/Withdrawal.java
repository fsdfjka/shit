package com.mall.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 提现单（卡信息快照，物理保留） */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("withdrawal")
public class Withdrawal extends BaseDO {

    private String withdrawalNo;

    private Long merchantId;

    private String bankName;

    private String accountNo;

    private String holder;

    private BigDecimal amount;

    /** 0 待处理 1 成功 2 驳回 */
    private Integer status;

    private String rejectReason;

    private LocalDateTime handleTime;
}
