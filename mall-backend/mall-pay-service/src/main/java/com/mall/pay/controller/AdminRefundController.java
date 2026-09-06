package com.mall.pay.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.Result;
import com.mall.pay.entity.Refund;
import com.mall.pay.mapper.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 平台退款管理（网关已验证 type=1）：退款流水查看（处理动作见用户端申请=MOCK 渠道即时成功） */
@RestController
@RequiredArgsConstructor
public class AdminRefundController {

    private final RefundMapper refundMapper;

    @GetMapping("/api/admin/refunds")
    public Result<Page<Refund>> list(@RequestParam(required = false) Integer status,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return Result.ok(refundMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Refund>()
                        .eq(status != null, Refund::getStatus, status)
                        .orderByDesc(Refund::getCreateTime)));
    }
}
