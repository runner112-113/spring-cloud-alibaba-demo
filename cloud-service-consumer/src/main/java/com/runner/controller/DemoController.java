package com.runner.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.runner.interfaces.DemoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class DemoController {

    public DemoController() {
        System.out.println();
    }

    //    @DubboReference(version = "1.0.0", group = "dev", timeout = 3000)
    DemoService demoService;

    @GetMapping("/demo")
//    @PreAuthorize("hasAnyRole('USER')")
    @SentinelResource("sayHello")
    public String sayHello() {
//        return demoService.sayHello("context");
        System.out.println("hello ...");
        return null;
    }
}
