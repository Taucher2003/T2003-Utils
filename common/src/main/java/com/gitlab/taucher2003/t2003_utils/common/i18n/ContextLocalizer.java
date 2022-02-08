package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;
import java.util.function.Function;

public class ContextLocalizer<T> extends DefaultLocalizer {

    private final Function<T, Locale> localeResolver;

    public ContextLocalizer(String bundleName, Function<T, Locale> localeResolver) {
        super(bundleName);
        this.localeResolver = localeResolver;
    }

    public String localize(String key, T t) {
        return localize(key, t, new Replacement[0]);
    }

    public String localize(String key, T t, Replacement... replacements) {
        return localize(key, localeResolver.apply(t), replacements);
    }
}
