package com.gitlab.taucher2003.t2003_utils.common.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The default implementation of the {@link Localizer} interface.
 * <p>
 * It provides the most basic localization method using a {@link Locale} to define the target language.
 *
 * @see Localizer
 * @see DefaultContextLocalizer
 * @see ComposedLocalizer
 */
public class DefaultLocalizer implements Localizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalizer.class);

    private final String bundleName;

    /**
     * Creates a new DefaultLocalizer with a given bundle name.
     *
     * @param bundleName the name of the {@link ResourceBundle} to use
     */
    public DefaultLocalizer(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public String localize(String key, Locale locale, Replacement... replacements) {
        var bundle = getBundle(locale);
        var expandedMessage = resolveNestedStrings(key, bundle::getString, bundle::containsKey, new HashSet<>());
        return applyReplacements(expandedMessage, replacements);
    }

    @Override
    public boolean keyExists(String key, Locale locale) {
        return getBundle(locale).containsKey(key);
    }

    protected ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(bundleName, locale, DefaultLocalizerControl.SINGLETON);
    }

    protected String resolveNestedStrings(String key, Function<String, String> messageResolver, Predicate<String> messageExistsPredicate, Set<String> visitedNodes) {
        if (!messageExistsPredicate.test(key)) {
            LOGGER.error("Tried to lookup '{}' which does not exist in bundle '{}'", key, bundleName);
            return key;
        }
        visitedNodes.add(key);
        var message = messageResolver.apply(key);
        var currentCheckIndex = 0;
        int startIndex, endIndex;
        while (
                (startIndex = message.indexOf("%{", currentCheckIndex)) >= 0
                        && (endIndex = message.indexOf("}", currentCheckIndex + 1)) >= 0
        ) {
            currentCheckIndex = endIndex - 1;
            var nestedKey = message.substring(startIndex + 2, endIndex);
            message = resolveNestedString(message, nestedKey, messageResolver, messageExistsPredicate, visitedNodes);
        }
        return message;
    }

    protected String resolveNestedString(String message, String nestedKey, Function<String, String> messageResolver,
                                         Predicate<String> messageExistsPredicate, Set<String> visitedNodes) {
        if (visitedNodes.contains(nestedKey)) {
            LOGGER.error("Circular reference with nodes {} detected", visitedNodes);
            return message;
        }

        var resolvedNested = resolveNestedStrings(nestedKey, messageResolver, messageExistsPredicate, visitedNodes);
        if (resolvedNested.equals(nestedKey)) {
            return message;
        }
        return message.replace("%{" + nestedKey + "}", resolvedNested);
    }

    protected String applyReplacements(String message, Replacement... replacements) {
        if (replacements.length == 0) {
            return message;
        }
        var result = message;
        for (var replacement : replacements) {
            if (!replacement.test(message)) {
                LOGGER.warn("String '{}' does not contain replacement '{}'", message, replacement.getKey());
                continue;
            }
            result = replacement.apply(result);
        }
        return result;
    }

    private static final class DefaultLocalizerControl extends ResourceBundle.Control {
        private static final DefaultLocalizerControl SINGLETON = new DefaultLocalizerControl();

        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return Locale.ROOT;
        }
    }
}
