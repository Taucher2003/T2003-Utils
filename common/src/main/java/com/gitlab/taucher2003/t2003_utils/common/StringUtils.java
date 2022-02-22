package com.gitlab.taucher2003.t2003_utils.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Formats a {@link Duration} to a human readable string
     *
     * @param duration the duration which should be formatted
     * @return the formatted duration
     */
    public static String toReadableString(Duration duration) {
        var days = duration.toDays();
        var hours = duration.toHoursPart();
        var minutes = duration.toMinutesPart();
        var seconds = duration.toSecondsPart();

        var builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append(" Days, ");
        }
        if (hours > 0) {
            builder.append(hours).append(" Hours, ");
        }
        if (minutes > 0) {
            builder.append(minutes).append(" Minutes, ");
        }
        builder.append(seconds).append(" Seconds");

        return builder.toString();
    }

    /**
     * Gets the stack trace of a Throwable as it would be printed by {@link Throwable#printStackTrace()}
     *
     * @param throwable the throwable, which stack trace should be returned
     * @return the stack trace
     */
    public static String getExceptionStacktrace(Throwable throwable) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * Gets the stack trace of a Throwable as it would be printed by {@link Throwable#printStackTrace()}
     *
     * @param throwable     the throwable, which stack trace should be returned
     * @param maxCharacters the limit of characters at which the stack trace should be cut
     * @return the stack trace
     */
    public static String getExceptionStacktrace(Throwable throwable, int maxCharacters) {
        if (throwable == null) {
            return null;
        }

        var builder = new StringBuilder(throwable + "\n");

        var currentThrowable = throwable;
        do {
            var stackTraceElements = currentThrowable.getStackTrace();
            if (currentThrowable != throwable) {
                builder.append("Caused by: ").append(currentThrowable).append("\n");
            }
            appendException(builder, stackTraceElements, maxCharacters);
        } while ((currentThrowable = currentThrowable.getCause()) != null);


        return builder.toString();
    }

    private static void appendException(StringBuilder builder, StackTraceElement[] stackTraceElements, int maxCharacters) {
        for (var i = 0; i < stackTraceElements.length; i++) {
            final var xMoreConstant = "\t... " + (stackTraceElements.length - i) + " more\n";
            var toAppend = "\tat " + stackTraceElements[i].toString() + "\n";
            if ((builder.length() + toAppend.length() + xMoreConstant.length()) >= maxCharacters) {
                builder.append(xMoreConstant);
                break;
            }
            builder.append(toAppend);
        }
    }

    /**
     * Cuts a string to the given amount of characters
     * This method shows begin and end.
     * <br>
     * {@code
     * shortenWithMiddleCut("Some Text with a message", 15)
     * => Some Te...ssage
     * }
     *
     * @param message the message to shorten
     * @param max     the amount of maximum characters
     * @return the shortened message
     * @see #shortenWithEndCut(String, int)
     */
    public static String shortenWithMiddleCut(String message, int max) {
        if (message.length() <= max) {
            return message;
        }

        if(max <= 3) {
            return Stream.generate(() -> ".").limit(max).collect(Collectors.joining());
        }

        var length = message.length();
        var firstSubEnd = (max / 2);
        if (max % 2 == 0) {
            firstSubEnd -= 1;
        }
        var secondSubStart = length - (max / 2) + 2;
        return message.substring(0, firstSubEnd) + "..." + message.substring(secondSubStart, length);
    }

    /**
     * Cuts a string to the given amount of characters
     * This method shows begin.
     * <br>
     * {@code
     * shortenWithMiddleCut("Some Text with a message", 15)
     * => Some Text wi...
     * }
     *
     * @param message the message to shorten
     * @param max     the amount of maximum characters
     * @return the shortened message
     * @see #shortenWithMiddleCut(String, int)
     */
    public static String shortenWithEndCut(String message, int max) {
        if (message.length() <= max) {
            return message;
        }

        if(max <= 3) {
            return Stream.generate(() -> ".").limit(max).collect(Collectors.joining());
        }

        return message.substring(0, max - 3) + "...";
    }

    /**
     * Appends leading zeros to a given number until the minimum length is reached
     *
     * @param number the number to append leading zeros to
     * @param minLength the minimum amount of characters for the result string
     * @return the result string with the leading zeros appended
     */
    public static String appendLeadingZeros(long number, int minLength) {
        var currentLength = Long.toString(number).length();
        var missingZeros = Math.max(0, minLength - currentLength);
        return "0".repeat(missingZeros) + number;
    }

    /**
     * Removes all newlines (\r\n and \n) from the given String
     *
     * @param value the string to remove newlines from
     * @return the result string with newlines removed
     */
    public static String removeNewlines(String value) {
        return value.replace("\r\n", "").replace("\n", "");
    }
}
