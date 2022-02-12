package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.Locale;
import java.util.function.Function;

/**
 * A context scoped implementation of the {@link Localizer} interface.
 * <p>
 * It provides context specialized localization of a generic type T.
 * The T object is resolved to a {@link Locale} and passed to the {@link DefaultLocalizer}.
 *
 * @param <T> A type which should be usable for resolving the {@link Locale}
 * @see Localizer
 * @see DefaultLocalizer
 */
public class DefaultContextLocalizer<T> extends DefaultLocalizer implements ContextLocalizer<T> {

    private final Function<T, Locale> localeResolver;

    /**
     * Creates a new ContextLocalizer.
     * The ContextLocalizer allows using a generic type T to resolve a {@link Locale}
     *
     * @param bundleName     the name of the {@link java.util.ResourceBundle} to use
     * @param localeResolver a {@link Function} which resolves the generic type T to a {@link Locale}
     */
    public DefaultContextLocalizer(String bundleName, Function<T, Locale> localeResolver) {
        super(bundleName);
        this.localeResolver = localeResolver;
    }

    /**
     * Resolves the {@link Locale} for the given genery type T
     *
     * @param t the generic type T to resolve the {@link Locale} from
     * @return the resolved {@link Locale}
     */
    @Override
    public Locale getLocale(T t) {
        return localeResolver.apply(t);
    }
}
