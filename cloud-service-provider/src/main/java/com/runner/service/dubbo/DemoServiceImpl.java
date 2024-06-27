package com.runner.service.dubbo;

import com.runner.interfaces.DemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0", group = "dev", timeout = 3000)
public class DemoServiceImpl implements DemoService {


    public String sayHello(String context) {
        return "HELLO " + context;
    }
}
