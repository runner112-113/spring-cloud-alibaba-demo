package com.runner.service_discovery.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceDiscoveryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Zookeeper Config
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/24 17:03
 */
@Configuration(proxyBeanMethods = false)
public class ZookeeperConfig {

    @Bean
    public CuratorFramework curatorFramework(Environment env) {
        return CuratorFrameworkFactory.builder().connectString("zookeeper://").build();
    }


    @Bean
    public ServiceInstance serviceInstance(Environment env) throws Exception {
        return ServiceInstance.builder().serviceType(ServiceType.DYNAMIC)
                .port(Integer.parseInt(env.getProperty("server.port")))
                .enabled(true)
                .payload("just a instance ")
                .name(env.getProperty("spring.application.name"))
                .build();
    }

    @Bean
    public ServiceDiscovery<String> serviceDiscovery(CuratorFramework curatorFramework, ServiceInstance<String> serviceInstance, Environment env) {
        String basePath = env.getProperty("zookeeper.service.discovery.base.path");
        return new ServiceDiscoveryImpl(curatorFramework, basePath, new JsonInstanceSerializer(String.class), serviceInstance, true);
    }
}
