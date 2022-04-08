package com.gitlab.taucher2003.t2003_utils.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.gitlab.taucher2003.t2003_utils.common.color.AnsiColor;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ColorConverterTest {

    ColorConverter colorConverter = new ColorConverter();

    @Test
    void transformForLogLevel() {
        colorConverter.setOptionList(Collections.emptyList());
        assertThat(colorConverter.transform(buildEvent(Level.TRACE), "Message")).isEqualTo(AnsiColor.BLACK + "Message" + AnsiColor.RESET);
        assertThat(colorConverter.transform(buildEvent(Level.DEBUG), "Message")).isEqualTo(AnsiColor.BLUE + "Message" + AnsiColor.RESET);
        assertThat(colorConverter.transform(buildEvent(Level.INFO), "Message")).isEqualTo(AnsiColor.GREEN + "Message" + AnsiColor.RESET);
        assertThat(colorConverter.transform(buildEvent(Level.WARN), "Message")).isEqualTo(AnsiColor.YELLOW + "Message" + AnsiColor.RESET);
        assertThat(colorConverter.transform(buildEvent(Level.ERROR), "Message")).isEqualTo(AnsiColor.RED + "Message" + AnsiColor.RESET);
    }

    @Test
    void transformForParameter() {
        colorConverter.setOptionList(Collections.singletonList("red"));
        assertThat(colorConverter.transform(buildEvent(Level.INFO), "Message")).isEqualTo(AnsiColor.RED + "Message" + AnsiColor.RESET);

        colorConverter.setOptionList(Collections.singletonList("yellow"));
        assertThat(colorConverter.transform(buildEvent(Level.INFO), "Message")).isEqualTo(AnsiColor.YELLOW + "Message" + AnsiColor.RESET);
    }

    @Test
    void fallsBackToLevelColor() {
        colorConverter.setOptionList(Collections.singletonList("brown"));
        assertThat(colorConverter.transform(buildEvent(Level.WARN), "Message")).isEqualTo(AnsiColor.YELLOW + "Message" + AnsiColor.RESET);
    }

    private ILoggingEvent buildEvent(Level level) {
        return new LoggingEvent(
                Logger.FQCN,
                (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME),
                level,
                "",
                null,
                new Object[0]
        );
    }
}