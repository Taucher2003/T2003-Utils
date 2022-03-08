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

import static org.assertj.core.api.Assertions.assertThat;

class ComposedLocalizerTest {

    ComposedLocalizer localizer = new ComposedLocalizer(
            new DefaultLocalizer("i18n.composing"),
            new DefaultLocalizer("i18n.default")
    );

    @Test
    void canLocalizeMessages() {
        assertThat(localizer.localize("key")).isEqualTo("overwritten key from composing");
    }

    @Test
    void canLocalizeMessagesMissingInFirstLocalizer() {
        assertThat(localizer.localize("nested.key")).isEqualTo("nested-key");
    }

    @Test
    void canLocalizeMessagesOnlyInFirstLocalizer() {
        assertThat(localizer.localize("composing.key")).isEqualTo("key from composing");
    }

    @Test
    void logsKeysMissingInAllLocalizers() {
        var appender = setupAppender();

        localizer.localize("non-existing");

        Assertions.assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("Tried to lookup 'non-existing' which does not exist in any of the given localizers", Level.ERROR));
    }

    @Test
    void keyExistsWorks() {
        assertThat(localizer.keyExists("key")).isTrue();
        assertThat(localizer.keyExists("composing.key")).isTrue();
        assertThat(localizer.keyExists("nested.key")).isTrue();
        assertThat(localizer.keyExists("non-existing")).isFalse();
    }

    private ListAppender<ILoggingEvent> setupAppender() {
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        AppenderAttachable<ILoggingEvent> context = (Logger) LoggerFactory.getLogger("ROOT");
        context.addAppender(appender);
        return appender;
    }
}