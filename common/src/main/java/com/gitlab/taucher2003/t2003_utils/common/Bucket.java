package com.gitlab.taucher2003.t2003_utils.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Bucket {

    private final BucketData data;

    private final AtomicInteger remaining;
    private final AtomicLong resetAt = new AtomicLong(System.currentTimeMillis());

    public Bucket(BucketData data) {
        this.data = data;
        this.remaining = new AtomicInteger(data.limit());
    }

    public boolean isRatelimit() {
        checkReset();
        return remaining.get() <= 0;
    }

    public boolean use() {
        checkReset();
        return remaining.decrementAndGet() >= 0;
    }

    public long getUsableIn(TimeUnit timeUnit) {
        return timeUnit.convert(resetAt.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public BucketData getData() {
        return data;
    }

    private void checkReset() {
        var now = System.currentTimeMillis();
        if (now >= resetAt.get()) {
            remaining.set(data.limit());
            resetAt.set(now + data.resetAfterUnit().toMillis(data.resetAfter()));
        }
    }
}
