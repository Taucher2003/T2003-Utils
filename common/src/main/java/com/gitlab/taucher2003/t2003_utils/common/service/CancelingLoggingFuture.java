package com.gitlab.taucher2003.t2003_utils.common.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CancelingLoggingFuture<T> implements Future<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelingLoggingFuture.class);

    protected final Future<T> delegate;

    public CancelingLoggingFuture(Future<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (!isCancelled()) {
            var caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
            LOGGER.debug("Cancelled {} by {}", this.delegate, caller.getCanonicalName());
        }
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    @Override
    public String toString() {
        return "CancelingLoggingFuture{" +
                "delegate=" + delegate +
                '}';
    }

    public static class CancelingLoggingScheduledFuture<T> extends CancelingLoggingFuture<T> implements ScheduledFuture<T> {

        public CancelingLoggingScheduledFuture(@SuppressWarnings("TypeMayBeWeakened") ScheduledFuture<T> delegate) {
            super(delegate);
        }

        private ScheduledFuture<T> asScheduled() {
            return (ScheduledFuture<T>) delegate;
        }

        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return asScheduled().getDelay(unit);
        }

        @Override
        public int compareTo(@NotNull Delayed o) {
            return asScheduled().compareTo(o);
        }

        @Override
        public String toString() {
            return "CancelingLoggingScheduledFuture{" +
                    "delegate=" + delegate +
                    '}';
        }
    }
}
