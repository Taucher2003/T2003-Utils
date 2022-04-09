package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.Localizer;
import com.gitlab.taucher2003.t2003_utils.common.i18n.Replacement;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Locale;

public class LocalizedField extends MessageEmbed.Field {

    private final Localizer localizer;
    private final Locale locale;

    public LocalizedField(Localizer localizer, Locale locale) {
        super(null, null, false, false);
        this.localizer = localizer;
        this.locale = locale;
    }

    private LocalizedField(Localizer localizer, Locale locale, String name, String description, boolean inline) {
        super(name, description, inline);
        this.localizer = localizer;
        this.locale = locale;
    }

    private String __(String key, Replacement... replacements) {
        return localizer.localize(key, locale, replacements);
    }

    public LocalizedField name(String name, Replacement... replacements) {
        return new LocalizedField(localizer, locale, __(name, replacements), value, inline);
    }

    public LocalizedField value(String value, Replacement... replacements) {
        return new LocalizedField(localizer, locale, name, __(value, replacements), inline);
    }

    public LocalizedField inline(boolean inline) {
        return new LocalizedField(localizer, locale, name, value, inline);
    }
}
