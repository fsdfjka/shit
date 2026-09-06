package com.mall.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.BizException;
import com.mall.merchant.entity.Withdrawal;
import com.mall.merchant.mapper.MerchantMapper;
import com.mall.merchant.mapper.WithdrawalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 提现（资金账本规则，见 database-design §4 商家资金规则）：
 * 申请 -> 校验并原子扣 balance（余额不足拒绝）；驳回 -> 回补 balance；成功 -> 仅状态流转。
 */
@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalMapper withdrawalMapper;
    private final MerchantMapper merchantMapper;

    @Transactional
    public void apply(Long merchantId, WithdrawalRequest req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("提现金额必须大于 0");
        }
        // 原子扣余额：条件更新 0 行=余额不足（balance 为最终账本，单库事务内校验）
        int n = merchantMapper.deductBalance(merchantId, req.getAmount());
        if (n == 0) {
            throw new BizException("可提现余额不足");
        }
        Withdrawal w = new Withdrawal();
        w.setWithdrawalNo(genNo(merchantId));
        w.setMerchantId(merchantId);
        w.setBankName(req.getBankName());
        w.setAccountNo(req.getAccountNo());
        w.setHolder(req.getHolder());
        w.setAmount(req.getAmount());
        w.setStatus(0);
        withdrawalMapper.insert(w);
    }

    public Page<Withdrawal> myPage(Long merchantId, int page, int size) {
        return withdrawalMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Withdrawal>()
                        .eq(Withdrawal::getMerchantId, merchantId)
                        .orderByDesc(Withdrawal::getCreateTime));
    }

    /** 管理员：全部提现申请（待处理优先） */
    public Page<Withdrawal> adminPage(Integer status, int page, int size) {
        return withdrawalMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Withdrawal>()
                        .eq(status != null, Withdrawal::getStatus, status)
                        .orderByAsc(Withdrawal::getStatus)
                        .orderByDesc(Withdrawal::getCreateTime));
    }

    /** 处理：pass=1 打款成功；pass=0 驳回并回补余额（apply 扣 与 reject 补 成对出现） */
    @Transactional
    public void process(Long withdrawalId, boolean pass, String reason) {
        Withdrawal w = withdrawalMapper.selectById(withdrawalId);
        if (w == null) {
            throw new BizException("提现单不存在");
        }
        if (w.getStatus() != 0) {
            throw new BizException("该提现单已处理");
        }
        w.setStatus(pass ? 1 : 2);
        w.setRejectReason(pass ? null : reason);
        w.setHandleTime(LocalDateTime.now());
        withdrawalMapper.updateById(w);
        if (!pass) {
            merchantMapper.addBalance(w.getMerchantId(), w.getAmount());
        }
    }

    private String genNo(Long merchantId) {
        StringBuilder sb = new StringBuilder("W");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")));
        sb.append(String.format("%08d", ThreadLocalRandom.current().nextInt(100000000)));
        return sb.toString();
    }

    @lombok.Data
    public static class WithdrawalRequest {
        private String bankName;
        private String accountNo;
        private String holder;
        private BigDecimal amount;
    }
}
