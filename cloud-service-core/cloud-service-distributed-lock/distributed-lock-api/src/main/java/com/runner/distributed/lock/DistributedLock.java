package com.runner.distributed.lock;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * distributed lock api
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/28 16:04
 */
public interface DistributedLock {

    void lock() throws SQLException;

    void lock(long holdTime, TimeUnit timeUnit);

    void unlock();

    boolean tryLock();

    boolean tryLock(long holdTime, TimeUnit timeUnit);
}
