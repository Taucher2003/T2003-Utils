package com.gitlab.taucher2003.t2003_utils.common;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
class BucketTest {

    private final BucketData data = new BucketData(1, 1, TimeUnit.SECONDS);

    @Test
    void isRatelimit() throws InterruptedException {
        var bucket = new Bucket(data);
        assertThat(bucket.isRatelimit()).isFalse();
        bucket.use();
        assertThat(bucket.isRatelimit()).isTrue();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertThat(bucket.isRatelimit()).isFalse();
    }

    @Test
    void use() throws InterruptedException {
        var bucket = new Bucket(data);
        assertThat(bucket.use()).isTrue();
        assertThat(bucket.use()).isFalse();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertThat(bucket.use()).isTrue();
    }

    @Test
    void getUsableInMilliseconds() {
        var bucket = new Bucket(data);
        bucket.use();
        assertThat(bucket.getUsableIn(TimeUnit.MILLISECONDS)).isCloseTo(1000, Offset.offset(10L));
    }

    @Test
    void getUsableInSeconds() {
        var bucket = new Bucket(data);
        bucket.use();
        assertThat(bucket.getUsableIn(TimeUnit.SECONDS)).isCloseTo(1, Offset.offset(1L));
    }
}
