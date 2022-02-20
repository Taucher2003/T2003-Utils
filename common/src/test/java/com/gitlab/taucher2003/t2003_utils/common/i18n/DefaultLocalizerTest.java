package com.gitlab.taucher2003.t2003_utils.common.i18n;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.spi.AppenderAttachable;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

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
    void localizeWithNonexistentNestedKey() {
        assertThat(localizer.localize("key.with.nonexistent.nested")).isEqualTo("key from main %{non-existing}");
        assertThat(localizer.localize("key.with.nonexistent.nested", Locale.GERMANY)).isEqualTo("key from de_DE %{non-existing}");
        assertThat(localizer.localize("key.with.nonexistent.nested", Locale.US)).isEqualTo("key from en_US %{non-existing}");
    }

    @Test
    void logsNonexistentKeys() {
        var appender = setupAppender();

        localizer.localize("key.does.not.exist");

        Assertions.assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("Tried to lookup 'key.does.not.exist' which does not exist in bundle 'i18n.default'", Level.ERROR));
    }

    @Test
    void doesNotLogOnlyInMainExistentKeys() {
        var appender = setupAppender();

        localizer.localize("key.only.in.main");
        localizer.localize("key.only.in.main", Locale.GERMANY);
        localizer.localize("key.only.in.main", Locale.US);

        Assertions.assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .isEmpty();
    }

    @Test
    void localizeWithDoubleNestedKey() {
        assertThat(localizer.localize("key.with.double.nested")).isEqualTo("double-key from main key from main nested-key");
        assertThat(localizer.localize("key.with.double.nested", Locale.GERMANY)).isEqualTo("double-key from de_DE key from de_DE nested-key-de_DE");
        assertThat(localizer.localize("key.with.double.nested", Locale.US)).isEqualTo("double-key from en_US key from en_US nested-key-en_US");
    }

    @Test
    void localizeFallsBackToRoot() {
        assertThat(localizer.localize("key", Locale.CANADA)).isEqualTo("key from main");
        assertThat(localizer.localize("key.with.variable", Locale.CANADA, new Replacement("variable", "var"))).isEqualTo("key from main var");
    }

    @Test
    void doesNotFailWithCircularKeys() {
        assertThat(localizer.localize("circular.key")).isEqualTo("%{circular.key2}");
    }

    @Test
    void logsCircularKeys() {
        var appender = setupAppender();

        localizer.localize("circular.key");

        Assertions.assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("Circular reference in 'circular.key' detected", Level.ERROR));
    }

    private ListAppender<ILoggingEvent> setupAppender() {
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        AppenderAttachable<ILoggingEvent> context = (Logger) LoggerFactory.getLogger("ROOT");
        context.addAppender(appender);
        return appender;
    }
}