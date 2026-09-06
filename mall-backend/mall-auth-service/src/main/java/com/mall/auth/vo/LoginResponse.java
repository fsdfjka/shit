package com.mall.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 登录响应 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;

    /** 0 用户 1 平台管理员 2 商家 */
    private Integer type;

    private Long id;

    private String username;

    private String nickname;
}
