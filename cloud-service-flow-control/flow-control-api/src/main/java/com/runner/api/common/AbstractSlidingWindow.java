package com.runner.api.common;

import com.runner.misc.utils.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于数组的滑动窗口
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/1 13:41
 */
public abstract class AbstractSlidingWindow<T> {
    /**
     * 每个bucket的时间窗口大小
     */
    protected int bucketLengthInMs;
    /**
     * sampleCount = intervalInMs / windowLengthInMs
     * 总bucket数
     */
    protected int sampleCount;
    /**
     * 整体窗口的时间长度  ms
     */
    protected int windowLengthInMs;
    /**
     * 整体窗口的时间长度  s
     */
    private double windowLengthInSecond;

    /**
     * bucket数组
     */
    protected final AtomicReferenceArray<BucketWrapper<T>> array;

    /**
     * The conditional (predicate) update lock is used only when current bucket is deprecated.
     */
    private final ReentrantLock updateLock = new ReentrantLock();

    /**
     * The total bucket count is: {@code sampleCount = windowLengthInMs / windowLengthInMs}.
     *
     * @param sampleCount  样本数
     * @param windowLengthInMs 整个窗口的时间长度跨度
     */
    public AbstractSlidingWindow(int sampleCount, int windowLengthInMs) {
        AssertUtil.isTrue(sampleCount > 0, "bucket count is invalid: " + sampleCount);
        AssertUtil.isTrue(windowLengthInMs > 0, "total time interval of the sliding window should be positive");
        AssertUtil.isTrue(windowLengthInMs % sampleCount == 0, "time span needs to be evenly divided");

        this.bucketLengthInMs = windowLengthInMs / sampleCount;
        this.windowLengthInMs = windowLengthInMs;
        this.windowLengthInSecond = windowLengthInMs / 1000.0;
        this.sampleCount = sampleCount;

        this.array = new AtomicReferenceArray<>(sampleCount);
    }

    /**
     * Get the bucket at current timestamp.
     *
     * @return the bucket at current timestamp
     */
    public BucketWrapper<T> currentBucket() {
        return currentBucket(TimeUtil.currentTimeMillis());
    }

    /**
     * Create a new statistic value for bucket.
     *
     * @param timeMillis current time in milliseconds
     * @return the new empty bucket
     */
    public abstract T newEmptyBucket(long timeMillis);

    /**
     * Reset given bucket to provided start time and reset the value.
     *
     * @param startTime  the start time of the bucket in milliseconds
     * @param bucketWrapper current bucket
     * @return new clean bucket at given start time
     */
    protected abstract BucketWrapper<T> resetBucketTo(BucketWrapper<T> bucketWrapper, long startTime);

    /**
     * 计算当前给定的时间应该所处的数组索引
     * @param timeMillis   记录的时间
     * @return
     */
    private int calculateTimeIdx(/*@Valid*/ long timeMillis) {
        long timeId = timeMillis / bucketLengthInMs;
        // Calculate current index so we can map the timestamp to the leap array.
        return (int)(timeId % array.length());
    }

    /**
     * 计算指定时间点所在的bucket的起始时间
     * @param timeMillis    指定时间点
     * @return
     */
    protected long calculateBucketStart(/*@Valid*/ long timeMillis) {
        return timeMillis - timeMillis % bucketLengthInMs;
    }

    /**
     * 根据提供的时间戳找到它应该放置的bucket
     *
     * @param timeMillis a valid timestamp in milliseconds
     * @return current bucket item at provided timestamp if the time is valid; null if time is invalid
     */
    public BucketWrapper<T> currentBucket(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }

        // 计算当前时间在array中的下标（循环数组） bucket索引
        int idx = calculateTimeIdx(timeMillis);
        // Calculate current bucket start time.
        // 计算bucket的起始时间
        long bucketStart = calculateBucketStart(timeMillis);

