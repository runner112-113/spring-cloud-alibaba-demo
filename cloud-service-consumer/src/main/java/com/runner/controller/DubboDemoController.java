package com.runner.controller;

import com.runner.interfaces.DubboDemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DubboDemoController {

    @DubboReference(version = "1.0.0", group = "dev", timeout = 3000)
    DubboDemoService dubboDemoService;

    @GetMapping("/dubbo")
    public String sayHello() {
        return dubboDemoService.sayHello("context");
    }
}
