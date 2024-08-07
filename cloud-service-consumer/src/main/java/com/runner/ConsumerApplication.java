package com.runner;

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
//@EnableDiscoveryClient
public class ConsumerApplication {
    public static void main( String[] args ) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
