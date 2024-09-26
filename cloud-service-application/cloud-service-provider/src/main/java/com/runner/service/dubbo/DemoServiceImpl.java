package com.runner.service.dubbo;

import brave.Span;
import brave.Tracing;
import com.runner.interfaces.DemoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.cloud.sleuth.brave.bridge.BraveCurrentTraceContext;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

@DubboService(version = "1.0.0", group = "dev", timeout = 3000,
        methods = {
        @Method(name = "sayHello2",timeout = 4000)
})
public class DemoServiceImpl implements DemoService {


    @Resource
    BraveCurrentTraceContext braveCurrentTraceContext;

    @Resource
    Tracing tracing;

    public String sayHello(String context) {

        Span span = null;
        try {
            span = tracing.tracer().nextSpan().name("provider")
                    .tag("side","provider")
                    .tag("test-tag","test").start();

            Span finalSpan = span;
            Runnable runnableWrapper = braveCurrentTraceContext.wrap(() -> {
            try {
                finalSpan.annotate("before start execute");
                System.out.println(Thread.currentThread().getName() + " sleep start...");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "sleep over...");
                finalSpan.annotate("before end execute");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

            ForkJoinPool.commonPool().submit(runnableWrapper).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            span.finish();
        }
        return "HELLO " + context;
    }

    @Override
    public String sayHello2(String context) {
        return null;
    }

    @Override
    public String sayHello3(String context) {
        return null;
    }

    @Override
    public String sayHello4(String context) {
        return null;
    }
}
