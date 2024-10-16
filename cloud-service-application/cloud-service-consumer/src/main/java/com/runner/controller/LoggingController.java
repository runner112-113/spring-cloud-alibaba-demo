package com.runner.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * about {@link org.springframework.boot.logging.LoggingSystem} some operates
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/16 13:42
 */
@RestController
@RequestMapping("/logging")
public class LoggingController {

    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @Autowired
    LoggingSystem loggingSystem;

    @GetMapping
    public boolean getLogInfos(LogLevel logLevel) {
        // TODO  service discovery foreach?

        logger.debug(" ===> {}", "this is a debug log");

        LoggerConfiguration loggerConfiguration = loggingSystem.getLoggerConfiguration(LoggingController.class.getName());

        logger.info(" ===> logger level is {}", loggerConfiguration.getEffectiveLevel());

        loggingSystem.setLogLevel(LoggingController.class.getName(), logLevel);

        return true;

    }

}
