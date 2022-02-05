package com.gitlab.taucher2003.t2003_utils.common.function;

@FunctionalInterface
public interface SilentSupplier<T> {
    T get() throws Throwable;
}
