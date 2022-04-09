package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;

public class NoOpLocalizer implements Localizer {

    @Override
    public String localize(String key, Locale locale, Replacement... replacements) {
        return key;
    }

    @Override
    public boolean keyExists(String key, Locale locale) {
        return false;
    }
}
