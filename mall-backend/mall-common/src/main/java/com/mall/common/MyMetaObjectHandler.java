package com.mall.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * insert/update 自动填充 createTime/updateTime（BaseDO 的两个 fill 字段）。
 * 注意：不使用 @Component —— mall-common 把 mybatis-plus 声明为 provided，
 * 无数据库的 mall-gateway 不扫描本类（否则类加载报 MetaObjectHandler 缺失）；
 * 各数据服务通过 @Import(MyMetaObjectHandler.class) 显式注册。
 */
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
