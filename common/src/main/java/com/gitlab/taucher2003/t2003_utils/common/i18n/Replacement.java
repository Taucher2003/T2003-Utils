package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.function.Function;
import java.util.function.Predicate;

public class Replacement implements Function<String, String>, Predicate<String> {

    private final String key;
    private final String value;

    public Replacement(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Replacement format(StringFormat... formats) {
        var value = this.value;
        for (var format : formats) {
            value = format.apply(value);
        }
        return new Replacement(key, value);
    }

    @Override
    public String apply(String s) {
        return s.replaceAll("\\$\\{" + key + "}", value);
    }

    @Override
    public boolean test(String s) {
        return s.contains("${" + key + "}");
    }
}
