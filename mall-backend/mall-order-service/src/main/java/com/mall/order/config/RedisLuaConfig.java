package com.mall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * 库存 Redis Lua 脚本（Redis 预扣热点挡板，DB 为最终基准——见数据库设计 §4 库存链路）。
 * key 约定：stock:{skuId}（与 MallConstants.REDIS_STOCK_KEY 一致）。
 * 预扣返回：剩余数（>=0 成功）；-1 未预热（热key缺失，直接判失败并提示稍后重试）；-2 库存不足。
 */
@Configuration
public class RedisLuaConfig {

    @Bean
    public DefaultRedisScript<Long> stockDeductScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(
                "local cur = redis.call('GET', KEYS[1]) " +
                        "if not cur then return -1 end " +
                        "local left = tonumber(cur) - tonumber(ARGV[1]) " +
                        "if left < 0 then return -2 end " +
                        "redis.call('SET', KEYS[1], left) " +
                        "return left");
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> stockRefundScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(
                "local cur = redis.call('GET', KEYS[1]) " +
                        "if cur then redis.call('SET', KEYS[1], tonumber(cur) + tonumber(ARGV[1])) end " +
                        "return 1");
        script.setResultType(Long.class);
        return script;
    }
}
