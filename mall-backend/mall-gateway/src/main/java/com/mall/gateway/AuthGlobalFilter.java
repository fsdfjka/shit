package com.mall.gateway;

import com.mall.common.JwtUtil;
import com.mall.common.MallConstants;
import com.mall.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 全局鉴权过滤器：
 * 1. 白名单（登录/注册）直接放行；
 * 2. 其余请求校验 Authorization: Bearer <token>，合法则转发 X-User-Id / X-User-Type / X-Username 头；
 * 3. 身份-路径规划：/api/admin/** 要求 type=1（平台管理员），为后续管理员接口兜底。
 * 下游服务不再解析 JWT，改读请求头（MallConstants.HEADER_*）。
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Value("${mall.jwt.secret}")
    private String secret;
    @Value("${mall.jwt.expire-seconds}")
    private long expireSeconds;

    private final AntPathMatcher matcher = new AntPathMatcher();

    /** 白名单：登录 / 用户注册 / 商家入驻申请 */
    private static final String[] WHITE_LIST = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/register/merchant"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        for (String white : WHITE_LIST) {
            if (matcher.match(white, path)) {
                return chain.filter(exchange);
            }
        }

        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return deny(exchange, HttpStatus.UNAUTHORIZED, "未登录或登录失效");
        }

        Map<String, Object> claims;
        try {
            claims = new JwtUtil(secret, expireSeconds).parse(authorization.substring(7));
        } catch (Exception e) {
            return deny(exchange, HttpStatus.UNAUTHORIZED, "token 无效或已过期");
        }

        Integer type = (Integer) claims.get("type");
        if (path.startsWith("/api/admin/") && type != MallConstants.TYPE_ADMIN) {
            return deny(exchange, HttpStatus.FORBIDDEN, "无权限访问");
        }

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(MallConstants.HEADER_USER_ID, String.valueOf(claims.get("sub")))
                .header(MallConstants.HEADER_USER_TYPE, String.valueOf(type))
                .header(MallConstants.HEADER_USERNAME, String.valueOf(claims.get("username")))
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> deny(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"code\":" + Result.CODE_UNAUTHORIZED +
                ",\"message\":\"" + message + "\",\"data\":null}").getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
