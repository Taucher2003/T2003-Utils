package com.gitlab.taucher2003.t2003_utils.common.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class FailureLoggingScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(FailureLoggingScheduledThreadPoolExecutor.class);

    public FailureLoggingScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public FailureLoggingScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public FailureLoggingScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public FailureLoggingScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                var future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException exception) {
                return;
            } catch (ExecutionException exception) {
                t = exception.getCause();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            LOG.error("An unhandled exception occurred", t);
        }
    }

    // --- Future Delegation


    @Override
    public ScheduledFuture<?> schedule(@NotNull Runnable command, long delay, @NotNull TimeUnit unit) {
        return new CancelingLoggingFuture.CancelingLoggingScheduledFuture<>(super.schedule(command, delay, unit));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
        return new CancelingLoggingFuture.CancelingLoggingScheduledFuture<>(super.schedule(callable, delay, unit));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable command, long initialDelay, long period, @NotNull TimeUnit unit) {
        return new CancelingLoggingFuture.CancelingLoggingScheduledFuture<>(super.scheduleAtFixedRate(command, initialDelay, period, unit));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable command, long initialDelay, long delay, @NotNull TimeUnit unit) {
        return new CancelingLoggingFuture.CancelingLoggingScheduledFuture<>(super.scheduleWithFixedDelay(command, initialDelay, delay, unit));
    }

    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable task) {
        return new CancelingLoggingFuture<>(super.submit(task));
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        return new CancelingLoggingFuture<>(super.submit(task, result));
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        return new CancelingLoggingFuture<>(super.submit(task));
    }
}
