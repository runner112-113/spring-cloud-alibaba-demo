package com.runner.flowcontrol.resilience4j;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/18 16:26
 */
public class Demo {
    public static void main(String[] args) {

        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .retryExceptions(Exception.class)
                .intervalFunction(IntervalFunction.ofRandomized())
                .build();

        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);

        Retry retry = retryRegistry.retry("my-retry-demo");

        AtomicInteger i = new AtomicInteger();
        retry.executeRunnable(() -> {
            System.out.println("execute time is " + i.getAndIncrement());
            int m = 1/0;
        });
    }
}
