package com.runner.service_discovery.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerGracefulShutdownLifecycle;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Instance Registry And DeRegistry Automatically
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/24 16:58
 */
@Component
public class ZookeeperAutoRegistryRunner implements ApplicationRunner, SmartLifecycle {

    Logger log = LoggerFactory.getLogger(ZookeeperAutoRegistryRunner.class);

    @Autowired
    ServiceDiscovery<String> serviceDiscovery;


    private final AtomicBoolean running = new AtomicBoolean(false);


    public void run(ApplicationArguments args) throws Exception {
        serviceDiscovery.start();
/*        serviceDiscovery.queryForInstances("aaa", new Watcher(){
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeCreated) {
                    String path = event.getPath();
                }
            }
        });*/

    }

    public void start() {
        if (running.compareAndSet(false,true)) {
            log.info("ZookeeperAutoRegistryRunner start success !!! ");
        }
    }

    /**
     * WebServer start stage will registry {@link WebServerGracefulShutdownLifecycle} help shutdown webserver
     */
    public void stop() {
        try {
            serviceDiscovery.close();
            log.info("ZookeeperAutoRegistryRunner stop success !!! ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
