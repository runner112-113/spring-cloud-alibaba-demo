package com.runner;

import com.runner.interfaces.DubboDemoService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDubbo
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
