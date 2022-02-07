package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;

public interface Localizer {

    String localize(String key);
    String localize(String key, Locale locale);
    String localize(String key, Replacement... replacements);
    String localize(String key, Locale locale, Replacement... replacements);

}
