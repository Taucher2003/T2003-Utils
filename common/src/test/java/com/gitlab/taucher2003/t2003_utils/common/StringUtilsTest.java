package com.gitlab.taucher2003.t2003_utils.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
class StringUtilsTest {

    @Test
    void toReadableString() {
        assertThat(StringUtils.toReadableString(Duration.ofDays(2))).isEqualTo("2 Days, 0 Seconds");
        assertThat(StringUtils.toReadableString(Duration.ofSeconds(120))).isEqualTo("2 Minutes, 0 Seconds");
        assertThat(StringUtils.toReadableString(Duration.ofSeconds(122))).isEqualTo("2 Minutes, 2 Seconds");
    }

    @Test
    void appendLeadingZeros() {
        assertThat(StringUtils.appendLeadingZeros(1, 3)).isEqualTo("001");
        assertThat(StringUtils.appendLeadingZeros(10, 3)).isEqualTo("010");
        assertThat(StringUtils.appendLeadingZeros(100, 3)).isEqualTo("100");
        assertThat(StringUtils.appendLeadingZeros(1000, 3)).isEqualTo("1000");
    }
}