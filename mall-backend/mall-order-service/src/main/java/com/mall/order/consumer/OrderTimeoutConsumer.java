package com.mall.order.consumer;

import com.mall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 订单超时取消：消费下单时投递的延迟消息（延迟等级见 mall.order.timeout-delay-level）。
 * 幂等：消费端先查单，非"待支付"直接跳过（已支付/已取消不重放）；条件更新冲突走消息重试。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = OrderService.TIMEOUT_TOPIC, consumerGroup = "mall-order-timeout-consumer")
public class OrderTimeoutConsumer implements RocketMQListener<String> {

    private final OrderService orderService;

    @Override
    public void onMessage(String orderNo) {
        orderService.cancel(orderNo, null);
        log.info("订单超时取消处理完成: {}", orderNo);
    }
}
