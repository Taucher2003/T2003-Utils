package com.gitlab.taucher2003.t2003_utils.common.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Pattern;

public class DefaultLocalizer implements Localizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalizer.class);

    private final String bundleName;

    public DefaultLocalizer(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public String localize(String key) {
        return localize(key, Locale.getDefault());
    }

    @Override
    public String localize(String key, Locale locale) {
        return localize(key, locale, new Replacement[0]);
    }

    @Override
    public String localize(String key, Replacement... replacements) {
        return localize(key, Locale.getDefault(), replacements);
    }

    @Override
    public String localize(String key, Locale locale, Replacement... replacements) {
        var bundle = ResourceBundle.getBundle(bundleName, locale);
        var expandedMessage = resolveNestedStrings(key, bundle::getString);
        return applyReplacements(expandedMessage, replacements);
    }

    protected static final String NESTED_KEY_PATTERN_STRING = "%\\{(?<key>[^}]+)}";
    protected static final Pattern NESTED_KEY_PATTERN = Pattern.compile(NESTED_KEY_PATTERN_STRING);

    protected String resolveNestedStrings(String key, Function<String, String> messageResolver) {
        var message = messageResolver.apply(key);
        var matcher = NESTED_KEY_PATTERN.matcher(message);
        while(matcher.find()) {
            var nestedKey = matcher.group("key");
            var resolvedNested = resolveNestedStrings(nestedKey, messageResolver);
            message = message.replace("%{" + nestedKey + "}", resolvedNested);
            matcher = NESTED_KEY_PATTERN.matcher(message);
        }
        return message;
    }

    protected String applyReplacements(String message, Replacement... replacements) {
        if(replacements.length == 0) {
            return message;
        }
        var result = message;
        for(var replacement : replacements) {
            if(!replacement.test(message)) {
                LOGGER.warn("String '{}' does not contain replacement '{}'", message, replacement.getKey());
                continue;
            }
            result = replacement.apply(result);
        }
        return result;
    }
}
