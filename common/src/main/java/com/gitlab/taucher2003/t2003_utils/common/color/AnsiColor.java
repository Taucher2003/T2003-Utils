package com.gitlab.taucher2003.t2003_utils.common.color;

import com.gitlab.taucher2003.t2003_utils.common.i18n.StringFormat;

public enum AnsiColor implements StringFormat {

    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m");

    public final String ansi;

    AnsiColor(String ansi) {
        this.ansi = ansi;
    }

    /**
     * Formats a string with the given ansi escape sequence
     *
     * @param stringToColorize the String to be colorized
     * @return the colorized String
     * @deprecated Replaced with {@link #apply(String)}
     */
    @Deprecated
    public String colorize(String stringToColorize) {
        return apply(stringToColorize);
    }

    /**
     * Strips all Ansi Escape Codes supported by this enum from the string
     *
     * @param coloredString the string to strip
     * @return the string without ansi escape codes
     */
    public static String stripColors(String coloredString) {
        return coloredString.replaceAll("\u001B\\[(0|3[0-7])m", "");
    }

    public static AnsiColor fromName(String name) {
        for (var color : values()) {
            if (color.name().equalsIgnoreCase(name)) {
                return color;
            }
        }
        return null;
    }

    /**
     * Formats a string with the given ansi escape sequence
     *
     * @param s the String to be colorized
     * @return the colorized String
     */
    @Override
    public String apply(String s) {
        return this + s + RESET;
    }

    @Override
    public String toString() {
        return ansi;
    }
}
