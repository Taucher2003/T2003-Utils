package com.gitlab.taucher2003.t2003_utils.common.function;

@FunctionalInterface
public interface SilentConsumer<T> {

    void accept(T t) throws Throwable;
}
