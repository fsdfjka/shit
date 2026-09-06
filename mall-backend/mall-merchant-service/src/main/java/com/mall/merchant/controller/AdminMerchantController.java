package com.mall.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.Result;
import com.mall.merchant.entity.Merchant;
import com.mall.merchant.entity.Withdrawal;
import com.mall.merchant.service.MerchantService;
import com.mall.merchant.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 平台运营（网关已验证 type=1）：入驻审核、提现处理 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMerchantController {

    private final MerchantService merchantService;
    private final WithdrawalService withdrawalService;

    @GetMapping("/merchants")
    public Result<Page<Merchant>> merchants(@RequestParam(required = false) Integer applyStatus,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Merchant> lw =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Merchant>()
                        .eq(applyStatus != null, Merchant::getApplyStatus, applyStatus)
                        .orderByDesc(Merchant::getCreateTime);
        return Result.ok(merchantService.auditPage(lw, page, size));
    }

    @PutMapping("/merchants/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean pass = Boolean.TRUE.equals(body.get("pass"));
        merchantService.audit(id, pass, (String) body.get("reason"));
        return Result.ok();
    }

    @GetMapping("/withdrawals")
    public Result<Page<Withdrawal>> withdrawals(@RequestParam(required = false) Integer status,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return Result.ok(withdrawalService.adminPage(status, page, size));
    }

    @PutMapping("/withdrawals/{id}/process")
    public Result<Void> process(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean pass = Boolean.TRUE.equals(body.get("pass"));
        withdrawalService.process(id, pass, (String) body.get("reason"));
        return Result.ok();
    }
}
