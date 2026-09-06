package com.mall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mall.common.MyMetaObjectHandler;
import org.springframework.context.annotation.Import;

/**
 * 用户域服务：用户资料、收货地址；平台管理员账号管理（user.role=1）。
 */
@SpringBootApplication(scanBasePackages = "com.mall")
@MapperScan("com.mall.user.mapper")
@Import(MyMetaObjectHandler.class)
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
