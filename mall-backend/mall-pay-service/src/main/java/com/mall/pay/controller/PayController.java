package com.mall.pay.controller;

import com.mall.common.MallConstants;
import com.mall.common.Result;
import com.mall.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 用户支付（网关已验证 type=0） */
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    /** 创建支付单：返回 payNo（Mock 渠道即跳转凭证；真实沙箱返回跳转 URL） */
    @PostMapping("/create")
    public Result<String> create(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                 @RequestBody Map<String, String> body) {
        return Result.ok(payService.create(userId, body.get("orderNo")));
    }

    /**
     * 演示用模拟回调（真实沙箱时改由支付宝异步通知验签后进入）。
     * 入参 {payNo, tradeNo, amount}：amount 与支付单不一致则拒绝入账（金额核对）。
     */
    @PostMapping("/mock/callback")
    public Result<Void> mockCallback(@RequestBody Map<String, String> body) {
        payService.handleCallback(body.get("payNo"), body.get("tradeNo"), "amount=" + body.get("amount"));
        return Result.ok();
    }

    @GetMapping("/status/{orderNo}")
    public Result<Boolean> status(@PathVariable String orderNo) {
        return Result.ok(payService.isPaid(orderNo));
    }

    /** 退款申请（最小实现：Mock 渠道即时成功；真实沙箱为异步） */
    @PostMapping("/refund")
    public Result<String> refund(@RequestHeader(MallConstants.HEADER_USER_ID) Long userId,
                                 @RequestBody Map<String, String> body) {
        return Result.ok(payService.refund(userId, body.get("orderNo"), body.get("reason")));
    }
}
