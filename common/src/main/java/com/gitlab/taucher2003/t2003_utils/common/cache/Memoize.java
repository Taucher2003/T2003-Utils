package com.gitlab.taucher2003.t2003_utils.common.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Memoize {

    private final Map<String, Memoizer<?>> memoizers = new HashMap<>();

    public <T> T memoize(String name, Supplier<T> supplier) {
        return (T) memoizers.computeIfAbsent(name, key -> new Memoizer<>(supplier)).get();
    }

    public void forgetMemoize(String name) {
        Optional.ofNullable(memoizers.get(name)).ifPresent(Memoizer::invalidate);
    }
}
