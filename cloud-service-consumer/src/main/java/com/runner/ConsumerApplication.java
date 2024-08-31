package com.runner;

import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;
import com.runner.config.MyAnnotationConfigWebApplicationContext;
import com.runner.controller.dubbo.DubboDemoController;
import com.runner.entity.MyBean;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.weaving.AspectJWeavingEnabler;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude = NacosServiceRegistryAutoConfiguration.class)
//@EnableDubbo
//@EnableDiscoveryClient
//@EnableLoadTimeWeaving
@EnableWebSecurity
@EnableMethodSecurity
//@ComponentScan(basePackageClasses = ConsumerApplication.class)
public class ConsumerApplication {



/*    @Bean
    public AspectJWeavingEnabler enabler() {
        return new AspectJWeavingEnabler();
    }*/
    public static void main( String[] args ) {

/*        SpringApplication springApplication = new SpringApplication();
        springApplication.*/

        ConfigurableApplicationContext context = SpringApplication.run(ConsumerApplication.class, args);
        ObjectPostProcessor objectPostProcessor = context.getBean(ObjectPostProcessor.class);

        MyBean myBean = new MyBean();
        objectPostProcessor.postProcess(myBean);

        System.out.println(myBean.getAge());





//        AnnotationConfigWebApplicationContext context = new MyAnnotationConfigWebApplicationContext();
//        context.addBeanFactoryPostProcessor(aspectJWeavingEnabler);
//        context.register(ConsumerApplication.class);
//        ConfigurableApplicationContext context = SpringApplication.run(new Class[]{AspectJWeavingEnabler.class,ConsumerApplication.class }, args);
//        context.refresh();
        DubboDemoController controller = context.getBean(DubboDemoController.class);
//        DubboDemoController controller = new DubboDemoController();
        controller.sayHello();


        DubboDemoController dubboDemoController = new DubboDemoController();
        dubboDemoController.sayHello();
    }
}
