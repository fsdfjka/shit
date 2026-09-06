package com.mall.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具。claim 约定：sub=用户 id，type={0 用户/1 平台管理员/2 商家}，username。
 * 用法：mall-auth-service 签发；mall-gateway 校验；下游服务读取网关转发的 X-User-* 头。
 * secret 与过期时间由各服务 yml 的 mall.jwt.* 配置（gateway 与 auth 必须一致）。
 */
public class JwtUtil {

    private final String secret;
    private final long expireSeconds;

    public JwtUtil(String secret, long expireSeconds) {
        this.secret = secret;
        this.expireSeconds = expireSeconds;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String create(Long id, Integer type, String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("type", type)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireSeconds * 1000))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** 解析 token 全部 claim；失效/非法抛异常 */
    public Map<String, Object> parse(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody();
        return claims;
    }
}
