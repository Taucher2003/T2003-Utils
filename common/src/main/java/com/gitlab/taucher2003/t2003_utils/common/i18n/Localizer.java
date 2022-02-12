package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;

/**
 * Top level interface for localization implementations
 *
 * @see DefaultLocalizer
 * @see DefaultContextLocalizer
 * @see Replacement
 */
@FunctionalInterface
public interface Localizer {

    /**
     * Localize a message from a given key. Uses the default bundle ({@link Locale#ROOT}).
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @return the localized message
     */
    default String localize(String key) {
        return localize(key, Locale.ROOT);
    }

    /**
     * Localize a message from a given key. Uses the given {@link Locale}.
     * If no bundle is found for the given Locale, the fallback bundle is used.
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @param locale the preferred locale
     * @return the localized message
     */
    default String localize(String key, Locale locale) {
        return localize(key, locale, new Replacement[0]);
    }

    /**
     * Localize a message from a given key. Uses the default bundle ({@link Locale#ROOT}).
     *
     * This method allows using {@link Replacement}s.
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message with replacements applied
     */
    default String localize(String key, Replacement... replacements) {
        return localize(key, Locale.ROOT, replacements);
    }

    /**
     * Localize a message from a given key. Uses the given {@link Locale}.
     * If no bundle is found for the given Locale, the fallback bundle is used.
     *
     * This method allows using {@link Replacement}s.
     *
     * @param key the key of the message in the {@link java.util.ResourceBundle}
     * @param locale the preferred locale
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message with replacements applied
     */
    String localize(String key, Locale locale, Replacement... replacements);

}
