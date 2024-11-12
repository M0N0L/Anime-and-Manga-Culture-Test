package org.example.backend.sentinel;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

@Component
public class SentinelRulesManager {
    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
    }

    //限流规则
    public void initFlowRules() {
        ParamFlowRule rule = new ParamFlowRule("listQuestionVOByPageSentinel")
                .setParamIdx(0) //对第0个参数限流
                .setCount(50) // 一分钟不超过50次
                .setDurationInSec(10);
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }

    // 降级规则
    public void initDegradeRules() {
        //添加熔断规则
        DegradeRule slowCallRule = new DegradeRule("listQuestionVOByPageSentinel")
                .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                .setCount(0.2)
                .setTimeWindow(20)
                .setStatIntervalMs(30 * 1000)
                .setMinRequestAmount(10)
                .setSlowRatioThreshold(3);
        DegradeRule errorRateRule = new DegradeRule("listQuestionVOByPageSentinel")
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(0.1)
                .setTimeWindow((60))
                .setStatIntervalMs(30 * 1000)
                .setMinRequestAmount(10);
        DegradeRuleManager.loadRules(Arrays.asList(slowCallRule, errorRateRule));
    }
}
