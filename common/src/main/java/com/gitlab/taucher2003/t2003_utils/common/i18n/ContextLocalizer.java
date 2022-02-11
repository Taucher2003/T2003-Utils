package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;
import java.util.function.Function;

/**
 * A context scoped implementation of the {@link Localizer} interface.
 *
 * It provides context specialized localization of a generic type T.
 * The T object is resolved to a {@link Locale} and passed to the {@link DefaultLocalizer}.
 *
 * @param <T> A type which should be usable for resolving the {@link Locale}
 *
 * @see Localizer
 * @see DefaultLocalizer
 */
public class ContextLocalizer<T> extends DefaultLocalizer {

    private final Function<T, Locale> localeResolver;

    /**
     * Creates a new ContextLocalizer.
     * The ContextLocalizer allows using a generic type T to resolve a {@link Locale}
     *
     * @param bundleName the name of the {@link java.util.ResourceBundle} to use
     * @param localeResolver a {@link Function} which resolves the generic type T to a {@link Locale}
     */
    public ContextLocalizer(String bundleName, Function<T, Locale> localeResolver) {
        super(bundleName);
        this.localeResolver = localeResolver;
    }

    /**
     * Localizes a message from a given key and dynamically resolves the {@link Locale} for the type T
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @param t the generic type T to resolve the {@link Locale} from
     * @return the localized message
     */
    public String localize(String key, T t) {
        return localize(key, t, new Replacement[0]);
    }

    /**
     * Localizes a message from a given key and dynamically resolves the {@link Locale} for the type T
     *
     * This method allows using {@link Replacement}s.
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @param t the generic type T to resolve the {@link Locale} from
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message
     */
    public String localize(String key, T t, Replacement... replacements) {
        return localize(key, localeResolver.apply(t), replacements);
    }
}
