package com.runner.intercept.api;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/29 19:45
 */
public class MyService {
    public String executeTask(String taskName) {
        System.out.println("execute task ...");
        return "Executing task: " + taskName;
    }
}
