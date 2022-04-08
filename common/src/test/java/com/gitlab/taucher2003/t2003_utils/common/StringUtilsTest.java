package com.gitlab.taucher2003.t2003_utils.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
class StringUtilsTest {

    @Test
    void shortenWithMiddleCut() {
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 8)).isEqualTo("Som...ng");
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 11)).isEqualTo("SomeT...ong");
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 3)).isEqualTo("...");
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 2)).isEqualTo("..");
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 4)).isEqualTo("S...");
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 5)).isEqualTo("So...");
        assertThat(StringUtils.shortenWithMiddleCut("SomeTextWhichIsPrettyLong", 6)).isEqualTo("So...g");
        assertThat(StringUtils.shortenWithMiddleCut("SomeText", 8)).isEqualTo("SomeText");
    }

    @Test
    void shortenWithEndCut() {
        assertThat(StringUtils.shortenWithEndCut("SomeTextWhichIsPrettyLong", 8)).isEqualTo("SomeT...");
        assertThat(StringUtils.shortenWithEndCut("SomeTextWhichIsPrettyLong", 11)).isEqualTo("SomeText...");
        assertThat(StringUtils.shortenWithEndCut("SomeTextWhichIsPrettyLong", 3)).isEqualTo("...");
        assertThat(StringUtils.shortenWithEndCut("SomeTextWhichIsPrettyLong", 2)).isEqualTo("..");
        assertThat(StringUtils.shortenWithEndCut("SomeText", 8)).isEqualTo("SomeText");
    }

    @Test
    void toReadableString() {
        assertThat(StringUtils.toReadableString(Duration.ofDays(2))).isEqualTo("2 Days, 0 Seconds");
        assertThat(StringUtils.toReadableString(Duration.ofSeconds(120))).isEqualTo("2 Minutes, 0 Seconds");
        assertThat(StringUtils.toReadableString(Duration.ofSeconds(122))).isEqualTo("2 Minutes, 2 Seconds");
        assertThat(StringUtils.toReadableString(Duration.ofMinutes(122))).isEqualTo("2 Hours, 2 Minutes, 0 Seconds");
    }

    @Test
    void appendLeadingZeros() {
        assertThat(StringUtils.appendLeadingZeros(1, 3)).isEqualTo("001");
        assertThat(StringUtils.appendLeadingZeros(10, 3)).isEqualTo("010");
        assertThat(StringUtils.appendLeadingZeros(100, 3)).isEqualTo("100");
        assertThat(StringUtils.appendLeadingZeros(1000, 3)).isEqualTo("1000");
    }

    @Test
    void getExceptionStacktrace() throws InterruptedException {
        var atomicStacktrace = new AtomicReference<String>();
        var thread = new Thread(() -> {
            var exception = new Exception("Some Exception");
            atomicStacktrace.set(StringUtils.getExceptionStacktrace(exception));
        });
        thread.start();
        thread.join();
        assertThat(StringUtils.normalizeNewlines(atomicStacktrace.get())).matches("java\\.lang\\.Exception: Some Exception\n" +
                "\tat com\\.gitlab\\.taucher2003\\.t2003_utils\\.common\\.StringUtilsTest\\.lambda\\$getExceptionStacktrace\\$\\d+\\(StringUtilsTest.java:\\d+\\)\n" +
                "\tat java\\.base/java\\.lang\\.Thread\\.run\\(Thread\\.java:\\d+\\)\n");
    }

    @Test
    void getExceptionStacktraceWithCharacterLimit() throws InterruptedException {
        var atomicStacktrace = new AtomicReference<String>();
        var atomicStacktrace2 = new AtomicReference<String>();
        var thread = new Thread(() -> {
            var exception = new Exception("Some Exception");
            atomicStacktrace.set(StringUtils.getExceptionStacktrace(exception, 20));
            atomicStacktrace2.set(StringUtils.getExceptionStacktrace(exception, 500));
        });
        thread.start();
        thread.join();
        assertThat(atomicStacktrace.get()).isEqualTo("java.lang.Exception: Some Exception\n" +
                "\t... 2 more\n");
        assertThat(StringUtils.normalizeNewlines(atomicStacktrace2.get())).matches("java\\.lang\\.Exception: Some Exception\n" +
                "\tat com\\.gitlab\\.taucher2003\\.t2003_utils\\.common\\.StringUtilsTest\\.lambda\\$getExceptionStacktraceWithCharacterLimit\\$\\d+\\(StringUtilsTest.java:\\d+\\)\n" +
                "\tat java\\.base/java\\.lang\\.Thread\\.run\\(Thread\\.java:\\d+\\)\n");
    }

    @Test
    void getExceptionStacktraceWithCharacterLimitAndCause() throws InterruptedException {
        var atomicStacktrace = new AtomicReference<String>();
        var thread = new Thread(() -> {
            var cause = new Exception("Some cause");
            var exception = new Exception("Some Exception", cause);
            atomicStacktrace.set(StringUtils.getExceptionStacktrace(exception, 2000));
        });
        thread.start();
        thread.join();
        assertThat(StringUtils.normalizeNewlines(atomicStacktrace.get())).matches("java\\.lang\\.Exception: Some Exception\n" +
                "\tat com\\.gitlab\\.taucher2003\\.t2003_utils\\.common\\.StringUtilsTest\\.lambda\\$getExceptionStacktraceWithCharacterLimitAndCause\\$\\d+\\(StringUtilsTest.java:\\d+\\)\n" +
                "\tat java\\.base/java\\.lang\\.Thread\\.run\\(Thread\\.java:\\d+\\)\n" +
                "Caused by: java\\.lang\\.Exception: Some cause\n" +
                "\tat com\\.gitlab\\.taucher2003\\.t2003_utils\\.common\\.StringUtilsTest\\.lambda\\$getExceptionStacktraceWithCharacterLimitAndCause\\$\\d+\\(StringUtilsTest\\.java:\\d+\\)\n" +
                "\tat java\\.base/java\\.lang\\.Thread\\.run\\(Thread\\.java:\\d+\\)\n");
    }

    @Test
    void removeNewlines() {
        assertThat(StringUtils.removeNewlines("Line1\nLine2\nLine3")).isEqualTo("Line1Line2Line3");
        assertThat(StringUtils.removeNewlines("Line1\nLine2\tLine3")).isEqualTo("Line1Line2\tLine3");
        assertThat(StringUtils.removeNewlines("Line1\nLine2\r\nLine3")).isEqualTo("Line1Line2Line3");
    }

    @Test
    void normalizeNewlines() {
        assertThat(StringUtils.normalizeNewlines("Line1\r\nLine2\r\nLine3")).isEqualTo("Line1\nLine2\nLine3");
        assertThat(StringUtils.normalizeNewlines("Line1\r\nLine2\tLine3")).isEqualTo("Line1\nLine2\tLine3");
        assertThat(StringUtils.normalizeNewlines("Line1\nLine2\r\nLine3")).isEqualTo("Line1\nLine2\nLine3");
    }
}