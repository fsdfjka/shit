package com.mall.common;

/** 通用常量 */
public final class MallConstants {

    private MallConstants() {
    }

    /** 网关校验后转发给下游服务的身份请求头（下游无需再解析 JWT） */
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_TYPE = "X-User-Type";
    public static final String HEADER_USERNAME = "X-Username";

    /** 身份类型（JWT claim "type"） */
    public static final int TYPE_USER = 0;
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_MERCHANT = 2;

    /** Redis key（后续里程碑使用） */
    public static final String REDIS_STOCK_KEY = "stock:";           // stock:{skuId} 库存预扣
    public static final String REDIS_ORDER_REQ_KEY = "order:req:";   // order:req:{userId}:{reqId} 下单幂等
}
