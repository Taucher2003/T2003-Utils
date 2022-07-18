package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.common.i18n.Localizer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PrefixedLocalizationFunction implements LocalizationFunction {

    private final Localizer localizer;
    private final String keyPrefix;

    public PrefixedLocalizationFunction(Localizer localizer) {
        this.localizer = localizer;
        this.keyPrefix = null;
    }

    public PrefixedLocalizationFunction(Localizer localizer, String keyPrefix) {
        this.localizer = localizer;
        this.keyPrefix = keyPrefix;
    }

    @NotNull
    @Override
    public Map<DiscordLocale, String> apply(@NotNull String localizationKey) {
        var effectiveKey = keyPrefix == null ? localizationKey : keyPrefix + "." + localizationKey;

        var map = new HashMap<DiscordLocale, String>();
        for (var discordLocale : DiscordLocale.values()) {
            var localeTag = discordLocale.getLocale();
            var locale = Locale.forLanguageTag(localeTag);
            map.put(discordLocale, localizer.localize(effectiveKey, locale));
        }

        map.remove(DiscordLocale.UNKNOWN); // UNKNOWN can't be in the map
        return map;
    }
}
