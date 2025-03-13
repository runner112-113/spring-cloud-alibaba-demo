package com.runner.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO
 *
 * 1. Local Documents --> Embedding Model --> Vector --> Vector Store
 *
 * 2. Query --> Embedding Model --> Vector --> find top-k in Vector Store --> LLM
 *
 * @author Runner
 * @version 1.0
 * @since 2025/3/3 15:13
 */
@SpringBootApplication
public class AIApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
    }
}
