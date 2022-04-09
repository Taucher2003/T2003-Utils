package com.gitlab.taucher2003.t2003_utils.common.i18n;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoOpLocalizerTest {

    NoOpLocalizer localizer = new NoOpLocalizer();

    @Test
    void localize() {
        assertThat(localizer.localize("key")).isEqualTo("key");
    }

    @Test
    void keyExists() {
        assertThat(localizer.keyExists("key")).isFalse();
    }
}