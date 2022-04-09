package com.gitlab.taucher2003.t2003_utils.common.color;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class AnsiColorTest {

    @SuppressWarnings("deprecation")
    @Test
    void colorize() {
        for (var color : AnsiColor.values()) {
            assertThat(color.colorize("String")).isEqualTo(color + "String" + AnsiColor.RESET);
        }
    }

    @Test
    void stripColors() {
        var joinedColors = Arrays.stream(AnsiColor.values()).map(AnsiColor::toString).collect(Collectors.joining()) + "Normal";
        assertThat(AnsiColor.stripColors(joinedColors)).isEqualTo("Normal");
    }

    @Test
    void fromName() {
        assertThat(AnsiColor.fromName("red")).isEqualTo(AnsiColor.RED);
        assertThat(AnsiColor.fromName("green")).isEqualTo(AnsiColor.GREEN);
        assertThat(AnsiColor.fromName("reset")).isEqualTo(AnsiColor.RESET);
    }

    @Test
    void apply() {
        for (var color : AnsiColor.values()) {
            assertThat(color.apply("String")).isEqualTo(color + "String" + AnsiColor.RESET);
        }
    }

    @Test
    void testToString() {
        for (var color : AnsiColor.values()) {
            assertThat(color.toString()).isEqualTo(color.ansi);
            assertThat(color + "").isEqualTo(color.ansi);
        }
    }
}