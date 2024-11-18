package com.runner.distributed.lock;

import java.util.concurrent.TimeUnit;

/**
 * distributed lock api
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/28 16:04
 */
public interface DistributedLock {

    void lock() throws Exception;

    void lock(long holdTime, TimeUnit timeUnit) throws Exception;

    void unlock() throws Exception;

    boolean tryLock();

    boolean tryLock(long holdTime, TimeUnit timeUnit);
}
