package com.runner.controller.dubbo;

import brave.Span;
import brave.Tracing;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.runner.interfaces.DemoService;
import com.runner.interfaces.MyAgentIntercept;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.brave.bridge.BraveCurrentTraceContext;
import org.springframework.scheduling.concurrent.ForkJoinPoolFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ForkJoinPool;

@RestController
@MyAgentIntercept
public class DubboDemoController {

    public DubboDemoController() {
        System.out.println();
    }

    @DubboReference(version = "1.0.0", group = "dev", timeout = 3000)
    DemoService demoService;


    @Resource
    Tracing tracing;


    @Resource
    BraveCurrentTraceContext braveCurrentTraceContext;

    @GetMapping("/dubbo")
    public String helloDubbo() {

        TraceContextOrSamplingFlags parentContext = TraceContextOrSamplingFlags.create(tracing.currentTraceContext().get());
        Runnable runnableWrapper = braveCurrentTraceContext.wrap(() -> {
            Span span = tracing.tracer().nextSpan(parentContext).name("consumer-new-span").annotate("consumer start execute").start();
            try {
                System.out.println("sleep start...");
                Thread.sleep(3000);
                System.out.println("sleep over...");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            span.annotate("consumer end execute").finish();
        });

        ForkJoinPool.commonPool().execute(runnableWrapper);

        System.out.println("hello dubbo");
        return demoService.sayHello("context");
    }


    @GetMapping("/say-hello")
    public String sayHello() {
//        return demoService.sayHello("context");
        System.out.println("hello dubbo");
        return "hello dubbo";
    }
}
