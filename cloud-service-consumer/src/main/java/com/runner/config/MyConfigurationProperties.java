package com.runner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/5 17:32
 */
@ConfigurationProperties(prefix = "my.demo")
@Configuration
public class MyConfigurationProperties {

    private List<String> addresses = new ArrayList<>(Arrays.asList("a", "b"));

    private String name;

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
