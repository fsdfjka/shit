package com.mall.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.MallConstants;
import com.mall.common.Result;
import com.mall.merchant.entity.Withdrawal;
import com.mall.merchant.service.MerchantService;
import com.mall.merchant.service.WithdrawalService;
import com.mall.merchant.vo.MerchantProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 商家端（网关已验证 type=2） */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final WithdrawalService withdrawalService;

    @GetMapping("/profile")
    public Result<MerchantProfileVO> profile(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId) {
        return Result.ok(merchantService.profile(merchantId));
    }

    @GetMapping("/shop")
    public Result<MerchantProfileVO> shop(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId) {
        return Result.ok(merchantService.profile(merchantId));
    }

    @PutMapping("/shop")
    public Result<Void> updateShop(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                                   @RequestBody MerchantService.MerchantPatch patch) {
        merchantService.updateShop(merchantId, patch);
        return Result.ok();
    }

    @PostMapping("/withdrawal")
    public Result<Void> applyWithdrawal(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                                        @RequestBody WithdrawalService.WithdrawalRequest req) {
        withdrawalService.apply(merchantId, req);
        return Result.ok();
    }

    @GetMapping("/withdrawal")
    public Result<Page<Withdrawal>> myWithdrawals(@RequestHeader(MallConstants.HEADER_USER_ID) Long merchantId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return Result.ok(withdrawalService.myPage(merchantId, page, size));
    }
}
