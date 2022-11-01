package com.gitlab.taucher2003.t2003_utils.common.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.provider.ResourceBundleProvider;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContextLocalizerTest {

    Map<Integer, Locale> locales = Map.of(
            1, Locale.ROOT,
            2, Locale.GERMANY,
            3, Locale.US
    );
    ContextLocalizer<Integer> localizer = new DefaultContextLocalizer<>(new ResourceBundleProvider("i18n.default"), locales::get);

    @Test
    void getLocale() {
        assertThat(localizer.getLocale(1)).isEqualTo(Locale.ROOT);
        assertThat(localizer.getLocale(2)).isEqualTo(Locale.GERMANY);
        assertThat(localizer.getLocale(3)).isEqualTo(Locale.US);
        assertThat(localizer.getLocale(4)).isNull();
    }

    @Test
    void localize() {
        assertThat(localizer.localize("key", 1)).isEqualTo("key from main");
        assertThat(localizer.localize("key", 2)).isEqualTo("key from de_DE");
        assertThat(localizer.localize("key", 3)).isEqualTo("key from en_US");
        assertThat(localizer.localize("key", 4)).isEqualTo("key from main");
    }

    @Test
    void worksWithReplacements() {
        var replacement = new Replacement("variable", "value");

        assertThat(localizer.localize("key.with.variable", 1, replacement)).isEqualTo("key from main value");
        assertThat(localizer.localize("key.with.variable", 2, replacement)).isEqualTo("key from de_DE value");
        assertThat(localizer.localize("key.with.variable", 3, replacement)).isEqualTo("key from en_US value");
        assertThat(localizer.localize("key.with.variable", 4, replacement)).isEqualTo("key from main value");
    }
}
