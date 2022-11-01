package com.gitlab.taucher2003.t2003_utils.common.i18n.provider;

import java.util.Locale;

@FunctionalInterface
public interface LocaleBundleProvider {

    LocaleBundle getBundle(Locale locale);

    interface LocaleBundle {

        String getString(String key);

        boolean containsKey(String key);
    }
}
