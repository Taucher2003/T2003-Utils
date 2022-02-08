package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.ContextLocalizer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Locale;
import java.util.function.Function;

public class DiscordGuildLocalizer extends ContextLocalizer<Guild> implements DiscordLocalizer {
    public DiscordGuildLocalizer(String bundleName, Function<Guild, Locale> localeResolver) {
        super(bundleName, localeResolver);
    }
}
