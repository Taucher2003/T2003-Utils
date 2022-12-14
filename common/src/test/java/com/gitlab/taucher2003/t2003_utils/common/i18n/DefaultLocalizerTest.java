package com.gitlab.taucher2003.t2003_utils.common.i18n;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gitlab.taucher2003.t2003_utils.common.i18n.provider.ResourceBundleProvider;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

class DefaultLocalizerTest {

    DefaultLocalizer localizer = new DefaultLocalizer(new ResourceBundleProvider("i18n.default"));

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
    void localizeWithMultipleNestedKey() {
        assertThat(localizer.localize("key.with.multiple.nested")).isEqualTo("nested-key key from main");
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
                .containsExactly(Tuple.tuple("Tried to lookup 'key.does.not.exist' which does not exist in 'ResourceBundleProvider{bundleName='i18n.default'}'", Level.ERROR));
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
        assertThat(localizer.localize("circular.key")).isEqualTo("circular1 circular2 %{circular.key}");
    }

    @Test
    void logsCircularKeys() {
        var appender = setupAppender();

        localizer.localize("circular.key");

        Assertions.assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("Circular reference with nodes [circular.key, circular.key2] detected", Level.ERROR));
    }

    @Test
    void doesNotLogNonCircularKeys() {
        var appender = setupAppender();

        assertThat(localizer.localize("key.without.circular")).isEqualTo("circular3 key from main key from main");

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .isEmpty();
    }

    @Test
    void doesNotFailWithUnclosedNestedString() {
        assertThat(localizer.localize("without.closing.string.bracket")).isEqualTo("Bla %{not.closed");
    }

    @Test
    void keyExistsWorks() {
        assertThat(localizer.keyExists("key")).isTrue();
        assertThat(localizer.keyExists("bla")).isFalse();
    }

    @Test
    void logsNonExistentReplacements() {
        var appender = setupAppender();

        assertThat(localizer.localize("key", new Replacement("variable", "replacement"))).isEqualTo("key from main");

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("String 'key from main' does not contain replacement 'variable'", Level.WARN));
    }

    @Test
    void canHandleNullLocale() {
        assertThat(localizer.localize("key", (Locale) null)).isEqualTo("key from main");
    }

    @Test
    void logsNullLocales() {
        var appender = setupAppender();

        localizer.localize("key", (Locale) null);

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("Localizer was called with key 'key' and null locale", Level.WARN));
    }

    private ListAppender<ILoggingEvent> setupAppender() {
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        var root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        root.addAppender(appender);
        return appender;
    }
}
