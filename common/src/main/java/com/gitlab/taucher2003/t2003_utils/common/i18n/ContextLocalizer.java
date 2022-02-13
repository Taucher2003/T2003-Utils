package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;

/**
 * A context scoped extension interface for the {@link Localizer} interface.
 * <p>
 * It provides context specialized localization of a generic type T.
 *
 * @param <T> A type which should be usable for resolving the {@link Locale}
 * @see Localizer
 * @see DefaultLocalizer
 */
public interface ContextLocalizer<T> extends Localizer {

    /**
     * Resolves the {@link Locale} for the given generic type T
     *
     * @param t the generic type T to resolve the {@link Locale} from
     * @return the resolved {@link Locale}
     */
    Locale getLocale(T t);

    /**
     * Localizes a message from a given key and dynamically resolves the {@link Locale} for the type T
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @param t the generic type T to resolve the {@link Locale} from
     * @return the localized message
     */
    default String localize(String key, T t) {
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
    default String localize(String key, T t, Replacement... replacements) {
        return localize(key, getLocale(t), replacements);
    }
}
