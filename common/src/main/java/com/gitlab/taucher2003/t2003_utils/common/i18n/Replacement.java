package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A replacement is used for replacing variables in localized messages
 */
public class Replacement implements Function<String, String>, Predicate<String> {

    private final String key;
    private final String value;

    /**
     * @param key   the name of the variable
     * @param value the value which should be used to replace the variable
     */
    public Replacement(String key, Object value) {
        this(key, value.toString());
    }

    /**
     * @param key   the name of the variable
     * @param value the value which should be used to replace the variable
     */
    public Replacement(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the name of the variable
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value which is used to replace the variable
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a new Replacement with the given {@link StringFormat}s applied
     *
     * @param formats an array (or varargs) of {@link StringFormat}s to apply
     * @return the cloned Replacement with the {@link StringFormat}s applied
     */
    public Replacement format(StringFormat... formats) {
        var newFormat = this.value;
        for (var format : formats) {
            newFormat = format.apply(newFormat);
        }
        return new Replacement(key, newFormat);
    }

    /**
     * Returns a new String with this Replacement applied to it.
     *
     * @param s the target string
     * @return a new string with this Replacement applied
     */
    @Override
    public String apply(String s) {
        return s.replace("${" + key + "}", value);
    }

    /**
     * Checks if the given String contains variables which can be replaced with this Replacement
     *
     * @param s the target string
     * @return a boolean indicating the compatibility with this Replacement
     */
    @Override
    public boolean test(String s) {
        return s.contains("${" + key + "}");
    }
}
