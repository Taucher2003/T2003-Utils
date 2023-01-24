package com.gitlab.taucher2003.t2003_utils.common.cache;

import com.gitlab.taucher2003.t2003_utils.common.data.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface Memoize {

    final class Memoizers {
        private static final Map<Pair<Memoize, String>, Memoizer<?>> memoizers = new HashMap<>();

        private Memoizers() {
        }
    }

    default <T> T memoize(String name, Supplier<T> supplier) {
        return (T) Memoizers.memoizers.computeIfAbsent(new Pair<>(this, name), key -> new Memoizer<>(supplier)).get();
    }

    default void forgetMemoize(String name) {
        Optional.ofNullable(Memoizers.memoizers.get(new Pair<>(this, name))).ifPresent(Memoizer::invalidate);
    }
}
