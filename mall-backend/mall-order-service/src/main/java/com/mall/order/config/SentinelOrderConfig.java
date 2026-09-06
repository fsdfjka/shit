package com.mall.order.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Sentinel 注解切面 + 静态流控规则（服务侧，大纲任务 12"接口限流/熔断降级基础配置"）。
 * @SentinelResource("orderCreate")：超限走 createBlocked（被限流）、异常走 createFallback（降级）。
 * 接入 Sentinel Dashboard（默认 8858）后可在控制台热更新接口规则。
 */
@Configuration
public class SentinelOrderConfig {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void initRules() {
        FlowRule rule = new FlowRule("orderCreate");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(20);
        FlowRuleManager.loadRules(List.of(rule));
    }
}
