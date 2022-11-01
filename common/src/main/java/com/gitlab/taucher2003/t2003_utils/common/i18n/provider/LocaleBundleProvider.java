package com.gitlab.taucher2003.t2003_utils.common.i18n.provider;

import java.util.Locale;

/**
 * Functional interface for providing a {@link LocaleBundle} for a given locale
 */
@FunctionalInterface
public interface LocaleBundleProvider {

    /**
     * Get the corresponding {@link LocaleBundle} for a given {@link Locale}
     *
     * @param locale the locale for the bundle
     * @return the bundle for the given locale
     */
    LocaleBundle getBundle(Locale locale);

    interface LocaleBundle {

        String getString(String key);

        boolean containsKey(String key);
    }
}
