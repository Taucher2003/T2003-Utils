package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.Localizer;
import com.gitlab.taucher2003.t2003_utils.common.i18n.Replacement;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Locale;

public class LocalizedEmbedMessageBuilder extends EmbedBuilder {

    private final Localizer localizer;
    private final Locale locale;

    public LocalizedEmbedMessageBuilder(Localizer localizer, Locale locale) {
        this.localizer = localizer;
        this.locale = locale;
    }

    private String __(String key, Replacement... replacements) {
        return localizer.localize(key, locale, replacements);
    }

    @Override
    @NotNull
    public LocalizedEmbedMessageBuilder setTitle(String title) {
        return setTitle(title, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setTitle(String title, Replacement... replacements) {
        super.setTitle(__(title, replacements));
        return this;
    }

    @Override
    @NotNull
    public LocalizedEmbedMessageBuilder setTitle(String title, String url) {
        return setTitle(title, url, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setTitle(String title, String url, Replacement... replacements) {
        super.setTitle(__(title, replacements), url);
        return this;
    }

    public LocalizedEmbedMessageBuilder setDescription(String description) {
        return setDescription(description, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setDescription(String description, Replacement... replacements) {
        super.setDescription(__(description, replacements));
        return this;
    }

    @Override
    @NotNull
    public LocalizedEmbedMessageBuilder appendDescription(@NotNull CharSequence description) {
        return appendDescription(description, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder appendDescription(CharSequence description, Replacement... replacements) {
        super.appendDescription(__(description.toString(), replacements));
        return this;
    }

    @Override
    @Nonnull
    public LocalizedEmbedMessageBuilder setAuthor(String name) {
        return setAuthor(name, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setAuthor(String name, Replacement... replacements) {
        super.setAuthor(__(name, replacements));
        return this;
    }

    @Override
    @Nonnull
    public LocalizedEmbedMessageBuilder setAuthor(String name, String url) {
        return setAuthor(name, url, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setAuthor(String name, String url, Replacement... replacements) {
        super.setAuthor(__(name, replacements), url);
        return this;
    }

    @Override
    @Nonnull
    public LocalizedEmbedMessageBuilder setAuthor(String name, String url, String iconUrl) {
        return setAuthor(name, url, iconUrl, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setAuthor(String name, String url, String iconUrl, Replacement... replacements) {
        super.setAuthor(__(name, replacements), url, iconUrl);
        return this;
    }

    @Override
    @Nonnull
    public LocalizedEmbedMessageBuilder setFooter(String text) {
        return setFooter(text, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setFooter(String text, Replacement... replacements) {
        super.setFooter(__(text, replacements));
        return this;
    }

    @Override
    @Nonnull
    public LocalizedEmbedMessageBuilder setFooter(String text, String iconUrl) {
        return setFooter(text, iconUrl, new Replacement[0]);
    }

    public LocalizedEmbedMessageBuilder setFooter(String text, String iconUrl, Replacement... replacements) {
        super.setFooter(__(text, replacements), iconUrl);
        return this;
    }
}
