package com.gitlab.taucher2003.t2003_utils.common.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The composing implementation of the {@link Localizer} interface.
 * <p>
 * It takes a {@link List} or an array of {@link Localizer}.
 * This implementation localizes by searching through all given {@link Localizer}s
 * and returning the first found translation.
 *
 * @see Localizer
 * @see DefaultLocalizer
 */
public class ComposedLocalizer implements Localizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComposedLocalizer.class);

    private final List<? extends Localizer> localizers;

    public ComposedLocalizer(List<? extends Localizer> localizers) {
        this.localizers = localizers;
    }

    public ComposedLocalizer(Localizer... localizers) {
        this(Arrays.asList(localizers));
    }

    @Override
    public String localize(String key, Locale locale, Replacement... replacements) {
        for (var localizer : localizers) {
            if (localizer.keyExists(key, locale)) {
                return localizer.localize(key, locale, replacements);
            }
        }
        LOGGER.error("Tried to lookup '{}' which does not exist in any of the given localizers", key);
        return key;
    }

    @Override
    public boolean keyExists(String key, Locale locale) {
        return localizers.stream().anyMatch(l -> l.keyExists(key, locale));
    }
}
