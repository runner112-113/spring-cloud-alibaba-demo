package com.runner.controller.dubbo;

import com.runner.interfaces.DemoService;
import com.runner.interfaces.MyAgentIntercept;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@MyAgentIntercept
public class DubboDemoController {

    public DubboDemoController() {
        System.out.println();
    }

    //    @DubboReference(version = "1.0.0", group = "dev", timeout = 3000)
    DemoService demoService;

    @GetMapping("/dubbo")
    public String sayHello() {
//        return demoService.sayHello("context");
        System.out.println("hello dubbo");
        return "hello dubbo";
    }
}
