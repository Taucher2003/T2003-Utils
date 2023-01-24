package com.gitlab.taucher2003.t2003_utils.common.cache;

import java.util.function.Supplier;

public class Memoizer<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value;
    private boolean loaded;

    public Memoizer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!loaded) {
            value = supplier.get();
            loaded = true;
        }
        return value;
    }

    public void invalidate() {
        loaded = false;
    }
}
