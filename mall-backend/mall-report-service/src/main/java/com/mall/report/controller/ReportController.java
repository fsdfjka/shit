package com.mall.report.controller;

import com.mall.common.Result;
import com.mall.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 统计报表（网关规则：type=1 管理员 / type=2 商家均可访问） */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** merchantId 传了=本店口径（商家看板），不传=全平台（管理员看板） */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard(@RequestParam(required = false) Long merchantId) {
        return Result.ok(reportService.dashboard(merchantId));
    }
}
