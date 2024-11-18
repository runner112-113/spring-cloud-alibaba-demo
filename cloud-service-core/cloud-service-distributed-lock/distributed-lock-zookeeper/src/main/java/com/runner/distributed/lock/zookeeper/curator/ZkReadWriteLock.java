package com.runner.distributed.lock.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/18 11:23
 */
public class ZkReadWriteLock {

    private final CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
            .connectString("localhost:2171").build();

    private static final String ZK_BASE_PATH = "/my-demo";


    private InterProcessReadWriteLock readWriteLock;

    public ZkReadWriteLock() {
        readWriteLock = new InterProcessReadWriteLock(curatorFramework, ZK_BASE_PATH);

    }


}
