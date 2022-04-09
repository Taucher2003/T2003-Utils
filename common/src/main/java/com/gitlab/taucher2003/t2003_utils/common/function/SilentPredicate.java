package com.gitlab.taucher2003.t2003_utils.common.function;

@FunctionalInterface
public interface SilentPredicate<T> {

    boolean test(T t) throws Throwable;
}
