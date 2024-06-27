package com.runner;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class App {
    @Autowired
    DataSource dataSource;

    public static void main( String[] args ) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(App.class, args);
        DataSource source = applicationContext.getBean(DataSource.class);
        System.out.println(source);
    }
}
