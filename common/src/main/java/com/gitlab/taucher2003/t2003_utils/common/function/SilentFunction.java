package com.gitlab.taucher2003.t2003_utils.common.function;

@FunctionalInterface
public interface SilentFunction<I, O> {
    O apply(I i) throws Throwable;
}
