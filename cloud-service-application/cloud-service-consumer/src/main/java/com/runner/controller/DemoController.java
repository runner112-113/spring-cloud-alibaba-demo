package com.runner.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.runner.annotations.MeterMonitor;
import com.runner.interfaces.DemoService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Component
public class DemoController {

    Logger logger = LoggerFactory.getLogger(DemoController.class);

    public DemoController() {
        System.out.println();
    }

    //    @DubboReference(version = "1.0.0", group = "dev", timeout = 3000)
    DemoService demoService;


    @Resource
    RedisTemplate redisTemplate;

    @Resource(name = "redisTemplate")
    HashOperations<String, byte[], byte[]> hashOperations;

    @Resource
    MeterRegistry meterRegistry;

    @GetMapping("/demo")
//    @PreAuthorize("hasAnyRole('USER')")
    @MeterMonitor
    @SentinelResource("sayHello")
    public String sayHello() {
        Counter.builder("demo-count").tag("tag--sayHello","test").register(meterRegistry).increment();
//        return demoService.sayHello("context");

        redisTemplate.opsForValue().set("test", 1);
        System.out.println("hello ...");
        return null;
    }
}
