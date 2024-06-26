package com.runner.service;

import com.runner.interfaces.DubboDemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0", group = "dev", timeout = 3000)
public class DubboDemoServiceImpl implements DubboDemoService {


    public String sayHello(String context) {
        return "HELLO " + context;
    }
}
