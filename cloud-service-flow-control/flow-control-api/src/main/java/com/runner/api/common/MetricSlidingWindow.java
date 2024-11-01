package com.runner.api.common;

/**
 * Metric SlidingWindow
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/1 17:37
 */
public class MetricSlidingWindow extends AbstractSlidingWindow<MetricBucket> {

    public MetricSlidingWindow(int sampleCount, int windowLengthInMs) {
        super(sampleCount, windowLengthInMs);
    }

    @Override
    public MetricBucket newEmptyBucket(long time) {
        return new MetricBucket();
    }

    @Override
    protected BucketWrapper<MetricBucket> resetBucketTo(BucketWrapper<MetricBucket> w, long startTime) {
        // Update the start time and reset value.
        // 更新bucket的起始时间
        w.resetTo(startTime);
        // 重置统计数据
        w.value().reset();
        return w;
    }
}
