package com.mall.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码声明路由：/api/portal/shops/** → mall-product-service（店铺信息，前台游客可访问）。
 *
 * 说明：网关其余路由写在 application.yml；该 yaml 含本地环境配置（Nacos 地址等）不入库，
 * 为避免新增路由随本地 yaml 一起丢失，店铺路由以 Java 方式声明，确保各环境一致生效。
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator shopRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("mall-product-shop", r -> r.path("/api/portal/shops/**")
                        .uri("lb://mall-product-service"))
                .build();
    }
}
