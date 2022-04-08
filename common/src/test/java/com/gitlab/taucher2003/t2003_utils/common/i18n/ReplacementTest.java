package com.gitlab.taucher2003.t2003_utils.common.i18n;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplacementTest {

    static final Replacement replacement = new Replacement("key", "value");

    @Test
    void format() {
        assertThat(replacement.format(String::toUpperCase).getValue()).isEqualTo("VALUE");
        assertThat(replacement.format(s -> "*" + s + "*").getValue()).isEqualTo("*value*");
    }

    @Test
    void apply() {
        assertThat(replacement.apply("Test ${key}")).isEqualTo("Test value");
        assertThat(replacement.apply("Test key")).isEqualTo("Test key");
        assertThat(replacement.apply("Test %{key}")).isEqualTo("Test %{key}");
        assertThat(replacement.apply("Test ${value}")).isEqualTo("Test ${value}");
    }

    @Test
    void test() {
        assertThat(replacement.test("Test ${key}")).isTrue();
        assertThat(replacement.test("Test key")).isFalse();
        assertThat(replacement.test("Test %{key}")).isFalse();
        assertThat(replacement.test("Test ${value}")).isFalse();
    }

    @Test
    void canBeUsedWithNonStringValues() {
        var replacement = new Replacement("key", 1);
        assertThat(replacement.getValue()).isEqualTo("1");
    }
}