        /*
         * Get bucket item at given time from the array.
         *
         * (1) Bucket is absent, then just create a new bucket and CAS update to circular array.
         * (2) Bucket is up-to-date, then just return the bucket.
         * (3) Bucket is deprecated, then reset current bucket.
         */
        while (true) {
            BucketWrapper<T> old = array.get(idx);
            // bucket索引的位置为空，则新建一个bucket，并通过cas进行插入
            if (old == null) {
                /*
                 *     B0       B1      B2    NULL      B4
                 * ||_______|_______|_______|_______|_______||___
                 * 200     400     600     800     1000    1200  timestamp
                 *                             ^
                 *                          time=888
                 *            bucket is empty, so create new and update
                 *
                 * If the old bucket is absent, then we create a new bucket at {@code windowStart},
                 * then try to update circular array via a CAS operation. Only one thread can
                 * succeed to update, while other threads yield its time slice.
                 */
                BucketWrapper<T> bucketWrapper = new BucketWrapper<T>(bucketLengthInMs, bucketStart, newEmptyBucket(timeMillis));
                if (array.compareAndSet(idx, null, bucketWrapper)) {
                    // Successfully updated, return the created bucket.
                    return bucketWrapper;
                } else {
                    // Contention failed, the thread will yield its time slice to wait for bucket available.
                    Thread.yield();
                }

                // 当前就是最新的，直接返回之前的bucket
            } else if (bucketStart == old.bucketStart()) {
                /*
                 *     B0       B1      B2     B3      B4
                 * ||_______|_______|_______|_______|_______||___
                 * 200     400     600     800     1000    1200  timestamp
                 *                             ^
                 *                          time=888
                 *            startTime of Bucket 3: 800, so it's up-to-date
                 *
                 * If current {@code windowStart} is equal to the start timestamp of old bucket,
                 * that means the time is within the bucket, so directly return the bucket.
                 */
                return old;

                // 有旧址，即存在过期的bucket,需要reset
            } else if (bucketStart > old.bucketStart()) {
                /*
                 *   (old)
                 *             B0       B1      B2    NULL      B4
                 * |_______||_______|_______|_______|_______|_______||___
                 * ...    1200     1400    1600    1800    2000    2200  timestamp
                 *                              ^
                 *                           time=1676
                 *          startTime of Bucket 2: 400, deprecated, should be reset
                 *
                 * If the start timestamp of old bucket is behind provided time, that means
                 * the bucket is deprecated. We have to reset the bucket to current {@code windowStart}.
                 * Note that the reset and clean-up operations are hard to be atomic,
                 * so we need a update lock to guarantee the correctness of bucket update.
                 *
                 * The update lock is conditional (tiny scope) and will take effect only when
                 * bucket is deprecated, so in most cases it won't lead to performance loss.
                 */
                if (updateLock.tryLock()) {
                    try {
                        // Successfully get the update lock, now we reset the bucket.
                        return resetBucketTo(old, bucketStart);
                    } finally {
                        updateLock.unlock();
                    }
                } else {
                    // Contention failed, the thread will yield its time slice to wait for bucket available.
                    Thread.yield();
                }
            } else if (bucketStart < old.bucketStart()) {
                // Should not go through here, as the provided time is already behind.
                return new BucketWrapper<>(bucketLengthInMs, bucketStart, newEmptyBucket(timeMillis));
            }
        }
    }

    /**
     * Get the previous bucket item before provided timestamp.
     *
     * @param timeMillis a valid timestamp in milliseconds
     * @return the previous bucket item before provided timestamp
     */
    public BucketWrapper<T> getPreviousBucket(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        int idx = calculateTimeIdx(timeMillis - bucketLengthInMs);
        timeMillis = timeMillis - bucketLengthInMs;
        BucketWrapper<T> bucketWrapper = array.get(idx);

        if (bucketWrapper == null || isBucketDeprecated(bucketWrapper)) {
            return null;
        }

        if (bucketWrapper.bucketStart() + bucketLengthInMs < (timeMillis)) {
            return null;
        }

        return bucketWrapper;
    }

    /**
     * Get the previous bucket item for current timestamp.
     *
     * @return the previous bucket item for current timestamp
     */
    public BucketWrapper<T> getPreviousBucket() {
        return getPreviousBucket(TimeUtil.currentTimeMillis());
    }

    /**
     * Get statistic value from bucket for provided timestamp.
     *
     * @param timeMillis a valid timestamp in milliseconds
     * @return the statistic value if bucket for provided timestamp is up-to-date; otherwise null
     */
    public T getBucketValue(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        int idx = calculateTimeIdx(timeMillis);

        BucketWrapper<T> bucketWrapper = array.get(idx);

        if (bucketWrapper == null || !bucketWrapper.isTimeInBucket(timeMillis)) {
            return null;
        }

        return bucketWrapper.value();
    }

    /**
     * Check if a bucket is deprecated, which means that the bucket
     * has been behind for at least an entire window time span.
     *
     * @param bucketWrapper a non-null bucket
     * @return true if the bucket is deprecated; otherwise false
     */
    public boolean isBucketDeprecated(/*@NonNull*/ BucketWrapper<T> bucketWrapper) {
        return isBucketDeprecated(TimeUtil.currentTimeMillis(), bucketWrapper);
    }

    /**
     * 判断bucket是否过期
     * @param time
     * @param bucketWrapper
     * @return
     */
    public boolean isBucketDeprecated(long time, BucketWrapper<T> bucketWrapper) {
        return time - bucketWrapper.bucketStart() > windowLengthInMs;
    }

    /**
     * Get valid bucket list for entire sliding window.
     * The list will only contain "valid" buckets.
     *
     * @return valid bucket list for entire sliding window.
     */
    public List<BucketWrapper<T>> list() {
        return list(TimeUtil.currentTimeMillis());
    }

    public List<BucketWrapper<T>> list(long validTime) {
        int size = array.length();
        List<BucketWrapper<T>> result = new ArrayList<BucketWrapper<T>>(size);

        for (int i = 0; i < size; i++) {
            BucketWrapper<T> bucketWrapper = array.get(i);
            if (bucketWrapper == null || isBucketDeprecated(validTime, bucketWrapper)) {
                continue;
            }
            result.add(bucketWrapper);
        }

        return result;
    }

    /**
     * Get all buckets for entire sliding window including deprecated buckets.
     *
     * @return all buckets for entire sliding window
     */
    public List<BucketWrapper<T>> listAll() {
        int size = array.length();
        List<BucketWrapper<T>> result = new ArrayList<BucketWrapper<T>>(size);

        for (int i = 0; i < size; i++) {
            BucketWrapper<T> bucketWrapper = array.get(i);
            if (bucketWrapper == null) {
                continue;
            }
            result.add(bucketWrapper);
        }

        return result;
    }

    /**
     * Get aggregated value list for entire sliding window.
     * The list will only contain value from "valid" buckets.
     *
     * @return aggregated value list for entire sliding window
     */
    public List<T> values() {
        return values(TimeUtil.currentTimeMillis());
    }

    public List<T> values(long timeMillis) {
        if (timeMillis < 0) {
            return new ArrayList<T>();
        }
        int size = array.length();
        List<T> result = new ArrayList<T>(size);

        for (int i = 0; i < size; i++) {
            BucketWrapper<T> bucketWrapper = array.get(i);
            if (bucketWrapper == null || isBucketDeprecated(timeMillis, bucketWrapper)) {
                continue;
            }
            result.add(bucketWrapper.value());
        }
        return result;
    }

    /**
     * Get the valid "head" bucket of the sliding window for provided timestamp.
     * Package-private for test.
     *
     * @param timeMillis a valid timestamp in milliseconds
     * @return the "head" bucket if it exists and is valid; otherwise null
     */
    BucketWrapper<T> getValidHead(long timeMillis) {
        // Calculate index for expected head time.
        int idx = calculateTimeIdx(timeMillis + bucketLengthInMs);

        BucketWrapper<T> bucketWrapper = array.get(idx);
        if (bucketWrapper == null || isBucketDeprecated(bucketWrapper)) {
            return null;
        }

        return bucketWrapper;
    }

    /**
     * Get the valid "head" bucket of the sliding window at current timestamp.
     *
     * @return the "head" bucket if it exists and is valid; otherwise null
     */
    public BucketWrapper<T> getValidHead() {
        return getValidHead(TimeUtil.currentTimeMillis());
    }

    /**
     * Get sample count (total amount of buckets).
     *
     * @return sample count
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Get total interval length of the sliding window in milliseconds.
     *
     * @return interval in second
     */
    public int getWindowLengthInMs() {
        return windowLengthInMs;
    }

    /**
     * Get total interval length of the sliding window.
     *
     * @return interval in second
     */
    public double getWindowLengthInSecond() {
        return windowLengthInSecond;
    }

    public void debug(long time) {
        StringBuilder sb = new StringBuilder();
        List<BucketWrapper<T>> lists = list(time);
        sb.append("Thread_").append(Thread.currentThread().getId()).append("_");
        for (BucketWrapper<T> bucketWrapper : lists) {
            sb.append(bucketWrapper.bucketStart()).append(":").append(bucketWrapper.value().toString());
        }
        System.out.println(sb.toString());
    }

    public long currentWaiting() {
        // TODO: default method. Should remove this later.
        return 0;
    }

    public void addWaiting(long time, int acquireCount) {
        // Do nothing by default.
        throw new UnsupportedOperationException();
    }
}
