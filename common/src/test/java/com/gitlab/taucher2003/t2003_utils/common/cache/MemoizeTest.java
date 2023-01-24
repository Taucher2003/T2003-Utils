package com.gitlab.taucher2003.t2003_utils.common.cache;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemoizeTest {

    @Test
    void memoize() {
        var provider = (Supplier<String>) mock(Supplier.class);
        when(provider.get()).thenReturn("a value");

        class TestClass implements Memoize {
            String memoized() {
                return memoize("memoize", provider);
            }
        }

        var testClass = new TestClass();
        var result = testClass.memoized();
        testClass.memoized();
        testClass.memoized();

        assertThat(result).isEqualTo("a value");
        verify(provider).get();
    }

    @Test
    void forgetMemoize() {
        var provider = (Supplier<String>) mock(Supplier.class);
        when(provider.get()).thenReturn("a value");

        class TestClass implements Memoize {
            String memoized() {
                return memoize("memoize", provider);
            }
        }

        var testClass = new TestClass();

        testClass.memoized();
        testClass.memoized();

        verify(provider).get();

        testClass.forgetMemoize("memoize");
        testClass.memoized();

        // 2 in total, one before forget and one after
        verify(provider, times(2)).get();
    }
}
