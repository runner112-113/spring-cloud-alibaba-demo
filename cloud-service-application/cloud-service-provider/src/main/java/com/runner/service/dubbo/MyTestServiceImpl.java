package com.runner.service.dubbo;

import com.runner.interfaces.MyTestService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/5 10:59
 */
@DubboService(version = "1.0.0", group = "dev", timeout = 4000)
public class MyTestServiceImpl implements MyTestService {
    @Override
    public void demo(String message) {
        System.out.println(",,,");
    }
}
