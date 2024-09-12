package com.runner.aop;

import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Duration;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/11 17:45
 */
@Component
@Aspect
public class MeterAspect {

    @Autowired
    private MeterRegistry meterRegistry;

    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    @Pointcut("@annotation(com.runner.annotations.MeterMonitor)")
    private void pointcut() {

    }

    @Around("pointcut()")
    public Object doExecute(ProceedingJoinPoint pjp) throws Throwable {

        FunctionCounter
                .builder("cpu_processors", osBean, OperatingSystemMXBean::getAvailableProcessors)
                .baseUnit("beans") // optional
                .description("available processors count of current system") // optional
                .tags("mbean", "osbean") // optional
                .register(meterRegistry);

        Gauge.builder("system_avg_load", osBean, OperatingSystemMXBean::getSystemLoadAverage)
                .baseUnit("beans") // optional
                .description("the system avg load of current system") // optional
                .tags("mbean", "osbean") // optional
                .register(meterRegistry);


        Timer timer = Timer.builder("http_requests")
                .tag("method_name", pjp.getSignature().getName())
                .description("HTTP请求的响应时间")
                // 启用百分位数统计
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                // 启用基于直方图的百分位数
                .publishPercentileHistogram(true)
                // 配置 SLA 目标 Service Level Agreement (SLA) 桶
                .sla(Duration.ofMillis(100), Duration.ofMillis(300), Duration.ofMillis(500), Duration.ofSeconds(1))
                .register(meterRegistry);


        return timer.record(() -> {
            try {
                return pjp.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
