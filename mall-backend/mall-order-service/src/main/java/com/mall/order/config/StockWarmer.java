package com.mall.order.config;

import com.mall.common.MallConstants;
import com.mall.order.entity.SkuOrder;
import com.mall.order.mapper.SkuOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存 Redis 预热（database-design §4 库存链路）：
 * 启动时全量同步 sku.stock -> stock:{skuId}；商品上架/改库存后由商品变更逻辑同步（演示路径：
 * 重启即校准，配合 Lua 预扣的"-1 未预热"兜底提示）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockWarmer implements ApplicationRunner {

    private final SkuOrderMapper skuOrderMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<SkuOrder> all = skuOrderMapper.selectAllActive();
        for (SkuOrder sku : all) {
            redisTemplate.opsForValue().set(MallConstants.REDIS_STOCK_KEY + sku.getId(), String.valueOf(sku.getStock()));
        }
        log.info("[库存预热] 同步 {} 个 SKU 至 Redis", all.size());
    }
}
