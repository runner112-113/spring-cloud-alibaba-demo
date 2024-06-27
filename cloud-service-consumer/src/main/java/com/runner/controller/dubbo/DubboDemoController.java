package com.runner.controller.dubbo;

import com.runner.interfaces.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DubboDemoController {

    @DubboReference(version = "1.0.0", group = "dev", timeout = 3000)
    DemoService demoService;

    @GetMapping("/dubbo")
    public String sayHello() {
        return demoService.sayHello("context");
    }
}
