package com.runner.distributed.lock.zookeeper.curator;

import com.runner.distributed.lock.DistributedLock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * {@link InterProcessMutex} A re-entrant mutex that works across JVMs
 *
 * implement by {@link CreateMode.EPHEMERAL_SEQUENTIAL}
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/18 11:10
 */
public class ZkReentrantLock implements DistributedLock {


    private final CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
            .connectString("localhost:2171")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    private static final String ZK_BASE_PATH = "/my-demo";

    private InterProcessMutex mutex;

    public ZkReentrantLock() {
        mutex = new InterProcessMutex(curatorFramework, ZK_BASE_PATH);
    }

    public ZkReentrantLock(CuratorFramework curatorFramework, String path) {
        mutex = new InterProcessMutex(curatorFramework, path);
    }

    @Override
    public void lock() throws Exception {
        mutex.acquire();
    }

    @Override
    public void lock(long holdTime, TimeUnit timeUnit) throws Exception {
        mutex.acquire(holdTime, timeUnit);
    }

    @Override
    public void unlock() throws Exception {
        mutex.release();
    }

    @Override
    public boolean tryLock() {
       throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long holdTime, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }
}
