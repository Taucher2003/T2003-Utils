package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import com.gitlab.taucher2003.t2003_utils.common.i18n.ContextLocalizer;
import com.gitlab.taucher2003.t2003_utils.common.i18n.DefaultContextLocalizer;
import com.gitlab.taucher2003.t2003_utils.common.i18n.Localizer;
import com.gitlab.taucher2003.t2003_utils.common.i18n.Replacement;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.PrefixedLocalizationFunction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An extended interface of {@link Localizer} providing convenience methods for localizing from specific JDA entities.
 *
 * @see Localizer
 * @see DefaultContextLocalizer
 * @see DiscordGuildLocalizer
 */
public interface DiscordLocalizer extends ContextLocalizer<Guild>, LocalizationFunction {

    /**
     * Localize a message from a given key and a {@link Guild} to resolve the {@link java.util.Locale} from
     *
     * @param key   the key of the message in the {@link java.util.ResourceBundle}
     * @param guild the {@link Guild} to resolve the {@link java.util.Locale} from
     * @return the localized message
     */
    @Override
    default String localize(String key, Guild guild) {
        return localize(key, guild, new Replacement[0]);
    }

    /**
     * Localize a message from a given key and a {@link Guild} to resolve the {@link java.util.Locale} from
     * <p>
     * This method allows using {@link Replacement}s.
     *
     * @param key          the key of the message in the {@link java.util.ResourceBundle}
     * @param guild        the {@link Guild} to resolve the {@link java.util.Locale} from
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message
     */
    @Override
    String localize(String key, Guild guild, Replacement... replacements);

    /**
     * Localize a message from a given key and a {@link GuildMessageChannel} to resolve the {@link java.util.Locale} from
     *
     * @param key     the key of the message in the {@link java.util.ResourceBundle}
     * @param channel the {@link GuildMessageChannel} to resolve the {@link java.util.Locale} from
     * @return the localized message
     */
    default String localize(String key, GuildMessageChannel channel) {
        return localize(key, channel, new Replacement[0]);
    }

    /**
     * Localize a message from a given key and a {@link GuildMessageChannel} to resolve the {@link java.util.Locale} from
     * <p>
     * This method allows using {@link Replacement}s.
     *
     * @param key          the key of the message in the {@link java.util.ResourceBundle}
     * @param channel      the {@link GuildMessageChannel} to resolve the {@link java.util.Locale} from
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message
     */
    default String localize(String key, GuildMessageChannel channel, Replacement... replacements) {
        return localize(key, channel.getGuild(), replacements);
    }

    /**
     * Localize a message from a given key and an {@link Interaction} to resolve the {@link java.util.Locale} from
     *
     * @param key         the key of the message in the {@link java.util.ResourceBundle}
     * @param interaction the {@link Interaction} to resolve the {@link java.util.Locale} from
     * @return the localized message
     */
    default String localize(String key, Interaction interaction) {
        return localize(key, interaction, new Replacement[0]);
    }

    /**
     * Localize a message from a given key and an {@link Interaction} to resolve the {@link java.util.Locale} from
     * <p>
     * This method allows using {@link Replacement}s.
     *
     * @param key          the key of the message in the {@link java.util.ResourceBundle}
     * @param interaction  the {@link Interaction} to resolve the {@link java.util.Locale} from
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message
     */
    default String localize(String key, Interaction interaction, Replacement... replacements) {
        if (interaction.isFromGuild()) {
            return localize(key, interaction.getGuild(), replacements);
        }
        return localize(key, replacements);
    }

    /**
     * Localize a message from a given key and an {@link MessageReceivedEvent} to resolve the {@link java.util.Locale} from
     *
     * @param key   the key of the message in the {@link java.util.ResourceBundle}
     * @param event the {@link MessageReceivedEvent} to resolve the {@link java.util.Locale} from
     * @return the localized message
     */
    default String localize(String key, MessageReceivedEvent event) {
        return localize(key, event, new Replacement[0]);
    }

    /**
     * Localize a message from a given key and an {@link MessageReceivedEvent} to resolve the {@link java.util.Locale} from
     * <p>
     * This method allows using {@link Replacement}s.
     *
     * @param key          the key of the message in the {@link java.util.ResourceBundle}
     * @param event        the {@link MessageReceivedEvent} to resolve the {@link java.util.Locale} from
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message
     */
    default String localize(String key, MessageReceivedEvent event, Replacement... replacements) {
        if (event.isFromGuild()) {
            return localize(key, event.getGuild(), replacements);
        }
        return localize(key, replacements);
    }

    /**
     * Localize a message from a given key and an {@link GuildChannel} to resolve the {@link java.util.Locale} from
     *
     * @param key     the key of the message in the {@link java.util.ResourceBundle}
     * @param channel the {@link GuildChannel} to resolve the {@link java.util.Locale} from
     * @return the localized message
     */
    default String localize(String key, GuildChannel channel) {
        return localize(key, channel, new Replacement[0]);
    }

    /**
     * Localize a message from a given key and an {@link GuildChannel} to resolve the {@link java.util.Locale} from
     * <p>
     * This method allows using {@link Replacement}s.
     *
     * @param key          the key of the message in the {@link java.util.ResourceBundle}
     * @param channel      the {@link GuildChannel} to resolve the {@link java.util.Locale} from
     * @param replacements an array (or varargs) of {@link Replacement}s to apply on the localized message
     * @return the localized message
     */
    default String localize(String key, GuildChannel channel, Replacement... replacements) {
        return localize(key, channel.getGuild(), replacements);
    }

    @NotNull
    @Override
    default Map<DiscordLocale, String> apply(@NotNull String localizationKey) {
        return new PrefixedLocalizationFunction(this).apply(localizationKey);
    }
}
