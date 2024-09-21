package com.runner.dubbo.simple;

import com.runner.interfaces.DemoService;
import com.runner.service.dubbo.DemoServiceImpl;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DubboSimpleDemo {

    @Test
    public void provider() {
        // 定义所有的服务
        ServiceConfig<DemoService> service = new ServiceConfig<>();
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());

        // 启动 Dubbo
        DubboBootstrap.getInstance()
                .application("simple-dubbo-provider")
                .registry(new RegistryConfig("nacos://124.222.122.96:8848"))
                .protocol(new ProtocolConfig("dubbo", -1))
                .service(service)
                .start()
                .await();


    }


    @Test
    public void consumer() {
        // 定义所有的订阅
        ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
        reference.setInterface(DemoService.class);

        // 启动 Dubbo
        DubboBootstrap.getInstance()
                .application("first-dubbo-consumer")
                .registry(new RegistryConfig("nacos://124.222.122.96:8848"))
                .reference(reference)
                .start();

        // 获取订阅到的 Stub
        DemoService service = reference.get();
        // 像普通的 java 接口一样调用
        String message = service.sayHello("dubbo");

        System.out.println(message);
    }
}
