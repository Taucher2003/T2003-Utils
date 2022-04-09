package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.DefaultContextLocalizer;
import com.gitlab.taucher2003.t2003_utils.common.i18n.Replacement;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Locale;
import java.util.function.Function;

public class DiscordGuildLocalizer extends DefaultContextLocalizer<Guild> implements DiscordLocalizer {

    public DiscordGuildLocalizer(String bundleName, Function<Guild, Locale> localeResolver) {
        super(bundleName, localeResolver);
    }

    @Override
    public String localize(String key, Guild guild) {
        return super.localize(key, guild);
    }

    @Override
    public String localize(String key, Guild guild, Replacement... replacements) {
        return super.localize(key, guild, replacements);
    }
}
