package com.runner.service.dubbo;

import com.runner.interfaces.DemoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;

@DubboService(version = "1.0.0", group = "dev", timeout = 3000,
        methods = {
        @Method(name = "sayHello2",timeout = 4000)
})
public class DemoServiceImpl implements DemoService {


    public String sayHello(String context) {
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
