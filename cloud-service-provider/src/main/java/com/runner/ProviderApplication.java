package com.runner;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, ShardingSphereAutoConfiguration.class})
@EnableDubbo
//@EnableDiscoveryClient
public class ProviderApplication {
/*    @Autowired
    DataSource dataSource;*/

    public static void main( String[] args ) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ProviderApplication.class, args);
      /*  DataSource source = applicationContext.getBean(DataSource.class);
        System.out.println(source);*/
    }
}
