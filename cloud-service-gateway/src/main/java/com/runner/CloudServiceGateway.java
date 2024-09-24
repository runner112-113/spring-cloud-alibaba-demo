package com.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * gateway service
 * TODO：gateway layer use dubbo transport instead of http
 *
 */
@SpringBootApplication
public class CloudServiceGateway {
    public static void main(String[] args) {
        SpringApplication.run(CloudServiceGateway.class, args);
    }
}
