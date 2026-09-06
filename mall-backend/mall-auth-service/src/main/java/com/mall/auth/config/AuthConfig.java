package com.mall.auth.config;

import com.mall.common.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthConfig {

    @Bean
    public JwtUtil jwtUtil(@Value("${mall.jwt.secret}") String secret,
                           @Value("${mall.jwt.expire-seconds}") long expireSeconds) {
        return new JwtUtil(secret, expireSeconds);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
