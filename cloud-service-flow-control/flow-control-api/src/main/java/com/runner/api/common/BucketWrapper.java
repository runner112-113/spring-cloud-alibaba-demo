package com.runner.api.common;

/**
 * 滑动窗口中的一个样本bucket
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/1 13:35
 */
public class BucketWrapper<T> {

    /**
     * bucket的ms长度
     */
    private final long bucketLengthInMs;

    /**
     * 当前bucket的起始时间
     */
    private long bucketStart;

    /**
     * 统计的值
     */
    private T value;

    /**
     * @param bucketLengthInMs bucket的ms长度
     * @param bucketStart      当前bucket的起始时间
     * @param value            统计的值
     */
    public BucketWrapper(long bucketLengthInMs, long bucketStart, T value) {
        this.bucketLengthInMs = bucketLengthInMs;
        this.bucketStart = bucketStart;
        this.value = value;
    }

    public long bucketLength() {
        return bucketLengthInMs;
    }

    public long bucketStart() {
        return bucketStart;
    }

    public T value() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Reset start timestamp of current bucket to provided time.
     *
     * @param startTime valid start timestamp
     * @return bucket after reset
     */
    public BucketWrapper<T> resetTo(long startTime) {
        this.bucketStart = startTime;
        return this;
    }

    /**
     * Check whether given timestamp is in current bucket.
     *
     * @param timeMillis valid timestamp in ms
     * @return true if the given time is in current bucket, otherwise false
     */
    public boolean isTimeInBucket(long timeMillis) {
        return bucketStart <= timeMillis && timeMillis < bucketStart + bucketLengthInMs;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "bucketLengthInMs=" + bucketLengthInMs +
                ", bucketStart=" + bucketStart +
                ", value=" + value +
                '}';
    }
}
