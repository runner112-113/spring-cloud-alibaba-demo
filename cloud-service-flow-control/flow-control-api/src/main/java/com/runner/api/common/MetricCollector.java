package com.runner.api.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/4 9:05
 */
public class MetricCollector implements  Metric {

    private final AbstractSlidingWindow<MetricBucket> data;

    public MetricCollector(int sampleCount, int intervalInMs) {
        this.data = new MetricSlidingWindow(sampleCount, intervalInMs);
    }

/*    public MetricCollector(int sampleCount, int intervalInMs, boolean enableOccupy) {
        if (enableOccupy) {
            this.data = new OccupiableBucketLeapArray(sampleCount, intervalInMs);
        } else {
            this.data = new BucketLeapArray(sampleCount, intervalInMs);
        }
    }*/

    /**
     * For unit test.
     */
    public MetricCollector(AbstractSlidingWindow<MetricBucket> array) {
        this.data = array;
    }

    @Override
    public long success() {
        data.currentBucket();
        long success = 0;

        List<MetricBucket> list = data.values();
        for (MetricBucket bucket : list) {
            success += bucket.success();
        }
        return success;
    }

    @Override
    public long maxSuccess() {
        data.currentBucket();
        long success = 0;

        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            if (window.success() > success) {
                success = window.success();
            }
        }
        return Math.max(success, 1);
    }

    @Override
    public long exception() {
        data.currentBucket();
        long exception = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            exception += window.exception();
        }
        return exception;
    }

    @Override
    public long block() {
        data.currentBucket();
        long block = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            block += window.block();
        }
        return block;
    }

    @Override
    public long pass() {
        data.currentBucket();
        long pass = 0;
        List<MetricBucket> list = data.values();

        for (MetricBucket window : list) {
            pass += window.pass();
        }
        return pass;
    }

    @Override
    public long occupiedPass() {
        data.currentBucket();
        long pass = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            pass += window.occupiedPass();
        }
        return pass;
    }

    @Override
    public long rt() {
        data.currentBucket();
        long rt = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            rt += window.rt();
        }
        return rt;
    }

    @Override
    public long minRt() {
        data.currentBucket();
//        long rt = SentinelConfig.statisticMaxRt();
        long rt = 5000;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            if (window.minRt() < rt) {
                rt = window.minRt();
            }
        }

        return Math.max(1, rt);
    }

    @Override
    public List<MetricNode> details() {
        List<MetricNode> details = new ArrayList<>();
        data.currentBucket();
        List<BucketWrapper<MetricBucket>> list = data.list();
        for (BucketWrapper<MetricBucket> window : list) {
            if (window == null) {
                continue;
            }

            details.add(fromBucket(window));
        }

        return details;
    }

    @Override
    public List<MetricNode> detailsOnCondition(Predicate<Long> timePredicate) {
        List<MetricNode> details = new ArrayList<>();
        data.currentBucket();
        List<BucketWrapper<MetricBucket>> list = data.list();
        for (BucketWrapper<MetricBucket> bucket : list) {
            if (bucket == null) {
                continue;
            }
            if (timePredicate != null && !timePredicate.test(bucket.bucketStart())) {
                continue;
            }

            details.add(fromBucket(bucket));
        }

        return details;
    }

    private MetricNode fromBucket(BucketWrapper<MetricBucket> wrap) {
        MetricNode node = new MetricNode();
        node.setBlockQps(wrap.value().block());
        node.setExceptionQps(wrap.value().exception());
        node.setPassQps(wrap.value().pass());
        long successQps = wrap.value().success();
        node.setSuccessQps(successQps);
        if (successQps != 0) {
            node.setRt(wrap.value().rt() / successQps);
        } else {
            node.setRt(wrap.value().rt());
        }
        node.setTimestamp(wrap.bucketStart());
        node.setOccupiedPassQps(wrap.value().occupiedPass());
        return node;
    }

    @Override
    public MetricBucket[] buckets() {
        data.currentBucket();
        return data.values().toArray(new MetricBucket[0]);
    }

    @Override
    public void addException(int count) {
        BucketWrapper<MetricBucket> wrap = data.currentBucket();
        wrap.value().addException(count);
    }

    @Override
    public void addBlock(int count) {
        BucketWrapper<MetricBucket> wrap = data.currentBucket();
        wrap.value().addBlock(count);
    }

    @Override
    public void addWaiting(long time, int acquireCount) {
        data.addWaiting(time, acquireCount);
    }

    @Override
    public void addOccupiedPass(int acquireCount) {
        BucketWrapper<MetricBucket> wrap = data.currentBucket();
        wrap.value().addOccupiedPass(acquireCount);
    }

    @Override
    public void addSuccess(int count) {
        BucketWrapper<MetricBucket> wrap = data.currentBucket();
        wrap.value().addSuccess(count);
    }

    @Override
    public void addPass(int count) {
        BucketWrapper<MetricBucket> wrap = data.currentBucket();
        wrap.value().addPass(count);
    }

    @Override
    public void addRT(long rt) {
        BucketWrapper<MetricBucket> wrap = data.currentBucket();
        wrap.value().addRT(rt);
    }

    @Override
    public long previousWindowBlock() {
        data.currentBucket();
        BucketWrapper<MetricBucket> wrap = data.getPreviousBucket();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().block();
    }

    @Override
    public long previousWindowPass() {
        data.currentBucket();
        BucketWrapper<MetricBucket> wrap = data.getPreviousBucket();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().pass();
    }

    public void add(MetricEvent event, long count) {
        data.currentBucket().value().add(event, count);
    }

    public long getCurrentCount(MetricEvent event) {
        return data.currentBucket().value().get(event);
    }

    /**
     * Get total sum for provided event in {@code intervalInSec}.
     *
     * @param event event to calculate
     * @return total sum for event
     */
    public long getSum(MetricEvent event) {
        data.currentBucket();
        long sum = 0;

        List<MetricBucket> buckets = data.values();
        for (MetricBucket bucket : buckets) {
            sum += bucket.get(event);
        }
        return sum;
    }

    /**
     * Get average count for provided event per second.
     *
     * @param event event to calculate
     * @return average count per second for event
     */
    public double getAvg(MetricEvent event) {
        return getSum(event) / data.getWindowLengthInSecond();
    }

    @Override
    public long getWindowPass(long timeMillis) {
        MetricBucket bucket = data.getBucketValue(timeMillis);
        if (bucket == null) {
            return 0L;
        }
        return bucket.pass();
    }

    @Override
    public long waiting() {
        return data.currentWaiting();
    }

    @Override
    public double getWindowIntervalInSec() {
        return data.getWindowLengthInSecond();
    }

    @Override
    public int getSampleCount() {
        return data.getSampleCount();
    }
}
