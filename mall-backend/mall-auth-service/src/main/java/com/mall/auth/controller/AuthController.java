package com.mall.auth.controller;

import com.mall.auth.dto.LoginRequest;
import com.mall.auth.dto.MerchantApplyRequest;
import com.mall.auth.dto.RegisterRequest;
import com.mall.auth.service.AuthService;
import com.mall.auth.vo.LoginResponse;
import com.mall.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 认证入口（网关白名单：/api/auth/login、/api/auth/register、/api/auth/register/merchant） */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return Result.ok();
    }

    @PostMapping("/register/merchant")
    public Result<Void> merchantApply(@Valid @RequestBody MerchantApplyRequest req) {
        authService.merchantApply(req);
        return Result.ok();
    }
}
