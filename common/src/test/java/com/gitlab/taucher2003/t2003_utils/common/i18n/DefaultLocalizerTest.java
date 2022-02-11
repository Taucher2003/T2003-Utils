package com.gitlab.taucher2003.t2003_utils.common.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultLocalizerTest {

    DefaultLocalizer localizer = new DefaultLocalizer("i18n.default");

    @Test
    void localizeBasic() {
        assertThat(localizer.localize("key")).isEqualTo("key from main");
        assertThat(localizer.localize("key", Locale.GERMANY)).isEqualTo("key from de_DE");
        assertThat(localizer.localize("key", Locale.US)).isEqualTo("key from en_US");
    }

    @Test
    void localizeWithVariable() {
        assertThat(localizer.localize("key.with.variable", new Replacement("variable", "var"))).isEqualTo("key from main var");
        assertThat(localizer.localize("key.with.variable", Locale.GERMANY, new Replacement("variable", "var"))).isEqualTo("key from de_DE var");
        assertThat(localizer.localize("key.with.variable", Locale.US, new Replacement("variable", "var"))).isEqualTo("key from en_US var");

        assertThat(localizer.localize("key.with.variable", new Replacement("variable", "var2"))).isEqualTo("key from main var2");
        assertThat(localizer.localize("key.with.variable", Locale.GERMANY, new Replacement("variable", "var2"))).isEqualTo("key from de_DE var2");
        assertThat(localizer.localize("key.with.variable", Locale.US, new Replacement("variable", "var2"))).isEqualTo("key from en_US var2");
    }

    @Test
    void localizeWithNestedKey() {
        assertThat(localizer.localize("key.with.nested")).isEqualTo("key from main nested-key");
        assertThat(localizer.localize("key.with.nested", Locale.GERMANY)).isEqualTo("key from de_DE nested-key-de_DE");
        assertThat(localizer.localize("key.with.nested", Locale.US)).isEqualTo("key from en_US nested-key-en_US");
    }

    @Test
    void localizeWithDoubleNestedKey() {
        assertThat(localizer.localize("key.with.double.nested")).isEqualTo("double-key from main key from main nested-key");
        assertThat(localizer.localize("key.with.double.nested", Locale.GERMANY)).isEqualTo("double-key from de_DE key from de_DE nested-key-de_DE");
        assertThat(localizer.localize("key.with.double.nested", Locale.US)).isEqualTo("double-key from en_US key from en_US nested-key-en_US");
    }
}